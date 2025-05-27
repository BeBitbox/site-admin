package be.bitbox.site.admin;

import java.util.Random;

public class Util {

  public static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  public static String getLastElementOfUrl(String url) {
    if (url == null || url.isEmpty()) {
      return "";
    }

    String[] parts = url.split("/");
    return parts.length > 0 ? parts[parts.length - 1] : "";
  }

  public static String generateRandomId() {
    var randomString = new StringBuilder();
    var random = new Random();

    for (int i = 0; i < 10; i++) {
      randomString.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
    }

    return randomString.toString();
  }
}
