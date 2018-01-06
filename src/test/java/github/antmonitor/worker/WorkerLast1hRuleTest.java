package github.antmonitor.worker;

import github.antmonitor.AbstractTest;
import github.antmonitor.notifications.Messages;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

public class WorkerLast1hRuleTest extends AbstractTest {
  public Worker worker = new Worker("test", 115, 125);
  public Worker worker2 = new Worker("test2", 115, 125);
  public WorkerLast1hRule rule = new WorkerLast1hRule("test", 123.45);
  public Worker slowWorker = new Worker("test", 1215, 115);
  public Map<String, Worker> workerMap = new HashMap<>();

  @Test
  public void workingMiner() {
    workerMap.put(worker.getName(),worker);
    String result = rule.alert(workerMap);
    assertNull("rule.alert() should return null then miner is working", result);
    assertFalse(rule.getErrorState());
  }

  @Test
  public void workerDoesntExist() {
    workerMap.put(worker2.getName(), worker2);
    String result = rule.alert(workerMap);

    //First call error message should be returned and error state = true
    assertEquals(Messages.workerNotFound(worker.getName()), result);
    assertTrue(rule.getErrorState());

    //Second call no error message but error state = true
    result = rule.alert(workerMap);
    assertNull(result);
    assertTrue(rule.getErrorState());

    //Adding slow worker
    workerMap.put(slowWorker.getName(), slowWorker);
    result = rule.alert(workerMap);
    assertNull(result);
    assertTrue(rule.getErrorState());

    //adding normal worker
    workerMap.put(worker.getName(), worker);
    result = rule.alert(workerMap);
    //Should return message that everything is back to normal
    assertEquals(Messages.workerNormalHashRate(worker.getName()), result);
    assertFalse(rule.getErrorState());

  }

  @Test
  public void slowWorker() {
    //Check with slow worker
    workerMap.put(slowWorker.getName(), slowWorker);
    String result = rule.alert(workerMap);
    assertEquals(Messages.workerLowHashRate(
        slowWorker.getName(),slowWorker.getLast1h(),rule.getThreshold()), result);
    assertTrue(rule.getErrorState());

    //Second request with slow worker
    result = rule.alert(workerMap);
    assertNull(result);
    assertTrue(rule.getErrorState());

    //Third request with normal worker.
    workerMap.put(worker.getName(), worker);
    result = rule.alert(workerMap);
    assertEquals(Messages.workerNormalHashRate(worker.getName()), result);
    assertFalse(rule.getErrorState());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidMap() {
    workerMap.put(worker.getName(), worker2);
    rule.alert(workerMap);
  }

  @Test
  public void toStringTest() {
    String result = rule.toString();
    assertTrue(result.contains(rule.getWorker()));
    assertTrue(result.contains(Double.toString(rule.getThreshold())));
    assertTrue(result.contains(WorkerLast1hRule.class.getSimpleName()));
  }

}
