package github.antmonitor.worker;

import github.antmonitor.notifications.Messages;
import java.util.Map;

public class WorkerLast1hRule implements IWorkerRule {

  private double threshold;
  private String worker;
  private boolean errorState = false;

  public WorkerLast1hRule() { //It is mandatory for property binding
  }

  public WorkerLast1hRule(String worker, double threshold) {
    this.threshold = threshold;
    this.worker = worker;
  }

  public String alert(Map<String, Worker> workerMap) {
    Worker worker = workerMap.get(this.worker);
    if(worker != null && !this.worker.equals(worker.getName())) {
      throw new IllegalArgumentException("Map key should be equal to worker name");
    }
    String retVal = null;
    if (!errorState) {
      if (worker == null) {
        retVal = Messages.workerNotFound(this.worker);
      } else if (worker.getLast1h() < threshold) {
        retVal = Messages.workerLowHashRate(this.worker, worker.getLast1h(), threshold);
      }
    } else if (worker != null && worker.getLast1h() >= threshold) {
      retVal = Messages.workerNormalHashRate(this.worker);
    }

    if (retVal != null) {
      errorState = !errorState;
    }
    return retVal;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("WorkerLast1hRule{");
    sb.append("threshold=").append(threshold);
    sb.append(", worker='").append(worker).append('\'');
    sb.append('}');
    return sb.toString();
  }

  public boolean getErrorState() {
    return errorState;
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
