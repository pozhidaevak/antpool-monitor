package worker;

import worker.Worker;

import java.util.Map;

public interface IWorkerRule {
    String alert(Map<String, Worker> workerMap);
}
