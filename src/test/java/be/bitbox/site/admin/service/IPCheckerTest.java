package be.bitbox.site.admin.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class IPCheckerTest {

  @Test
  void isMicrosoftIp() {
    assertThat(IPChecker.isMicrosoftIp("72.145.152.65")).isFalse();
  }

  @Test
  void isResidentialIP() {
    assertThat(IPChecker.isResidentialIP("72.145.152.65")).isFalse();
    assertThat(IPChecker.isResidentialIP("213.52.242.151")).isTrue();
  }
}