package be.bitbox.site.admin.model;

import be.bitbox.site.admin.Util;
import java.time.LocalDateTime;

public record QRScan(LocalDateTime timestamp, String useragent) {

  public static String fieldNames() {
    return "timestamp;useragent";
  }

  @Override
  public String toString() {
    return Util.toLocalDateTimeString(timestamp) + ";" + useragent;
  }
}
