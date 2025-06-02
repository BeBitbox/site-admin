package be.bitbox.site.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger log = LoggerFactory.getLogger(SESService.class);
  private final SesV2Client sesClient;

  public SESService() {
    this.sesClient = SesV2Client.builder()
        .region(Region.EU_WEST_3)
        .build();
  }

  public void sendEventInvitation(String id, String email) {
    String subject = "🎉 Uitnodiging voor de zomerbar!";
    String htmlBody = """
        <html>
          <head>
            <style>
              @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap');
              body {
                margin: 0;
                padding: 0;
                background-color: #f0f4f8;
                font-family: 'Inter', sans-serif;
                color: #333333;
              }
              .container {
                max-width: 600px;
                margin: auto;
                background-color: #ffffff;
                border-radius: 10px;
                overflow: hidden;
                box-shadow: 0 0 15px rgba(0,0,0,0.1);
              }
              .header {
                background-color: #9333ea;
                padding: 30px;
                text-align: center;
                color: white;
              }
              .header img {
                max-width: 100px;
                margin-bottom: 10px;
              }
              .content {
                padding: 30px;
              }
              h1 {
                margin-top: 0;
                color: #111827;
              }
              .cta-button {
                display: inline-block;
                background-color: #9333ea;
                color: white;
                padding: 12px 24px;
                border-radius: 8px;
                text-decoration: none;
                font-weight: 600;
                margin-top: 20px;
              }
              .footer {
                font-size: 12px;
                color: #888888;
                text-align: center;
                padding: 20px;
              }
              .snail {
                margin-top: 30px;
                text-align: center;
              }
              .snail img {
                max-width: 60px;
                opacity: 0.6;
              }
              .hide {
                display: none;
              }
            </style>
          </head>
          <body>
            <div class="container">
              <div class="header">
                <img src="https://www.vlaanderen.be/img/logo/vlaanderen-logo.svg" alt="vlaanderen.click logo" />
                <h1>Gratis cocktail in de zomerbar!</h1>
              </div>
              <div class="content">
                <p>Beste deelnemer,</p>
                <p>We zijn verheugd om je uit te nodigen voor de allereerste zomerbar van <strong>vlaanderen.click</strong> in de laatste week van juni.
                We kunnen hierbij gebruik maken van het uniek dakterras van de Belpaire.
                </p>
                <p>Gebruik onderstaande knop om jouw plaats te bevestigen en uw cocktail te reserveren:</p>
                <a href="https://vlaanderen.click/$$ID$$/registreer" class="cta-button">Registreer nu</a>
                <p>We zullen alvast uw cocktail koel zetten. Tot dan! 🎉</p>
                <div class="snail">
                  <img src="https://vlaanderen.click/logo.png" alt="slakje" />
                </div>
                <div class="hide">
                  <img src="https://administratie.site/vlaanderen.click/pixel/$$ID$$" width="1px" height="1px" alt="pixel" />
                </div>
        </div>
        <div class="footer">
          © 2025 vlaanderen.click · Gemaakt met 💜 in Vlaanderen
              </div>
            </div>
          </body>
        </html>
        """;

    htmlBody = htmlBody.replace("$$ID$$", id);

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
        .text(Content.builder().data(subject).build())
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

    var sendEmailResponse = sesClient.sendEmail(request);
    log.info("Mail verzonden: {}", sendEmailResponse);
  }
}
