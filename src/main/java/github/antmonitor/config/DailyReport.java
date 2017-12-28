package github.antmonitor.config;

import github.antmonitor.api.Api;
import github.antmonitor.notifications.IMonospaceNotifier;
import github.antmonitor.notifications.INotifier;
import github.antmonitor.notifications.IReportGenerator;
import github.antmonitor.worker.Worker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DailyReport {

    private static final Logger log = LogManager.getLogger(DailyReport.class);

    private INotifier notifier;

    private IReportGenerator reportGenerator;
    private Api api;

    public DailyReport(@NotNull INotifier notifier, @NotNull IReportGenerator reportGenerator, @NotNull Api api) {

        this.notifier = notifier;
        this.reportGenerator = reportGenerator;
        this.api = api;
        if (reportGenerator.requireMonospace() && !(notifier instanceof IMonospaceNotifier)) {
            throw new IllegalArgumentException("If reportGenerator requests monospace method" +
                " then notifier should be IMonospaceNotifier");
        }
        dailyNotification();
    }
    public void dailyNotification() {
        /*final int SECONDS_IN_DAY = 60 * 60 * 24;
        LocalDateTime localNow = LocalDateTime.now();
        LocalDateTime localNext = localNow.withHour(hour).withMinute(minute).withSecond(0);
        if(localNow.compareTo(localNext) > 0)
            localNext = localNext.plusDays(1);

        Duration duration = Duration.between(localNow, localNext);
        long initialDelay = duration.getSeconds();
        log.debug("Initial delay = " + initialDelay);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {*/
                try {
                    Map<String, Worker> workerMap = api.getWorkers();
                    List<Worker> workerList = new ArrayList<>(workerMap.values());
                    String report = reportGenerator.generate(workerList);
                    if (reportGenerator.requireMonospace()) {
                        if(notifier instanceof IMonospaceNotifier) {
                            report = ((IMonospaceNotifier) notifier).monospace(report);
                        } else {
                            assert true : "Should never happen, checked in constructor";
                        }
                    }
                    notifier.send(report);

                } catch (Exception e) {
                    log.error("Exception during daily report: ", e);
                }

            /*}, initialDelay,
            SECONDS_IN_DAY, TimeUnit.SECONDS);*/
    }
}
