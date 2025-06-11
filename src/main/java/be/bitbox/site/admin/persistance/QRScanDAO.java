package be.bitbox.site.admin.persistance;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import be.bitbox.site.admin.model.QRScan;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QRScanDAO {

  private static final String FILE_NAME = "qrs.csv";
  private static final Logger log = LoggerFactory.getLogger(QRScanDAO.class);

  private final String path;

  public QRScanDAO(@Value("${application.user.file.location}") String path) {
    this.path = path;
    initializeFile();
  }

  private void initializeFile() {
    var fileName = getFileName();
    if (exists(get(fileName))) {
      log.info("CSV file {} already exists", fileName);
    } else {
      checkFileExists();
    }
  }

  public void checkFileExists() {
    var fileName = getFileName();
    if (!exists(get(fileName))) {
      try (FileWriter writer = new FileWriter(fileName)) {
        writer.append(QRScan.fieldNames()).append(System.lineSeparator());
      } catch (IOException e) {
        throw new RuntimeException("Failed to write Qr-scans to CSV", e);
      }
    }
  }

  public void appendToFile(QRScan qrScan) {
    var fileName = getFileName();
    log.info("Adding QRs to csv file {}", fileName);

    try (FileWriter writer = new FileWriter(fileName, true)) {
      writer.write(qrScan.toString());
      writer.write(System.lineSeparator());
    } catch (IOException e) {
      System.err.println("Error appending to CSV file: " + e.getMessage());
    }
  }

  private String getFileName() {
    return path + File.separator + FILE_NAME;
  }

  public byte[] getCSVFileAsBytes() {
    var fileName = getFileName();
    log.info("Converting csv file {} to byte array", fileName);

    try {
      return readAllBytes(get(fileName));
    } catch (IOException e) {
      throw new RuntimeException("Failed to read the CSV file as bytes", e);
    }
  }
}
