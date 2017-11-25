package worker;

import java.util.Map;

public class WorkerLast10mRule implements IWorkerRule {

    private double threshold;
    private String worker;
    //TODO private boolean state; think about adding state send a message only if it wasn't sent


    public WorkerLast10mRule() {

    }
    public WorkerLast10mRule(String worker, double threshold) {
        this.threshold = threshold;
        this.worker = worker;
    }

    public String alert(Map<String, Worker> workerMap) {
        Worker worker = workerMap.get(this.worker);
        if (worker == null) {
            return "Worker '" + this.worker + "' haven't been found on antPool";
        } else {
            if (worker.getLast10m() < threshold) {
                return "Worker '" + this.worker + "' hashrate is " + worker.getLast10m() +
                        " but it should be bigger than " + threshold;
            } else {
                return null;
            }
        }

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
        final StringBuffer sb = new StringBuffer("WorkerLast10mRule{");
        sb.append("threshold=").append(threshold);
        sb.append(", worker='").append(worker).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
