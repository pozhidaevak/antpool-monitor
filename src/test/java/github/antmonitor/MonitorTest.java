package github.antmonitor;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import github.antmonitor.api.AntpoolApi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableCircuitBreaker
@EnableAspectJAutoProxy
public class MonitorTest {

  private static final Logger log = LogManager.getLogger(MonitorTest.class);

  @Autowired
  private Monitor monitor;
  @Autowired
  private AntpoolApi api;

  private Matcher<String> matcher;

  @Autowired
  private RestTemplate restTemplate;

  private MockRestServiceServer mockServer;

  @Before
  public void setUp() {
    mockServer = MockRestServiceServer.createServer(restTemplate);
    matcher = new StringContains(api.getUrl());
  }

  @Test
  public void http404() throws InterruptedException {
    mockServer.expect(ExpectedCount.manyTimes(), requestTo(matcher))
        .andRespond(withStatus(HttpStatus.NOT_FOUND));
    for(int i = 0; i < 10; ++i) {
      Thread.sleep(2000);
      try {
        monitor.loop();
      } catch (HystrixBadRequestException e) {
        log.error("HysterixWorking", e);
      }
    }

  }

}
