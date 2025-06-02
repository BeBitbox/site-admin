package be.bitbox.site.admin.service;

import be.bitbox.site.admin.Util;
import be.bitbox.site.admin.controller.VlaanderenClick.VlaanderenClickRegister;
import be.bitbox.site.admin.controller.VlaanderenClick.VlaanderenClickStap2;
import be.bitbox.site.admin.model.UserClick;
import be.bitbox.site.admin.persistance.UserClickDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserCollector {

  private static final Logger log = LoggerFactory.getLogger(UserCollector.class);
  private final UserClickDAO userClickDAO;

  public UserCollector(UserClickDAO userClickDAO) {
    this.userClickDAO = userClickDAO;
  }

  public void insert(String emailToSend) {
    var userClicks = userClickDAO.getUserClicksFromCSV();

    var userClickOptional = userClicks.stream()
        .filter(userClick -> userClick.emailId().equals(emailToSend))
        .findAny();
    if (userClickOptional.isPresent()) {
      throw new IllegalArgumentException("User clicks already exists");
    }

    var userClick = new UserClick(Util.generateRandomId(), emailToSend);
    log.info("Inserting user click {} - {}", userClick.id(), userClick.emailId());
    userClicks.add(userClick);
    userClickDAO.saveUserClicksToCSV(userClicks);

    //TODO Sent with SES -> Update mail sent
  }

  public void update(String id, String userAgent, String ip) {
    var userClicks = userClickDAO.getUserClicksFromCSV();

    var userClick = userClicks.stream()
        .filter(click -> click.id().equals(id))
        .findAny()
        .orElseThrow();

    userClick.setUserAgent(userAgent);
    userClick.setIp(ip);
    userClickDAO.saveUserClicksToCSV(userClicks);
  }

  public void updateBetalen(String id) {
    var userClicks = userClickDAO.getUserClicksFromCSV();

    var userClick = userClicks.stream()
        .filter(click -> click.id().equals(id))
        .findAny()
        .orElseThrow();

    userClick.setBetalingAfgehandeld(true);
    userClickDAO.saveUserClicksToCSV(userClicks);
  }

  public void mailOpened(String id) {
    var userClicks = userClickDAO.getUserClicksFromCSV();

    userClicks.stream()
        .filter(click -> click.id().equals(id))
        .findAny()
        .ifPresent(userClick -> {
          userClick.setMailOpened(true);
        });

    userClickDAO.saveUserClicksToCSV(userClicks);
  }

  public void update(VlaanderenClickRegister vlaanderenClickRegister) {
    var userClicks = userClickDAO.getUserClicksFromCSV();

    var userClick = userClicks.stream()
        .filter(click -> click.id().equals(vlaanderenClickRegister.id()))
        .findAny()
        .orElseThrow();

    userClick.setVoornaam(vlaanderenClickRegister.voornaam());
    userClick.setAchternaam(vlaanderenClickRegister.achternaam());
    userClick.setEmail(vlaanderenClickRegister.email());
    userClick.setDienst(vlaanderenClickRegister.dienst());

    userClickDAO.saveUserClicksToCSV(userClicks);
  }

  public void update(VlaanderenClickStap2 vlaanderenClickStap2) {
    var userClicks = userClickDAO.getUserClicksFromCSV();

    var userClick = userClicks.stream()
        .filter(click -> click.id().equals(vlaanderenClickStap2.id()))
        .findAny()
        .orElseThrow();

    userClick.setBadgeNummer(vlaanderenClickStap2.badgeNummer());
    userClick.setTelefoonNummer(vlaanderenClickStap2.telefoonNummer());

    userClickDAO.saveUserClicksToCSV(userClicks);
  }
}
