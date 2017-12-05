import com.mashape.unirest.http.exceptions.UnirestException;
import config.Configuration;
import config.DailyReport;
import notifications.INotifier;
import notifications.TelegramNotifier;
import notifications.TextTableGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import worker.Worker;
import worker.WorkerChecker;
import worker.WorkerLast1hRule;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

final public class AntpoolMonitor {
    private static final Logger log = LogManager.getLogger(AntpoolMonitor.class);
    private AntpoolMonitor(){

    }

    public static void main(String[] args) throws InterruptedException {
        try {




            log.info("-------START------");

            //reading config
            long checkPeriod = Configuration.getInstance().getCheckPeriod();
            List<WorkerLast1hRule> rules = Configuration.getInstance().getRules();
            INotifier telegram = Configuration.getInstance().getTelegram();

            //Adding message on exit
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    log.warn("-------SHUT DOWN------");
                    telegram.send("Buy, I'm shutting down.");
                }
            }, "Shutdown-thread"));

            DailyReport dailyReport = Configuration.getInstance().getDailyReport();
            dailyNotification(dailyReport.getHour(), dailyReport.getMinute());

            telegram.send("Hey, I've started.");
        while (true) {
            try {
                Map<String, Worker> workers = null;
                try {
                    workers = Configuration.getInstance().getApi().getWorkers();
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

    public static void dailyNotification( int hour, int minute) throws UnirestException {
        //Map<String, Worker> workers = Configuration.getInstance().getApi().getWorkers();
        final int SECONDS_IN_DAY = 60 * 60 * 24;
        LocalDateTime localNow = LocalDateTime.now();
        LocalDateTime localNext = localNow.withHour(hour).withMinute(minute).withSecond(0);
        if(localNow.compareTo(localNext) > 0)
            localNext = localNext.plusDays(1);

        Duration duration = Duration.between(localNow, localNext);
        long initialDelay = duration.getSeconds();
        log.debug("Initial delay = " + initialDelay);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
              Map<String, Worker> workerMap = Configuration.getInstance().getApi().getWorkers();
              List<Worker> workerList = new ArrayList<>(workerMap.values());
              TextTableGenerator tableGenerator = new TextTableGenerator(workerList);
              Configuration.getInstance().getTelegram().send(TelegramNotifier.monospace(tableGenerator.toString()));

            } catch (Exception e) {
                log.error("Exception during daily report: ", e);
            }

        }, initialDelay,
                SECONDS_IN_DAY, TimeUnit.SECONDS);
    }
}
