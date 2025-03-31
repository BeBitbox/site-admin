package be.bitbox.site.admin.service;

import be.bitbox.site.admin.config.GitHubConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GitHubService {

  private final GitHubConfig gitHubConfig;
  private final RestTemplate restTemplate;

  public GitHubService(GitHubConfig gitHubConfig) {
    this.gitHubConfig = gitHubConfig;
    restTemplate = new RestTemplate();
  }

  public void triggerGitHubAction() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(gitHubConfig.githubToken());

    Map<String, Object> body = new HashMap<>();
    body.put("ref", "main"); // or your branch

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
    restTemplate.exchange(gitHubConfig.getWorkflowDispatchURL(), HttpMethod.POST, request, String.class);
  }

  public int logRunningWorkflowRuns() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(gitHubConfig.githubToken());

    HttpEntity<Void> request = new HttpEntity<>(headers);
    ResponseEntity<Map> response = restTemplate.exchange(gitHubConfig.getRunnersURL(), HttpMethod.GET, request, Map.class);

    List<Map<String, Object>> runs = (List<Map<String, Object>>) response.getBody().get("workflow_runs");

    int counter = 0;
    for (Map<String, Object> run : runs) {
      String status = (String) run.get("status");
      if ("in_progress".equals(status) || "queued".equals(status)) {
        counter++;
      }
    }
    return counter;
  }
}
