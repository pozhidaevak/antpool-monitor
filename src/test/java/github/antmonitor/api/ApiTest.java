package github.antmonitor.api;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.JsonMappingException;
import github.antmonitor.AbstractTest;
import github.antmonitor.notifications.IMonospaceNotifier;
import github.antmonitor.worker.Worker;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringContains;
import org.intellij.lang.annotations.Language;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


public class ApiTest extends AbstractTest {

  public static final String testWorkerName = "user.worker1";
  //region
  public static @Language("JSON")
  final String expectedResponse = "{\n" +
      "  \"code\": 0,\n" +
      "  \"message\": \"ok\",\n" +
      "  \"data\": {\n" +
      "    \"rows\": [\n" +
      "      {\n" +
      "        \"worker\": \"" + ApiTest.testWorkerName + "\",\n" +
      "        \"last10m\": \"123\",\n" +
      "        \"last30m\": \"321\",\n" +
      "        \"last1h\": \"123456\",\n" +
      "        \"last1d\": \"0\",\n" +
      "        \"prev10m\": \"0\",\n" +
      "        \"prev30m\": \"0\",\n" +
      "        \"prev1h\": \"0\",\n" +
      "        \"prev1d\": \"0\",\n" +
      "        \"accepted\": \"3776512\",\n" +
      "        \"stale\": \"90112\",\n" +
      "        \"dupelicate\": \"0\",\n" +
      "        \"other\": \"16384\"\n" +
      "      },\n" +
      "      {\n" +
      "        \"worker\": \"user.worker2\",\n" +
      "        \"last10m\": \"0\",\n" +
      "        \"last30m\": \"0\",\n" +
      "        \"last1h\": \"0\",\n" +
      "        \"last1d\": \"0\",\n" +
      "        \"prev10m\": \"0\",\n" +
      "        \"prev30m\": \"0\",\n" +
      "        \"prev1h\": \"0\",\n" +
      "        \"prev1d\": \"0\",\n" +
      "        \"accepted\": \"0\",\n" +
      "        \"stale\": \"0\",\n" +
      "        \"dupelicate\": \"0\",\n" +
      "        \"other\": \"0\"\n" +
      "      }\n" +
      "    ],\n" +
      "    \"page\": 1,\n" +
      "    \"totalPage\": 1,\n" +
      "    \"pageSize\": 10,\n" +
      "    \"totalRecord\": 6\n" +
      "  }\n" +
      "}";
  // endregion
  private static final Logger log = LogManager.getLogger(ApiTest.class);
  @Autowired
  private IGetWorkersApi api;
  private Matcher<String> matcher;
  @Autowired
  private RestTemplate restTemplate;
  private MockRestServiceServer mockServer;

  @MockBean
  private IMonospaceNotifier notifier;

  @Before
  public void setUp() {
    mockServer = MockRestServiceServer.createServer(restTemplate);
    matcher = new StringContains(api.getUrl());
  }

  @Test(expected = HttpClientErrorException.class)
  @DirtiesContext
  public void apiTest_HttpNotFound() throws Exception {
    mockServer.expect(requestTo(matcher))
        .andRespond(withStatus(HttpStatus.NOT_FOUND));
    api.requestWorkers();
  }

  @Test(expected = java.io.IOException.class)
  @DirtiesContext
  public void apiTest_NotJson() throws Exception {
    mockServer.expect(requestTo(matcher))
        .andRespond(withSuccess("non JSON Garbage", MediaType.APPLICATION_JSON));
    api.requestWorkers();
  }

  @Test(expected = NullPointerException.class)
  public void apiTest_JSONWithoutRows() throws Exception {
    mockServer.expect(requestTo(matcher))
        .andRespond(withSuccess("{\"data\":{\"notrows\": [1,2,3]}}", MediaType.APPLICATION_JSON));
    api.requestWorkers();
  }

  @Test(expected = JsonMappingException.class)
  @DirtiesContext
  public void apiTest_JSONWitInvalidRows() throws Exception {
    mockServer.expect(requestTo(matcher))
        .andRespond(withSuccess("{\n" +
            "  \"data\": {\n" +
            "    \"rows\": [\n" +
            "      {\n" +
            "        \"worker\": \"test\",\n" +
            "        \"last10m\": 123.32,\n" +
            "        \"last1h\": 321\n" +
            "      },\n" +
            "      {\n" +
            "        \"worker\": \"test\",\n" +
            "        \"garbage\": \"dizizgarbadge\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"moreGarbage\": \"d\",\n" +
            "        \"last1h\": 123.456\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}", MediaType.APPLICATION_JSON));
    Map<String, Worker> map = api.requestWorkers();
    log.info(map.toString());
  }

  @Test
  @DirtiesContext
  public void apiTest_goodResponse() throws Exception {
    final String testWorkerName = "user.worker1";
    mockServer.expect(requestTo(matcher))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));
    Map<String, Worker> workerMap = api.requestWorkers();
    assertEquals(2, workerMap.size());
    assertEquals(testWorkerName, workerMap.get(testWorkerName).getName());
    assertEquals(123456, workerMap.get(testWorkerName).getLast1h(), 0.1);

  }
}
