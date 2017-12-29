package github.antmonitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ImportResource("classpath:spring/app-context.xml")
@EnableScheduling
public class AntpoolMonitor {

  private static final Logger log = LogManager.getLogger(AntpoolMonitor.class);

  public static void main(String[] args) {
    try {

      log.info("-------START------");
      ConfigurableApplicationContext ctx = SpringApplication.run(AntpoolMonitor.class, args);

    } catch (Exception e) {
      log.fatal("Exception during initialization");
    }
  }
}
