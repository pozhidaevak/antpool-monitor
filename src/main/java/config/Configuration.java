package config;

import api.Api;
import org.yaml.snakeyaml.Yaml;
import worker.WorkerLast10mRule;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Configuration {
    private String userId;
    private String key;
    private String secret;
    private List<WorkerLast10mRule> rules;
    public static Configuration INSTANCE;
    public static Api API;
    static {
        Yaml yaml = new Yaml();
        try (InputStream is = Configuration.class.getResourceAsStream("config.yaml")) {
            INSTANCE = yaml.loadAs(is, Configuration.class );
            API = new Api(INSTANCE.getUserId(), Configuration.INSTANCE.getKey(), Configuration.INSTANCE.getSecret());
        }  catch (IOException e) {
            e.printStackTrace();
            //TODO add logs
        }
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public List<WorkerLast10mRule> getRules() {
        return rules;
    }

    public void setRules(List<WorkerLast10mRule> rules) {
        this.rules = rules;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "userId='" + userId + '\'' +
                ", key='" + key + '\'' +
                ", secret='" + secret + '\'' +
                ", rules=" + rules +
                '}';
    }
}
