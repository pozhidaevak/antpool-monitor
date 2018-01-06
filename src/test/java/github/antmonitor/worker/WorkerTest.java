package github.antmonitor.worker;

import github.antmonitor.AbstractTest;
import org.junit.Test;
import static org.junit.Assert.*;

public class WorkerTest extends AbstractTest {

  @Test
  public void testGetters() {
    Worker worker = new Worker("test", 123.45, 67.89);
    assertEquals("test", worker.getName());
    assertEquals(123.45, worker.getLast10m(),0.01);
    assertEquals(67.89, worker.getLast1h(),0.01);
  }

}
