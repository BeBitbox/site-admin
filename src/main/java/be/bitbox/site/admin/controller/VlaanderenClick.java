package be.bitbox.site.admin.controller;

import be.bitbox.site.admin.model.QRScan;
import be.bitbox.site.admin.persistance.QRScanDAO;
import be.bitbox.site.admin.service.IPChecker;
import be.bitbox.site.admin.service.UserCollector;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VlaanderenClick {

  public static final byte[] TRANSPARENT_PIXEL = new byte[]{
      (byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38, (byte) 0x39, (byte) 0x61, (byte) 0x01, (byte) 0x00,
      (byte) 0x01, (byte) 0x00, (byte) 0x80, (byte) 0xff, (byte) 0x00, (byte) 0xff, (byte) 0xff, (byte) 0xff,
      (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x2c, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
      (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x02, (byte) 0x4c,
      (byte) 0x01, (byte) 0x00, (byte) 0x3b
  };
  private static final Logger log = LoggerFactory.getLogger(VlaanderenClick.class);
  private final UserCollector userCollector;
  private final QRScanDAO qrScanDAO;

  public VlaanderenClick(UserCollector userCollector, QRScanDAO qrScanDAO) {
    this.userCollector = userCollector;
    this.qrScanDAO = qrScanDAO;
  }

  @GetMapping("/vlaanderen.click/qr")
  public void initVlaanderenClick(HttpServletRequest request) {
    log.info("VlaanderenClickMail qr");

    var userAgent = request.getHeader("User-Agent");
    qrScanDAO.appendToFile(new QRScan(LocalDateTime.now(), userAgent));
  }

  @PostMapping("/vlaanderen.click/add")
  public void addVlaanderenClick(@RequestBody VlaanderenClickId id) {
    log.info("Adding VlaanderenClickMail {}", id);
    userCollector.insert(id.id());
  }

  @PostMapping("/vlaanderen.click/init")
  public void initVlaanderenClick(@RequestBody VlaanderenClickId id, HttpServletRequest request) {
    var isResidentialIP = IPChecker.isResidentialIP(request.getRemoteAddr());
    log.debug("request.getRemoteAddr(): {}", request.getRemoteAddr());
    log.info("VlaanderenClickMail init {} with residential {}", id, isResidentialIP);
    userCollector.websiteLoaded(id.id, isResidentialIP);
  }

  @PostMapping("/vlaanderen.click/register")
  public void registerVlaanderenClick(@RequestBody VlaanderenClickRegister vlaanderenClickRegister) {
    log.info("Received register for {}", vlaanderenClickRegister);

    userCollector.step1(vlaanderenClickRegister);
  }

  @PostMapping("/vlaanderen.click/stap2")
  public void stap2VlaanderenClick(@RequestBody VlaanderenClickId id) {
    log.info("Received stap2 for {}", id);

    userCollector.step2(id.id);
  }

  @PostMapping("/vlaanderen.click/betalen")
  public void betalen(@RequestBody VlaanderenClickId id) {
    log.info("Received betalen for {}", id);
    userCollector.step3(id.id);
  }

  @GetMapping("/vlaanderen.click/pixel/{id}")
  public void downloadPixel(@PathVariable String id, HttpServletResponse response) {
    log.info("Downloading pixel with id: {}", id);

    userCollector.mailOpened(id);

    try {
      // Set response headers to deliver the pixel image
      response.setContentType("image/gif");
      response.setContentLength(TRANSPARENT_PIXEL.length);

      // Write the pixel bytes to the response's output stream
      response.getOutputStream().write(TRANSPARENT_PIXEL);
      response.getOutputStream().flush();
    } catch (IOException e) {
      log.error("Error sending pixel data", e);
    }
  }

  public record VlaanderenClickRegister(String id, String voornaam, String achternaam, String email, String functie, String organisatie, String telefoon) {

  }

  public record VlaanderenClickId(String id) {

  }
}
