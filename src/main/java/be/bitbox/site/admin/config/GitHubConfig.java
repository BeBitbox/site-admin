package be.bitbox.site.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitHubConfig {

  @Value("${github.token}")
  private String githubToken;

  @Value("${github.repo}")
  private String repo;

  @Value("${github.owner}")
  private String owner;

  @Value("${github.workflow}")
  private String workflow;

  public String getWorkflowDispatchURL() {
    return String.format("https://api.github.com/repos/%s/%s/actions/workflows/%s/dispatches", owner, repo, workflow);
  }

  public String getRunnersURL() {
    return String.format("https://api.github.com/repos/%s/%s/actions/workflows/%s/runs", owner, repo, workflow);
  }

  public String githubToken() {
    return githubToken;
  }

  public String repo() {
    return repo;
  }

  public String owner() {
    return owner;
  }

  public String workflow() {
    return workflow;
  }
}
