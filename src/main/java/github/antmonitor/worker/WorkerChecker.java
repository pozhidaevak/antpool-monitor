package github.antmonitor.worker;

import github.antmonitor.notifications.INotifier;

import java.util.List;
import java.util.Map;

public class WorkerChecker {
    final private List<? extends IWorkerRule> rules;
    final private INotifier notifier;

    public WorkerChecker( List<? extends IWorkerRule> rules, INotifier notifier) {
        this.rules = rules;
        this.notifier = notifier;
    }
    public void checkAll(Map<String, Worker> workers) {
        for (IWorkerRule rule: rules) {
            String alert = rule.alert(workers);
            if (alert != null) {
                notifier.send(alert);
            }
        }
    }
}
