package github.antmonitor.config;

import github.antmonitor.worker.WorkerLast1hRule;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class RulesConfig {

  private final List<WorkerLast1hRule> rules;

  RulesConfig() {
    this.rules = new ArrayList<>();
  }

  public List<WorkerLast1hRule> getRules() {
    return rules;
  }
}
