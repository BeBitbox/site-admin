package be.bitbox.site.admin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPChecker {

  private static final Logger log = LoggerFactory.getLogger(IPChecker.class);

  public static boolean isMicrosoftIp(String ip)  {
    String clientRequestId = UUID.randomUUID().toString();
    String url = "https://endpoints.office.com/endpoints/worldwide?clientrequestid=" + clientRequestId;

    try {
      HttpResponse<String> response;
      try (HttpClient client = HttpClient.newHttpClient()) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
      }

      ObjectMapper mapper = new ObjectMapper();
      JsonNode root = mapper.readTree(response.body());

      List<String> cidrs = new ArrayList<>();

      for (JsonNode entry : root) {
        if (entry.has("ips")) {
          for (JsonNode ipBlock : entry.get("ips")) {
            cidrs.add(ipBlock.asText());
          }
        }
      }

      for (String cidr : cidrs) {
        if (isIpInCidr(ip, cidr)) {
          return true;
        }
      }
    } catch (Exception e) {
      log.error("Error retrieving IP-address", e);
    }

    return false;
  }

  public static boolean isResidentialIP(String ip)  {
    String url = "https://ipapi.co/" + ip + "/json/";

    try {
      HttpResponse<String> response;
      try (HttpClient client = HttpClient.newHttpClient()) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
      }

      ObjectMapper mapper = new ObjectMapper();
      JsonNode node = mapper.readTree(response.body());

      String org = node.has("org") ? node.get("org").asText().toLowerCase() : "";
      String asn = node.has("asn") ? node.get("asn").asText().toLowerCase() : "";
      log.info("Checking IP: org: {}, asn: {} ", org, asn);

      // Heuristic check
      return !(org.contains("amazon") || org.contains("microsoft") || org.contains("google") ||
          org.contains("digitalocean") || org.contains("cloudflare") || org.contains("ovh") ||
          asn.contains("amazon") || asn.contains("azure") || asn.contains("cloud"));
    } catch (Exception e) {
      log.error("Error retrieving residential IP-address", e);
      return false;
    }
  }

  // Simplified CIDR matcher
  private static boolean isIpInCidr(String ip, String cidr) {
    try {
      String[] parts = cidr.split("/");
      InetAddress inetAddress = InetAddress.getByName(parts[0]);
      InetAddress targetAddress = InetAddress.getByName(ip);
      int prefixLength = Integer.parseInt(parts[1]);

      byte[] cidrBytes = inetAddress.getAddress();
      byte[] ipBytes = targetAddress.getAddress();

      int fullBytes = prefixLength / 8;
      int remainingBits = prefixLength % 8;

      for (int i = 0; i < fullBytes; i++) {
        if (cidrBytes[i] != ipBytes[i]) return false;
      }

      if (remainingBits > 0) {
        int mask = ((1 << remainingBits) - 1) << (8 - remainingBits);
        if ((cidrBytes[fullBytes] & mask) != (ipBytes[fullBytes] & mask)) return false;
      }

      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
