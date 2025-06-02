package be.bitbox.site.admin.controller;

import be.bitbox.site.admin.Util;
import be.bitbox.site.admin.config.SecurityConfig;
import be.bitbox.site.admin.model.Nest;
import be.bitbox.site.admin.model.SiteData;
import be.bitbox.site.admin.model.TextBlock;
import be.bitbox.site.admin.persistance.UserClickDAO;
import be.bitbox.site.admin.service.GitHubService;
import be.bitbox.site.admin.service.S3Service;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@EnableMethodSecurity
public class Home {
    private static final Logger LOG = LoggerFactory.getLogger(Home.class);

    private final S3Service s3Service;
    private final GitHubService gitHubService;
    private final SecurityConfig securityConfig;
    private final UserClickDAO userClickDAO;
    private SaveResult saveResult = SaveResult.NONE;

    public Home(S3Service s3Service, GitHubService gitHubService, SecurityConfig securityConfig, UserClickDAO userClickDAO) {
        this.s3Service = s3Service;
        this.gitHubService = gitHubService;
        this.securityConfig = securityConfig;
      this.userClickDAO = userClickDAO;
    }

    @GetMapping("/")
    public String startPage(Model model) {
        model.addAttribute("meulemeershoeve", securityConfig.isMeulemeershoeveUser());
        model.addAttribute("vlaanderenclick", securityConfig.isVlaanderenClickUser());
        return "index";
    }

    @GetMapping("/meulemeershoeve")
    public String meulemeershoeve(Model model) {
        var deploy = isGitHubDeploying();

        model.addAttribute("deploy", deploy ? "deploying" : "not deploying");
        model.addAttribute("success", saveResult.name());
        saveResult = SaveResult.NONE;
        return "meulemeershoeve";
    }

    @GetMapping("/phisher")
    public String showPhisherPage() {
        return "phisher";
    }

    @GetMapping("/downloadUserClickData")
    public ResponseEntity<byte[]> downloadUserClickData() {
        String csvFileName = "user_click_data.csv";
        byte[] csvData;

        try {
            csvData = userClickDAO.getCSVFileAsBytes();
        } catch (Exception e) {
            LOG.error("Error fetching the CSV file: {}", csvFileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
            .filename(csvFileName)
            .build());

        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }

    private boolean isGitHubDeploying() {
        var deploy = false;
        try {
            deploy = gitHubService.logRunningWorkflowRuns() > 0;
        } catch (Exception e) {
            LOG.error("Error triggering GitHub", e);
        }
        if (deploy) {
            LOG.info("currently running workflow runs");
        }
        return deploy;
    }

    @GetMapping("/textblock")
    public String hello(Model model, @RequestParam(value = "item") String item) {
        var siteData = s3Service.readSiteData();
        var data = getTextBlock(item, siteData);

        model.addAttribute("textblock", data);
        return "textblock";
    }

    @PostMapping("/submittextblock")
    public String doCreateUser(@ModelAttribute("textblock") TextBlock textBlock,
                               @RequestParam(value = "item") String item,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            LOG.error("ERROR {}", bindingResult);
            saveResult = SaveResult.FAILED;
            return "redirect:/meulemeershoeve";
        }

        var siteData = s3Service.readSiteData();
        TextBlock data = getTextBlock(item, siteData);

        data.setText(textBlock.getText());
        data.setShowMobile(textBlock.isShowMobile());

        deployWebsite(siteData);
        saveResult = SaveResult.SUCCESS;
        return "redirect:/meulemeershoeve";
    }

    @GetMapping("/upload")
    public String uploadPage(Model model) {
        var siteData = s3Service.readSiteData();

        Map<String, String> imageMap = siteData.getFotos().stream().collect(Collectors.toMap(
                imageURL -> imageURL.substring(imageURL.lastIndexOf('/') + 1),      // Filename as key
                imageURL -> imageURL                                                            // Full URL as value
        ));
        model.addAttribute("imageMap", imageMap);
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        boolean success = s3Service.uploadFile(file);

        if (success) {
            var siteData = s3Service.readSiteData();
            siteData.getFotos().add("https://s3.eu-west-3.amazonaws.com/be.meulemeershoeve/images/" + file.getOriginalFilename());
            deployWebsite(siteData);
            redirectAttributes.addFlashAttribute("message", "Foto opgeladen " + file.getOriginalFilename());
        } else {
            redirectAttributes.addFlashAttribute("message", "Foto werd niet opgeladen: " + file.getOriginalFilename());
        }

        return "redirect:/upload";
    }

    @GetMapping("/delete/{filename}")
    public String deleteFile(@PathVariable String filename, RedirectAttributes redirectAttributes) {
        boolean success = s3Service.deleteFile(filename);

        var siteData = s3Service.readSiteData();
        List<String> fotos = siteData.getFotos().stream()
                .filter(url -> !url.endsWith(filename))
                .toList();

        siteData.setFotos(fotos);
        deployWebsite(siteData);

        if (success) {
            redirectAttributes.addFlashAttribute("message", "Successfully deleted " + filename);
        } else {
            redirectAttributes.addFlashAttribute("message", "Failed to delete " + filename);
        }

        return "redirect:/upload";
    }

    @GetMapping("/nest")
    public String uploadNest(Model model) {
        var siteData = s3Service.readSiteData();

        model.addAttribute("nesten", siteData.getNesten());
        return "nest";
    }

    @PostMapping("/nest")
    public String uploadNest(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        boolean success = s3Service.uploadFile(file);

        if (success) {
            var siteData = s3Service.readSiteData();
            var nest = new Nest();
            nest.setImage("https://s3.eu-west-3.amazonaws.com/be.meulemeershoeve/images/" + file.getOriginalFilename());
            nest.setDescription("omschrijving");
            nest.setName("naam");
            nest.setUuid(UUID.randomUUID().toString());
            siteData.getNesten().add(nest);
            deployWebsite(siteData);
            redirectAttributes.addFlashAttribute("message", "Nest gemaakt " + file.getOriginalFilename());
        } else {
            redirectAttributes.addFlashAttribute("message", "Nest werd niet gemaakt: " + file.getOriginalFilename());
        }

        return "redirect:/nest";
    }

    @PostMapping("/updateNest")
    public String updateNest(@ModelAttribute("nest") Nest nest, RedirectAttributes redirectAttributes) {
        var siteData = s3Service.readSiteData();
        var nestToUpdate = siteData.getNesten().stream()
                .filter(n -> nest.getUuid().equals(n.getUuid()))
                .findAny()
                .orElseThrow();
        nestToUpdate.setName(nest.getName());
        nestToUpdate.setDescription(nest.getDescription());

        deployWebsite(siteData);
        redirectAttributes.addFlashAttribute("message", "Nest " + nest.getName() + " bewaard");
        return "redirect:/nest";
    }

    @GetMapping("/deleteNest/{uuid}")
    public String deleteNest(@PathVariable String uuid, RedirectAttributes redirectAttributes) {
        var siteData = s3Service.readSiteData();
        var nestToDelete = siteData.getNesten().stream()
                .filter(nest -> nest.getUuid().equals(uuid))
                .findAny()
                .orElseThrow();

        boolean success = s3Service.deleteFile(Util.getLastElementOfUrl(nestToDelete.getImage()));
        siteData.getNesten().remove(nestToDelete);

        deployWebsite(siteData);

        if (success) {
            redirectAttributes.addFlashAttribute("message", "Verwijderd nest " + nestToDelete.getName());
        } else {
            redirectAttributes.addFlashAttribute("message", "Nest werd niet verwijderd " + nestToDelete.getName());
        }

        return "redirect:/nest";
    }

    @GetMapping("/accessDenied")
    public String accessDenied(Model model, @RequestParam(value = "email") String email) {
        model.addAttribute("email", email);
        return "accessDenied";
    }

    private static TextBlock getTextBlock(String item, SiteData siteData) {
        return switch (item) {
            case "uppertitle" -> siteData.getUpperTitle();
            case "undertitle" -> siteData.getUnderTitle();
            case "vision" -> siteData.getVision();
            case "aboutus" -> siteData.getAboutUs();
            case "goldenretriever" -> siteData.getGoldenRetriever();
            case "bernersennen" -> siteData.getBernerSennen();
            default -> throw new UnsupportedOperationException("I don't know textblock " + item);
        };
    }

    private void deployWebsite(SiteData siteData) {
        s3Service.writeSiteData(siteData);
        try {
            gitHubService.triggerGitHubAction();
            while (!isGitHubDeploying()) {
                Thread.sleep(500);
            }
        } catch (Exception e) {
            LOG.error("Error triggering GitHub", e);
        }
    }

    private enum SaveResult {
        SUCCESS,
        FAILED,
        NONE
    }
}

