package github.antmonitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

//TODO use @Service instead @Component on all beans
@SpringBootApplication
@EnableConfigurationProperties
@EnableCircuitBreaker
public class AntpoolMonitor {

  private static final Logger log = LogManager.getLogger();

  public static void main(String[] args) {
    try {
      log.info("-------START------");
      ConfigurableApplicationContext ctx = SpringApplication.run(AntpoolMonitor.class, args);
      Monitor monitor = ctx.getBean(Monitor.class);
      monitor.init();
      ctx.registerShutdownHook();
    } catch (Exception e) {
      log.fatal("Exception during initialization", e);
    }
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public TaskScheduler taskScheduler() {
    return new ThreadPoolTaskScheduler();
  }

  @Bean
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("messages");
    return messageSource;
  }

  @Configuration
  @ConditionalOnProperty(value = "scheduling.enabled", havingValue = "true", matchIfMissing = true)
  @EnableScheduling
  public static class SchedulingConfiguration {

  }
}
