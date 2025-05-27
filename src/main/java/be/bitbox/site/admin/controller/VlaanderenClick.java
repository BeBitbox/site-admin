package be.bitbox.site.admin.controller;

import be.bitbox.site.admin.service.UserCollector;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VlaanderenClick {

  private static final Logger log = LoggerFactory.getLogger(VlaanderenClick.class);
  private final UserCollector userCollector;

  public VlaanderenClick(UserCollector userCollector) {
    this.userCollector = userCollector;
  }

  @PostMapping("/vlaanderen.click/add")
  public void addVlaanderenClick(@RequestBody VlaanderenClickId id) {
    log.info("Adding VlaanderenClickMail {}", id);
    userCollector.insert(id.id());
  }

  @PostMapping("/vlaanderen.click/init")
  public void initVlaanderenClick(@RequestBody VlaanderenClickId id, HttpServletRequest request) {
    log.info("VlaanderenClickMail init {}", id);

    var userAgent = request.getHeader("User-Agent");
    var clientIp = request.getRemoteAddr();
    userCollector.update(id.id, userAgent, clientIp);
  }

  @PostMapping("/vlaanderen.click/register")
  public void registerVlaanderenClick(@RequestBody VlaanderenClickRegister vlaanderenClickRegister) {
    log.info("Received click for {}", vlaanderenClickRegister);

    userCollector.update(vlaanderenClickRegister);
  }

  public record VlaanderenClickRegister(String id, String voornaam, String achternaam, String email, String dienst) {

  }

  private record VlaanderenClickId(String id) {

  }
}
