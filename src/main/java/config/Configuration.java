package config;

import api.Api;
import api.WorkerRequest;
import notifications.TelegramNotifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import worker.WorkerLast1hRule;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Configuration {
    public static final Configuration INSTANCE;
    //public static INotifier NOTIFIER;
    private static final Logger log = LogManager.getLogger(WorkerRequest.class);

    static {
        //TODO think about better singleton pattern
        Yaml yaml = new Yaml();
        Configuration tempConf = null;
        try (InputStream is = new FileInputStream(new File("config/config.yaml").getAbsoluteFile())) {
            tempConf = yaml.loadAs(is, Configuration.class );

        }  catch (IOException e) {
            log.error(e);
        } finally {
            INSTANCE = tempConf;
        }
    }
    private Api api;
    private long checkPeriod;
    private TelegramNotifier telegram;
    private List<WorkerLast1hRule> rules;

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public TelegramNotifier getTelegram() {
        return telegram;
    }

    public void setTelegram(TelegramNotifier telegram) {
        this.telegram = telegram;
    }

    public List<WorkerLast1hRule> getRules() {
        return rules;
    }

    public void setRules(List<WorkerLast1hRule> rules) {
        this.rules = rules;
    }

    public long getCheckPeriod() {
        return checkPeriod;
    }

    public void setCheckPeriod(long checkPeriod) {
        this.checkPeriod = checkPeriod;
    }
}
