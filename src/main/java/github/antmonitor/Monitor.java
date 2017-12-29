package github.antmonitor;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import github.antmonitor.api.AntpoolApi;
import github.antmonitor.notifications.INotifier;
import github.antmonitor.notifications.reports.Report;
import github.antmonitor.worker.Worker;
import github.antmonitor.worker.WorkerChecker;
import java.io.IOException;
import java.util.Map;
import javax.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class Monitor {
  private static final Logger log = LogManager.getLogger(Monitor.class);

  private final AntpoolApi antpoolApi;

  private final WorkerChecker workerChecker;

  private final Report report;

  private final INotifier notifier;

  @Autowired
  public Monitor(AntpoolApi antpoolApi, WorkerChecker workerChecker, Report report, INotifier notifier) {

    this.antpoolApi = antpoolApi;
    this.workerChecker = workerChecker;
    this.report = report;
    this.notifier = notifier;

    notifier.send("Hi!, I'm starting");
    report.sendReport();
  }

  @Scheduled(fixedRateString = "${monitorRate}")
  public void loop()  {
    try {
      Map<String, Worker> workerMap = antpoolApi.requestWorkers();
      workerChecker.checkAll(workerMap);
    } catch (HystrixRuntimeException e) {
      //TODO notify only once and then disable notifications
    }
    catch (Exception e) {
      log.error("error during loop: ", e);
      notifier.send(e.getMessage());
    }
  }

  @PreDestroy
  public  void preDestroy() {
    notifier.send("I'm shutting down. Good Buy!");
  }
}
