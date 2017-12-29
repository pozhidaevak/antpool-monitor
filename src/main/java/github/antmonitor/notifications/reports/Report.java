package github.antmonitor.notifications.reports;

import github.antmonitor.api.AntpoolApi;
import github.antmonitor.notifications.IMonospaceNotifier;
import github.antmonitor.notifications.INotifier;
import github.antmonitor.worker.Worker;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Report {

  private static final Logger log = LogManager.getLogger(Report.class);

  private INotifier notifier;

  private IReportGenerator reportGenerator;
  private AntpoolApi api;

  @Autowired
  public Report(@NotNull INotifier notifier, @NotNull IReportGenerator reportGenerator,
      @NotNull AntpoolApi api) {

    this.notifier = notifier;
    this.reportGenerator = reportGenerator;
    this.api = api;
    if (reportGenerator.requireMonospace() && !(notifier instanceof IMonospaceNotifier)) {
      throw new IllegalArgumentException("If reportGenerator requests monospace method" +
          " then notifier should be IMonospaceNotifier");
    }
  }

  @Scheduled(cron = "${reportSchedule}")
  public void sendReport() {
    try {
      Map<String, Worker> workerMap = api.requestWorkers();
      List<Worker> workerList = new ArrayList<>(workerMap.values());
      String report = reportGenerator.generate(workerList);
      if (reportGenerator.requireMonospace()) {
        if (notifier instanceof IMonospaceNotifier) {
          report = ((IMonospaceNotifier) notifier).monospace(report);
        } else {
          assert true : "Should never happen, checked in constructor";
        }
      }
      notifier.send(report);

    } catch (Exception e) {
      log.error("Exception during report: ", e);
    }
  }
}
