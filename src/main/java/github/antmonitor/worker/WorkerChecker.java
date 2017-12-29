package github.antmonitor.worker;

import github.antmonitor.config.RulesConfig;
import github.antmonitor.notifications.INotifier;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
public class WorkerChecker {


  final private List<? extends IWorkerRule> rules;
  final private INotifier notifier;

  @Autowired
  public WorkerChecker(INotifier notifier, RulesConfig rulesConfig) {
    rules = rulesConfig.getRules();
    this.notifier = notifier;
  }

  public void checkAll(Map<String, Worker> workers) {
    for (IWorkerRule rule : rules) {
      String alert = rule.alert(workers);
      if (alert != null) {
        notifier.send(alert);
      }
    }
  }
}
