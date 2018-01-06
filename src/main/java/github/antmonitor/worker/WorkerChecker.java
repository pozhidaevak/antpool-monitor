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
    this(notifier, rulesConfig.getRules());
  }

  /**
   * @param notifier notifier to which messages will be sent
   * @param rules List of rules that will be checked
   */
  public WorkerChecker(INotifier notifier, List<? extends IWorkerRule> rules) {
    this.rules = rules;
    this.notifier = notifier;
  }

  /**
   * Checks all rules against a map that represent current state of workers
   *
   * @param workers Map of workers what will be checked against rules.
   */
  public void checkAll(Map<String, Worker> workers) {
    for (IWorkerRule rule : rules) {
      String alert = rule.alert(workers);
      if (alert != null) {
        notifier.send(alert);
      }
    }
  }
}
