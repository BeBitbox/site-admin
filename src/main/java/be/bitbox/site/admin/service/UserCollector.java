package be.bitbox.site.admin.service;

import be.bitbox.site.admin.Util;
import be.bitbox.site.admin.controller.VlaanderenClick.VlaanderenClickRegister;
import be.bitbox.site.admin.model.UserClick;
import be.bitbox.site.admin.persistance.UserClickDAO;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserCollector {

  private static final Logger log = LoggerFactory.getLogger(UserCollector.class);
  public static final String EMAIL_DELIMITER = ";";
  private final UserClickDAO userClickDAO;
  private final SESService sesService;

  public UserCollector(UserClickDAO userClickDAO, SESService sesService) {
    this.userClickDAO = userClickDAO;
    this.sesService = sesService;
  }

  public void insert(String emailsToSend) {
    var emails = emailsToSend.trim().split(EMAIL_DELIMITER);

    var userClicks = userClickDAO.getUserClicksFromCSV();

    for (String emailToSend : emails) {
      var userClickOptional = userClicks.stream()
          .filter(userClick -> userClick.emailId().equals(emailToSend))
          .findAny();
      if (userClickOptional.isPresent()) {
        log.info("Email already exists: {}", emailToSend);
      } else {
        var userClick = new UserClick(Util.generateRandomId(), emailToSend);
        log.info("Inserting user click {} - {}", userClick.id(), userClick.emailId());
        userClicks.add(userClick);
        userClickDAO.saveUserClicksToCSV(userClicks);

        sesService.sendEventInvitation(userClick.id(), userClick.emailId());
        userClick.setSent(true);
        userClickDAO.saveUserClicksToCSV(userClicks);
      }
    }
  }

  private void update(String id, Consumer<UserClick> consumer) {
    var userClicks = userClickDAO.getUserClicksFromCSV();

    var userClick = userClicks.stream()
        .filter(click -> click.id().equals(id))
        .findAny()
        .orElseThrow();

    consumer.accept(userClick);

    userClickDAO.saveUserClicksToCSV(userClicks);
  }

  public void mailOpened(String id) {
    update(id, UserClick::incrementMailOpened);
  }

  public void websiteLoaded(String id) {
    update(id, userClick -> userClick.setWebpageVisited(true));
  }

  public void step1(VlaanderenClickRegister vlaanderenClickRegister) {
    update(vlaanderenClickRegister.id(), userClick -> {
      userClick.setVoornaam(vlaanderenClickRegister.voornaam());
      userClick.setAchternaam(vlaanderenClickRegister.achternaam());
      userClick.setEmail(vlaanderenClickRegister.email());
      userClick.setFunctie(vlaanderenClickRegister.functie());
      userClick.setOrganisatie(vlaanderenClickRegister.organisatie());
      userClick.setTelefoonNummer(vlaanderenClickRegister.telefoon());
      userClick.setStap1(true);
    });
  }

  public void step2(String id) {
    update(id, userClick -> userClick.setStap2(true));
  }

  public void step3(String id) {
    update(id, userClick -> userClick.setStap3(true));
  }
}
