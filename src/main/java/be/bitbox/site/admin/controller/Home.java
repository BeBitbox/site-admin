package be.bitbox.site.admin.controller;

import be.bitbox.site.admin.model.SiteData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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

    @GetMapping("/")
    public String hello(Model model) {
        try (var s3ObjectInputStream = s3Client.getObject(getObjectRequest)) {
            ObjectMapper objectMapper = new ObjectMapper();
            var data = objectMapper.readValue(s3ObjectInputStream, SiteData.class);

            model.addAttribute("upperTitle", data.getUpperTitle());
            model.addAttribute("underTitle", data.getUnderTitle());
            model.addAttribute("information", data.getInformation());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "hello";
    }
}

