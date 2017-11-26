package api;

import com.google.gson.Gson;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import worker.Worker;

import java.util.HashMap;
import java.util.Map;


public class Api {
    public static final String URL =  "https://antpool.com/api/workers.htm";
    private String key;
    private String secret;
    private String userId;
    public Api(String userId, String key, String secret) {
        this.userId = userId;
        this.key = key;
        this.secret = secret;
    }
    public Api() {
        //Default constructor is necessary for initialization from JSON
    }

    public Map<String, Worker> getWorkers() throws UnirestException {
        WorkerRequest request = new WorkerRequest(key, secret, userId);
        HttpResponse<JsonNode> jsonResponse = Unirest.post(URL) //TODO we need to encapsulate request in Worker request
                .queryString("key",request.getKey())
                .queryString("nonce", request.getNonce())
                .queryString("signature", request.getSignature()).asJson();

        //TODO handle error responses
        JSONArray workersJson = jsonResponse.getBody().getObject().getJSONObject("data").getJSONArray("rows");

        Gson gson = new Gson();
        Worker[] workers = gson.fromJson(workersJson.toString(), Worker[].class);
        Map<String,Worker> workersMap = new HashMap<>(workers.length);
        for (Worker i: workers) {
            workersMap.put(i.getName(),i);
        }
        return workersMap;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
