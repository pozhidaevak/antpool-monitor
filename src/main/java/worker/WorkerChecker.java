package worker;

import notifications.INotifier;

import java.util.List;
import java.util.Map;

public class WorkerChecker {
    private Map<String, Worker> workers;
    private List<IWorkerRule> rules;
    private INotifier notifier;

    public WorkerChecker(Map<String, Worker> workers, List<IWorkerRule> rules, INotifier notifier) {
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
