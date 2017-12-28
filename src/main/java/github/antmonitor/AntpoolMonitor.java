package github.antmonitor;

import com.mashape.unirest.http.exceptions.UnirestException;
import github.antmonitor.config.Configuration;
import github.antmonitor.config.DailyReport;
import github.antmonitor.notifications.INotifier;
import github.antmonitor.notifications.TelegramNotifier;
import github.antmonitor.notifications.TextTableGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;
import github.antmonitor.worker.Worker;
import github.antmonitor.worker.WorkerChecker;
import github.antmonitor.worker.WorkerLast1hRule;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@ImportResource("classpath:spring/app-context.xml")
@EnableScheduling
public class AntpoolMonitor {
    private static final Logger log = LogManager.getLogger(AntpoolMonitor.class);

    public static void main(String[] args)  {
        try {




            log.info("-------START------");
            ConfigurableApplicationContext ctx = SpringApplication.run(AntpoolMonitor.class, args);

           /* //reading config
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
        }*/
        } catch (Exception e) {
            log.fatal("Exception during initialization");
        }
    }
}
