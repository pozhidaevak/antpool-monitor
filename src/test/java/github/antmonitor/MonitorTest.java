package github.antmonitor;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.netflix.hystrix.Hystrix;
import github.antmonitor.api.ApiTest;
import github.antmonitor.api.IGetWorkersApi;
import github.antmonitor.notifications.IMonospaceNotifier;
import github.antmonitor.notifications.Messages;
import github.antmonitor.notifications.reports.Report;
import github.antmonitor.worker.WorkerChecker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockingDetails;
import org.mockito.internal.util.DefaultMockingDetails;
import org.mockito.internal.util.MockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;


@DirtiesContext
public class MonitorTest extends AbstractTest {

  private static final Logger log = LogManager.getLogger();

  @Autowired
  private Monitor monitor;
  @Autowired
  private IGetWorkersApi api;

  @Autowired
  private WorkerChecker workerChecker;

  @Autowired
  private Report report;

  @MockBean
  private IMonospaceNotifier notifier;

  private Matcher<String> matcher;

  @Autowired
  private RestTemplate restTemplate;

  private MockRestServiceServer mockServer;

  @Before
  public void setUp() {
    matcher = new StringContains(api.getUrl());
  }

  @Test
  @DirtiesContext
  public void http404() throws InterruptedException {
    Hystrix.reset();
    mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(ExpectedCount.manyTimes(), requestTo(matcher))
        .andRespond(withStatus(HttpStatus.NOT_FOUND));
    MockUtil mockUtil = new MockUtil();
    MockingDetails details = new DefaultMockingDetails(notifier, mockUtil);
    for (int i = 0; i < 9; ++i) {
      Thread.sleep(300);
      monitor.loop();
    }
    if (notifier != null) {
      verify(notifier, atLeastOnce()).send(contains("404"));
      verify(notifier, atMost(4)).send(contains("404"));
      verify(notifier, times(1)).send(Messages.antpoolCircuitOpened());
      verify(notifier, never()).send(Messages.antpoolWorkingAgain());
    }
  }

  @Test
  @DirtiesContext
  public void http404thenFixed() throws InterruptedException {
    mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(ExpectedCount.times(2), requestTo(matcher))
        .andRespond(withStatus(HttpStatus.NOT_FOUND));
    mockServer.expect(ExpectedCount.manyTimes(), requestTo(matcher))
        .andRespond(withSuccess(ApiTest.expectedResponse, MediaType.APPLICATION_JSON));

    for (int i = 0; i < 10; ++i) {
      Thread.sleep(300);
      monitor.loop();
    }
    if (notifier != null) {

      verify(notifier, atMost(3)).send(contains("404"));
      verify(notifier).send(Messages.antpoolCircuitOpened());
      verify(notifier).send(Messages.antpoolWorkingAgain());
    }
  }

  @Test
  @DirtiesContext
  public void infrequent404() throws InterruptedException {
    Hystrix.reset();
    mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(ExpectedCount.times(1), requestTo(matcher))
        .andRespond(withStatus(HttpStatus.NOT_FOUND));
    mockServer.expect(ExpectedCount.times(7), requestTo(matcher))
        .andRespond(withSuccess(ApiTest.expectedResponse, MediaType.APPLICATION_JSON));
    mockServer.expect(ExpectedCount.times(1), requestTo(matcher))
        .andRespond(withStatus(HttpStatus.NOT_FOUND));
    mockServer.expect(ExpectedCount.times(1), requestTo(matcher))
        .andRespond(withSuccess(ApiTest.expectedResponse, MediaType.APPLICATION_JSON));

    for (int i = 0; i < 10; ++i) {
      Thread.sleep(400);
      monitor.loop();
    }

    if (notifier != null) {
      verify(notifier, atMost(3)).send(contains("404"));
      verify(notifier, never()).send(Messages.antpoolCircuitOpened());
      verify(notifier, never()).send(Messages.antpoolWorkingAgain());
    }
  }

  @Test
  @DirtiesContext
  public void emptyJSON() throws InterruptedException {
    Hystrix.reset();
    mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(ExpectedCount.times(10), requestTo(matcher))
        .andRespond(withSuccess("", MediaType.APPLICATION_JSON));
    for (int i = 0; i < 10; ++i) {
      Thread.sleep(300);
      monitor.loop();
    }
    if (notifier != null) {
      verify(notifier, atMost(3)).send(Messages.exceptionsInLoop(Messages.noJson()));
      verify(notifier, times(1)).send(Messages.antpoolCircuitOpened());
      verify(notifier, never()).send(Messages.antpoolWorkingAgain());
    }
  }

  @Test
  @DirtiesContext
  public void initTest() {
    doAnswer((invocation) -> invocation.getArguments()[0]).when(notifier).monospace(anyString());
    monitor.init();
    verify(notifier).send(Messages.greeting());
    verify(notifier).send(matches("(?s).*Worker Name.*"));
  }

}
