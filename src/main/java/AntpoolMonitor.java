import com.mashape.unirest.http.exceptions.UnirestException;
import config.Configuration;
import notifications.INotifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import worker.IWorkerRule;
import worker.Worker;
import worker.WorkerChecker;
import worker.WorkerLast10mRule;

import java.util.List;
import java.util.Map;

public class AntpoolMonitor {
    private static final Logger log = LogManager.getLogger(AntpoolMonitor.class);
    public static void main(String[] args) throws InterruptedException {
        log.info("-------START------");
        long checkPeriod = Configuration.INSTANCE.getCheckPeriod();
        List<WorkerLast10mRule> rules = Configuration.INSTANCE.getRules();
        INotifier telegram = Configuration.INSTANCE.getTelegram();
        while (true) {

            Map<String, Worker> workers = null;
            try {
                workers = Configuration.INSTANCE.getApi().getWorkers();
            } catch (Exception e) {
                String message = "Error while retrieving workers: ";
                log.error(message, e);
                telegram.send(message + e.getMessage());
            }

            if(workers != null) {
                WorkerChecker checker = new WorkerChecker(workers, rules, telegram);
                checker.checkAll();
            } else {
                String message = "Empty workers list";
                log.error(message);
                telegram.send(message);
            }

            Thread.sleep( checkPeriod * 1000);
        }
    }
}
