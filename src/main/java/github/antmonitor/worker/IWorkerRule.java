package github.antmonitor.worker;

import java.util.Map;

public interface IWorkerRule {

  String alert(Map<String, Worker> workerMap);
}
