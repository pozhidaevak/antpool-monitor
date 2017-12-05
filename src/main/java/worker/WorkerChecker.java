package worker;

import notifications.INotifier;

import java.util.List;
import java.util.Map;

public class WorkerChecker {
    final private Map<String, Worker> workers;
    final private List<? extends IWorkerRule> rules;
    final private INotifier notifier;

    public WorkerChecker(Map<String, Worker> workers, List<? extends IWorkerRule> rules, INotifier notifier) {
        this.workers = workers;
        this.rules = rules;
        this.notifier = notifier;
    }
    public void checkAll() {
        for (IWorkerRule rule: rules) {
            String alert = rule.alert(workers);
            if (alert != null) {
                notifier.send(alert);
            }
        }
    }
}
