package github.antmonitor.worker;

import java.util.Map;

public class WorkerLast1hRule implements IWorkerRule {

  private double threshold;
  private String worker;
  private boolean error_state;

  public WorkerLast1hRule() {
    error_state = false;
  }

  public WorkerLast1hRule(String worker, double threshold) {
    this.threshold = threshold;
    this.worker = worker;
  }

  public String alert(Map<String, Worker> workerMap) {
    Worker worker = workerMap.get(this.worker);
    String retVal = null;
    if (!error_state) {
      if (worker == null) {
        retVal = "Worker '" + this.worker + "' haven't been found on antPool";
      } else if (worker.getLast1h() < threshold) {
        retVal = "Worker '" + this.worker + "' hashrate is " + worker.getLast1h() +
            " but it should be bigger than " + threshold;
      }
    } else if (worker != null && worker.getLast1h() >= threshold) {
      retVal = "Worker '" + this.worker + "' hashrate is back to normal";
    }

    if (retVal != null) {
      error_state = !error_state;
    }
    return retVal;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("WorkerLast1hRule{");
    sb.append("threshold=").append(threshold);
    sb.append(", github.antmonitor.worker='").append(worker).append('\'');
    sb.append('}');
    return sb.toString();
  }

  public double getThreshold() {
    return threshold;
  }

  public void setThreshold(double threshold) {
    this.threshold = threshold;
  }

  public String getWorker() {
    return worker;
  }

  public void setWorker(String worker) {
    this.worker = worker;
  }
}
