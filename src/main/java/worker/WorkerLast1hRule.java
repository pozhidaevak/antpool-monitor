package worker;

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
        if(!error_state) {
            if (worker == null) {
                retVal = "Worker '" + this.worker + "' haven't been found on antPool";
            } else if (worker.getLast1h() < threshold) {
                retVal = "Worker '" + this.worker + "' hashrate is " + worker.getLast1h() +
                        " but it should be bigger than " + threshold;
            }
        } else if (error_state && worker != null && worker.getLast1h() >= threshold) {
                retVal =  "Worker '" + this.worker + "' hashrate is back to normal";
            }

        if(retVal != null) {
                error_state = !error_state;
        }
        return retVal;
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("WorkerLast1hRule{");
        sb.append("threshold=").append(threshold);
        sb.append(", worker='").append(worker).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
