package be.bitbox.site.admin.persistance;

import static java.nio.file.Files.exists;
import static java.nio.file.Paths.get;

import be.bitbox.site.admin.model.UserClick;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserClickDAO {

  private static final String FILE_NAME = "userclicks.csv";
  private static final Logger log = LoggerFactory.getLogger(UserClickDAO.class);

  private final String path;

  public UserClickDAO(@Value("${application.user.file.location}") String path) {
    this.path = path;
  }

  public void saveUserClicksToCSV(List<UserClick> userClicks) {
    var fileName = getFileName();
    log.info("Saving user clicks to csv file {}", fileName);
    try (FileWriter writer = new FileWriter(fileName)) {
      for (UserClick userClick : userClicks) {
        writer.append(userClick.toString())
            .append(System.lineSeparator());
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to write user clicks to CSV", e);
    }
  }

  private String getFileName() {
    return path + File.separator + FILE_NAME;
  }

  public List<UserClick> getUserClicksFromCSV() {
    List<UserClick> userClicks = new ArrayList<>();
    var fileName = getFileName();
    log.info("Loading user clicks from csv file {}", fileName);

    if (!exists(get(fileName))) {
      return userClicks;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (!line.isBlank()) {
          userClicks.add(UserClick.fromString(line.trim()));
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to read user clicks from CSV", e);
    }
    return userClicks;
  }
}
