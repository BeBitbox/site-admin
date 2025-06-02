package be.bitbox.site.admin.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

@Service
public class SESService {

  private final SesV2Client sesClient;

  public SESService() {
    this.sesClient = SesV2Client.builder()
        .region(Region.EU_WEST_3)
        .build();
  }

  public void sendEventInvitation(String id, String email) {
    String subject = "🎉 Uitnodiging voor vlaanderen.click!";
    String htmlBody = """
            
            
            """;

    Destination destination = Destination.builder()
        .toAddresses(email)
        .build();

    Content contentSubject = Content.builder()
        .data(subject)
        .build();

    Content contentHtml = Content.builder()
        .data(htmlBody)
        .build();

    Body body = Body.builder()
        .html(contentHtml)
        .build();

    Message message = Message.builder()
        .subject(contentSubject)
        .body(body)
        .build();

    EmailContent emailContent = EmailContent.builder()
        .simple(message)
        .build();

    SendEmailRequest request = SendEmailRequest.builder()
        .destination(destination)
        .content(emailContent)
        .fromEmailAddress("noreply@vlaanderen.click")
        .build();

    sesClient.sendEmail(request);
  }
}
