package github.antmonitor.worker;

import github.antmonitor.worker.Worker;

import java.util.Map;

public interface IWorkerRule {
    String alert(Map<String, Worker> workerMap);
}
