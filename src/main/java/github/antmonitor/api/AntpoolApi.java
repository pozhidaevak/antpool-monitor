package github.antmonitor.api;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import github.antmonitor.notifications.Messages;
import github.antmonitor.worker.Worker;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
public class AntpoolApi {

  private static final Logger log = LogManager.getLogger(AntpoolApi.class);

  private String url;
  private String key;
  private String secret;
  private String userId;
  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  public AntpoolApi(@Value("${antpool.user}") String userId,
      @Value("${antpool.key}") String key,
      @Value("${antpool.secret}") String secret,
      @Value("${antpool.url}") String url) {
    this.userId = userId;
    this.key = key;
    this.secret = secret;
    this.url = url;
  }

  @HystrixCommand
  public Map<String, Worker> requestWorkers() throws IOException {
    log.info("Starting requestWorkers");
    //Receive necessary request parameters
    WorkerRequest request = new WorkerRequest(key, secret, userId);

    //Declare/init vars
    ObjectMapper mapper = new ObjectMapper();
    String httpResult;
    JsonNode rootNode;
    JsonNode workersNode;

    //TODO probably one try catch is enough
    // get response from Antpoolserver
    try {
      httpResult = restTemplate.getForObject(url + request.getParams(), String.class);
    } catch (HttpStatusCodeException e) {
      log.error("Antpool api returned HTTP code: " + e.getStatusCode().toString());
      throw e;
    } catch (Exception e) {
      log.fatal("Unexpected exception: ", e);
      throw e;
    }

    //Parse it as JSON
    try {
      rootNode = mapper.readValue(httpResult, JsonNode.class);
    } catch (JsonParseException | NullPointerException e) {
      log.error("Error during parsing Antpool API response. Response: \n" + httpResult
          + "\n Exception: \n", e);

      throw new IOException(Messages.noJson(), e);
    } catch (Exception e) {
      log.fatal("Unexpected exception: ", e);
      throw e;
    }

    //Get workers array (data.rows) from json
    try {
      workersNode = rootNode.get("data").get("rows");
    } catch (NullPointerException e) {
      log.error("JSON probably doesn't has data or rows parameters. Response: \n"
          + httpResult + "\n Exception: ", e);
      throw e;
    } catch (Exception e) {
      log.fatal("Unexpected exception: ", e);
      throw e;
    }

    //Parse workers array
    Worker[] workers;
    try {
      workers = mapper.readValue(workersNode.toString(), Worker[].class);
    } catch (IOException e) {
      log.error("Error during parsing workers array: ", e);
      throw e;
    }

    Map<String, Worker> workersMap = new HashMap<>(workers.length);
    for (Worker i : workers) {
      workersMap.put(i.getName(), i);
    }

    return workersMap;
  }

  public String getKey() {
    return key;
  }

  public String getSecret() {
    return secret;
  }

  public String getUrl() {
    return url;
  }

  public String getUserId() {
    return userId;
  }
}
