package be.bitbox.site.admin.service;

import be.bitbox.site.admin.Util;
import be.bitbox.site.admin.controller.VlaanderenClick.VlaanderenClickRegister;
import be.bitbox.site.admin.model.UserClick;
import be.bitbox.site.admin.persistance.UserClickDAO;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
  private final ExecutorService executor = Executors.newFixedThreadPool(1); // Tune thread pool size as needed

  public UserCollector(UserClickDAO userClickDAO, SESService sesService) {
    this.userClickDAO = userClickDAO;
    this.sesService = sesService;
  }

  public synchronized void insert(String emailsToSend) {
    var emails = emailsToSend.trim().split(EMAIL_DELIMITER);

    var userClicks = userClickDAO.getUserClicksFromCSV();

    executor.submit(() -> {  //Run async
      for (String emailToSend : emails) {
        var userClickOptional = userClicks.stream()
            .filter(userClick -> userClick.emailId().equals(emailToSend))
            .findAny();
        if (userClickOptional.isPresent()) {
          log.info("Email already exists: {}", emailToSend);
        } else {
          try {
            Thread.sleep(5000);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }

          var userClick = new UserClick(Util.generateRandomId(), emailToSend);
          log.info("Inserting user click {} - {}", userClick.id(), userClick.emailId());
          send(userClick);
        }
      }
    });
  }

  private synchronized void send(UserClick userClick) {
    var userClicks = userClickDAO.getUserClicksFromCSV();
    userClicks.add(userClick);
    userClickDAO.saveUserClicksToCSV(userClicks);

    sesService.sendEventInvitation(userClick.id(), userClick.emailId());
    userClick.setSent(true);
    userClickDAO.saveUserClicksToCSV(userClicks);
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

  public synchronized void mailOpened(String id) {
    update(id, UserClick::incrementMailOpened);
  }

  public synchronized void websiteLoaded(String id, boolean isResidentialIP) {
    update(id, userClick -> {
      if (isResidentialIP) {
        userClick.incrementPageOpenedUser();
      } else {
        userClick.incrementPageOpenedScanner();
      }
    });
  }

  public synchronized void step1(VlaanderenClickRegister vlaanderenClickRegister) {
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

  public synchronized void step2(String id) {
    update(id, userClick -> userClick.setStap2(true));
  }

  public synchronized void step3(String id) {
    update(id, userClick -> userClick.setStap3(true));
  }
}
