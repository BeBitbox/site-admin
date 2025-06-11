package be.bitbox.site.admin;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class UtilTest {

  @Test
  void getLastElementOfUrl() {
    assertThat(Util.getLastElementOfUrl("https://foo.bar/dansen?tof=true")).isEqualTo("dansen?tof=true");
    assertThat(Util.getLastElementOfUrl("https://foo.bar")).isEqualTo("foo.bar");
    assertThat(Util.getLastElementOfUrl("foo")).isEqualTo("foo");
    assertThat(Util.getLastElementOfUrl(null)).isEqualTo("");
  }

  @Test
  void testGenerateRandomId() {
    assertThat(Util.generateRandomId()).hasSize(10);
  }

  @Test
  void testLocalDateTime() {
    var timeString = Util.toLocalDateTimeString(LocalDateTime.of(LocalDate.of(2025, 1, 24), LocalTime.of(3, 4, 55)));

    assertThat(timeString).isEqualTo("24-01-2025 03:04:55");
  }
}