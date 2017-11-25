package config;

import api.Api;
import notifications.INotifier;
import notifications.TelegramNotifier;
import org.yaml.snakeyaml.Yaml;
import worker.WorkerLast10mRule;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Configuration {
    public static Configuration INSTANCE;
    public static Api API;
    public static INotifier NOTIFIER;

    static {
        Yaml yaml = new Yaml();
        try (InputStream is = new FileInputStream(new File("config/config.yaml").getAbsoluteFile())) {
            INSTANCE = yaml.loadAs(is, Configuration.class );

        }  catch (IOException e) {
            e.printStackTrace();
            //TODO add logs
        }
        //TODO refactor API so it initialize automatically as rules
        API = new Api(INSTANCE.getUserId(), Configuration.INSTANCE.getKey(), Configuration.INSTANCE.getSecret());
    }

    private String userId;
    private String key;
    private String secret;
    private TelegramNotifier telegram;
    private List<WorkerLast10mRule> rules;

    public TelegramNotifier getTelegram() {
        return telegram;
    }

    public void setTelegram(TelegramNotifier telegram) {
        this.telegram = telegram;
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
