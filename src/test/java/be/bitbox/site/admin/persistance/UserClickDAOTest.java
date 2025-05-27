package be.bitbox.site.admin.persistance;

import static org.assertj.core.api.Assertions.assertThat;

import be.bitbox.site.admin.model.UserClick;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class UserClickDAOTest {

  private UserClickDAO userClickDAO;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    userClickDAO = new UserClickDAO(tempDir.toString());
  }

  @Test
  void saveAndGetUserClicks_ShouldPreserveAllFields() {
    // Given
    var singleUserClick = createSingleUserClick();
    var dummyUserClick = createDummyUserClick();

    // When
    userClickDAO.saveUserClicksToCSV(List.of(singleUserClick, dummyUserClick));
    List<UserClick> loadedClicks = userClickDAO.getUserClicksFromCSV();

    // Then
    assertThat(loadedClicks).hasSize(2);

    UserClick loadedSingleClick = loadedClicks.get(0);
    assertThat(loadedSingleClick.id()).isEqualTo(singleUserClick.id());
    assertThat(loadedSingleClick.emailId()).isEqualTo(singleUserClick.emailId());
    assertThat(loadedSingleClick.userAgent()).isEqualTo(singleUserClick.userAgent());
    assertThat(loadedSingleClick.ip()).isEqualTo(singleUserClick.ip());
    assertThat(loadedSingleClick.voornaam()).isEqualTo(singleUserClick.voornaam());
    assertThat(loadedSingleClick.achternaam()).isEqualTo(singleUserClick.achternaam());
    assertThat(loadedSingleClick.email()).isEqualTo(singleUserClick.email());
    assertThat(loadedSingleClick.dienst()).isEqualTo(singleUserClick.dienst());
    assertThat(loadedSingleClick.badgeNummer()).isEqualTo(singleUserClick.badgeNummer());
    assertThat(loadedSingleClick.telefoonnummer()).isEqualTo(singleUserClick.telefoonnummer());

    UserClick loadedDummyClick = loadedClicks.get(1);
    assertThat(loadedDummyClick.id()).isEqualTo(dummyUserClick.id());
    assertThat(loadedDummyClick.emailId()).isEqualTo(dummyUserClick.emailId());
  }

  private UserClick createSingleUserClick() {
    var userClick = new UserClick("test-id", "email-id");
    userClick.setUserAgent("Agent 47");
    userClick.setIp("1.2.3.4");
    userClick.setVoornaam("John");
    userClick.setAchternaam("Doe");
    userClick.setEmail("john.doe@example.com");
    userClick.setDienst("Test Service");
    userClick.setBadgeNummer("B12345");
    userClick.setTelefoonnummer("0123456789");
    return userClick;
  }

  private UserClick createDummyUserClick() {
    return new UserClick("dummy-id", "email@dummy.be");
  }
}