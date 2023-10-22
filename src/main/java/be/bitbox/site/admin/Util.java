package be.bitbox.site.admin;

public class Util {

    public static String getLastElementOfUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }

        String[] parts = url.split("/");
        return parts.length > 0 ? parts[parts.length - 1] : "";
    }
}
