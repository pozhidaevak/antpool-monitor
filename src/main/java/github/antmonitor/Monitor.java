package github.antmonitor;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import github.antmonitor.api.IGetWorkersApi;
import github.antmonitor.notifications.INotifier;
import github.antmonitor.notifications.Messages;
import github.antmonitor.notifications.reports.Report;
import github.antmonitor.worker.Worker;
import github.antmonitor.worker.WorkerChecker;
import java.util.Map;
import javax.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class Monitor {

  private static final Logger log = LogManager.getLogger(Monitor.class);

  private final IGetWorkersApi IGetWorkersApi;

  private final WorkerChecker workerChecker;

  private final Report report;

  private final INotifier notifier;

  private boolean apiNotResponding = false;

  @Autowired
  public Monitor(IGetWorkersApi IGetWorkersApi, WorkerChecker workerChecker, Report report, INotifier notifier) {

    this.IGetWorkersApi = IGetWorkersApi;
    this.workerChecker = workerChecker;
    this.report = report;
    this.notifier = notifier;
  }

  public void init() {
    log.info("Init is called");
    notifier.send(Messages.greeting());
    report.sendReport();
  }

  @Scheduled(fixedRateString = "#{${monitorRate} * 1000}")
  public void loop()  {
    try {
      Map<String, Worker> workerMap = IGetWorkersApi.requestWorkers();
      if (workerMap != null && workerMap.size() >= 1 && apiNotResponding == true) {
        apiNotResponding = false;
        log.info(Messages.antpoolWorkingAgain());
        notifier.send(Messages.antpoolWorkingAgain());
      }
      workerChecker.checkAll(workerMap);
    } catch (HystrixRuntimeException e) {
      if (!apiNotResponding) {
        apiNotResponding = true;
        log.error("first Hystrix error");
        notifier.send(Messages.antpoolCircuitOpened());
      } else {
        log.warn("Consequent Hystrix error");
      }
    }
    catch (Exception e) {
      log.error("error during loop: ", e);
      if(!apiNotResponding) {
        notifier.send(Messages.exceptionsInLoop(e.getMessage()));
      }
    }
  }

  @PreDestroy
  public  void preDestroy() {
    notifier.send(Messages.bye());
  }
}
