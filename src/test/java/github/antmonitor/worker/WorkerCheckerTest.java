package github.antmonitor.worker;

import github.antmonitor.AbstractTest;
import github.antmonitor.notifications.IMonospaceNotifier;
import github.antmonitor.notifications.Messages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.*;

public class WorkerCheckerTest extends AbstractTest {

  @MockBean
  private IMonospaceNotifier notifier;



  @Test
  public void test() {
    //initializing workers
    Worker worker = new Worker("test", 123, 200);
    Worker slowWorker = new Worker("slow", 1, 1);
    Worker notUsedWorker = new Worker("notUsed", 1, 1);
    Worker notExistingWorker = new Worker("notExisting", 100, 200);
    Map<String, Worker> workerMap = new HashMap<>(4);
    workerMap.put(worker.getName(), worker);
    workerMap.put(slowWorker.getName(), slowWorker);
    workerMap.put(notUsedWorker.getName(), notUsedWorker);

    //initializing rules
    List<WorkerLast1hRule> rules = new ArrayList<>();
    rules.add(new WorkerLast1hRule(worker.getName(), 150));
    rules.add(new WorkerLast1hRule(slowWorker.getName(), 45));
    rules.add(new WorkerLast1hRule(notExistingWorker.getName(), 150));

    //First call error messages should apper
    WorkerChecker checker = new WorkerChecker(notifier, rules);
    checker.checkAll(workerMap);
    verify(notifier).send(Messages.workerLowHashRate(slowWorker.getName(),slowWorker.getLast1h(),
        rules.get(1).getThreshold()));
    verify(notifier).send(Messages.workerNotFound(notExistingWorker.getName()));

    //Second call error messages shouldn't appear again
    checker.checkAll(workerMap);
    verifyNoMoreInteractions(notifier);

    //Third call nonExisting worker now exists. Message should appear
    workerMap.put(notExistingWorker.getName(), notExistingWorker);
    checker.checkAll(workerMap);
    verify(notifier).send(Messages.workerNormalHashRate(notExistingWorker.getName()));

  }

}
