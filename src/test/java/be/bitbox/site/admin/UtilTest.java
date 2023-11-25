package be.bitbox.site.admin;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UtilTest {

    @Test
    void getLastElementOfUrl() {
        assertThat(Util.getLastElementOfUrl("https://foo.bar/dansen?tof=true")).isEqualTo("dansen?tof=true");
        assertThat(Util.getLastElementOfUrl("https://foo.bar")).isEqualTo("foo.bar");
        assertThat(Util.getLastElementOfUrl("foo")).isEqualTo("foo");
        assertThat(Util.getLastElementOfUrl(null)).isEqualTo("");
    }
}