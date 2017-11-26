import config.Configuration;
import notifications.INotifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import worker.Worker;
import worker.WorkerChecker;
import worker.WorkerLast1hRule;

import java.util.List;
import java.util.Map;

public class AntpoolMonitor {
    private static final Logger log = LogManager.getLogger(AntpoolMonitor.class);
    public static void main(String[] args) throws InterruptedException {
        try {
            log.info("-------START------");
            long checkPeriod = Configuration.INSTANCE.getCheckPeriod();
            List<WorkerLast1hRule> rules = Configuration.INSTANCE.getRules();
            INotifier telegram = Configuration.INSTANCE.getTelegram();
            telegram.send("Hey, I'm started.");
        while (true) {
            try {
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
            } catch (Exception e) {
                String message = "Unexpected exception: ";
                log.error(message, e);
                telegram.send(message + e.getMessage());
            }


            Thread.sleep( checkPeriod * 1000);
        }
        } catch (Exception e) {
            log.fatal("Exception during initialization");
        }
    }
}
