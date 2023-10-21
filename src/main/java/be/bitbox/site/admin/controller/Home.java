package be.bitbox.site.admin.controller;

import be.bitbox.site.admin.model.SiteData;
import be.bitbox.site.admin.model.TextBlock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Controller
public class Home {

    private final S3Client s3Client;
    private final GetObjectRequest getObjectRequest;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PutObjectRequest putObjectRequest;


    public Home() {
        this.s3Client = S3Client.builder().region(Region.EU_WEST_3).build();
        String bucketName = "be.meulemeershoeve";
        String key = "data.json";

        getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
    }

    @GetMapping("/meulemeershoeve")
    public String index() {
        return "meulemeershoeve";
    }

    @GetMapping("/textblock")
    public String hello(Model model, @RequestParam(value = "item") String item) {
        var siteData = readSiteData();
        TextBlock data = getTextBlock(item, siteData);

        model.addAttribute("textblock", data);
        return "textblock";
    }

    private SiteData readSiteData() {
        try (var s3ObjectInputStream = s3Client.getObject(getObjectRequest)) {
            return objectMapper.readValue(s3ObjectInputStream, SiteData.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/submittextblock")
    public String doCreateUser(@ModelAttribute("textblock") TextBlock textBlock,
                               @RequestParam(value = "item", defaultValue = "uppertitle") String item,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            System.err.println("ERROR " + bindingResult);
            return "redirect:/meulemeershoeve?success=false";
        }

        var siteData = readSiteData();
        TextBlock data = getTextBlock(item, siteData);

        data.setText(textBlock.getText());
        data.setShowMobile(textBlock.isShowMobile());

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromString(objectMapper.writeValueAsString(siteData)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/meulemeershoeve?success=true";
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
}

