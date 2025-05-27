package be.bitbox.site.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VlaanderenClick {

  private static final Logger log = LoggerFactory.getLogger(VlaanderenClick.class);

  @PostMapping("/vlaanderen.click/register")
  public void registerVlaanderenClick(@RequestBody VlaanderenClickRequest vlaanderenClickRequest) {
    log.info("Received click for {}", vlaanderenClickRequest);
  }

  private record VlaanderenClickRequest(String id, String voornaam, String achternaam, String email, String dienst) { }
}
