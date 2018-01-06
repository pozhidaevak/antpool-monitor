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

  /**
   * Sends an alert when worker starts or stops to satisfy the rule
   *
   * @param workerMap current workers
   * @return Alert message if needed or null if there is no need to send an alert
   */
  public String alert(Map<String, Worker> workerMap) {
    Worker worker = workerMap.get(this.worker);
    if (worker != null && !this.worker.equals(worker.getName())) {
      throw new IllegalArgumentException("Map key should be equal to worker name");
    }
    String retVal = null;
    // If worker satisfied the rule before (Or the rule hasn't been checked before)
    if (!errorState) {
      if (worker == null) {
        //Return message if there is no such worker.
        retVal = Messages.workerNotFound(this.worker);
      } else if (worker.getLast1h() < threshold) {
        //return message if worker hashrate is lower than the threshold
        retVal = Messages.workerLowHashRate(this.worker, worker.getLast1h(), threshold);
      }
    } else if (worker != null && worker.getLast1h() >= threshold) {
      //If worker broke the rule before, but now it satisfies the rule then send message
      retVal = Messages.workerNormalHashRate(this.worker);
    }

    // If message is not null then errorStatus should flip.
    // As we returning messages only if error status has changed
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
