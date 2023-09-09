package be.bitbox.site.admin.controller;

import be.bitbox.site.admin.model.SiteData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;

@Controller
public class Home {

    private final S3Client s3Client;
    private final GetObjectRequest getObjectRequest;

    public Home() {
        this.s3Client = S3Client.builder().region(Region.EU_WEST_3).build();
        String bucketName = "be.meulemeershoeve";
        String key = "data.json";

        getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
    }

    @GetMapping("/meulemeershoeve")
    public String hello(Model model) {
        try (var s3ObjectInputStream = s3Client.getObject(getObjectRequest)) {
            ObjectMapper objectMapper = new ObjectMapper();
            var siteData = objectMapper.readValue(s3ObjectInputStream, SiteData.class);

            model.addAttribute("siteData", siteData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "meulemeershoeve";
    }

    @PostMapping("/submit")
    public String doCreateUser(@ModelAttribute("siteData") SiteData siteData,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            System.err.println("ERROR " + bindingResult);
            return "redirect:/?success=false";
        }

        System.out.println(siteData);

        return "redirect:/?success=true";
    }

}

