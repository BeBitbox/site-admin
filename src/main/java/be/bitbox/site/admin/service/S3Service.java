package be.bitbox.site.admin.service;

import be.bitbox.site.admin.model.SiteData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Service
public class S3Service {
    private final S3Client s3Client;
    private final GetObjectRequest getObjectDataRequest;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PutObjectRequest putObjectDataRequest;
    private static final String BUCKET_NAME = "be.meulemeershoeve";
    private static final Logger LOG = LoggerFactory.getLogger(S3Service.class);

    public S3Service() {
        this.s3Client = S3Client.builder()
                .region(Region.EU_WEST_3)
                .build();
        String key = "data.json";
        getObjectDataRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .build();

        putObjectDataRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .build();
    }

    public SiteData readSiteData() {
        LOG.info("[S3Service] reading siteData");
        try (var s3ObjectInputStream = s3Client.getObject(getObjectDataRequest)) {
            return objectMapper.readValue(s3ObjectInputStream, SiteData.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeSiteData(SiteData siteData) {
        LOG.info("[S3Service] writing siteData");
        try {
            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectDataRequest, RequestBody.fromString(objectMapper.writeValueAsString(siteData)));
            if (!putObjectResponse.sdkHttpResponse().isSuccessful()) {
                LOG.error("Writing sitedata was not successful {}", putObjectResponse.sdkHttpResponse().statusText());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean uploadFile(MultipartFile file) {
        LOG.info("[S3Service] Uploading file {}", file.getOriginalFilename());
        boolean success = false;
        PutObjectRequest putImage = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key("images/" + file.getOriginalFilename())
                .build();
        try {
            PutObjectResponse putObjectResponse = s3Client.putObject(putImage, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            success = putObjectResponse.sdkHttpResponse().isSuccessful();
        } catch (IOException e) {
            LOG.error("error upload file", e);
        }
        return success;
    }

    public boolean deleteFile(String filename) {
        DeleteObjectResponse deleteObjectResponse = s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key("images/" + filename)
                .build());
        return deleteObjectResponse.sdkHttpResponse().isSuccessful();
    }
}
