# antpool-monitor
This is Java Spring Boot application. It monitors miners using [Antpool API](https://www.antpool.com/user/apiGuild.htm) and sends notifications using [Telegram Bot API](https://core.telegram.org/bots). The application is build in a way that it can be easily extended to support other pools or notification methods.

Main features:
* Send notification when the application starts or stops;
* Send notification if the hashrate of a miner is below a threshold or miner do not exists;
* Send notification then miner's hashrate is back to normal;
* HashRate report.

That it doesn't support:
* Multiple subaccounts;
* All notification are made only on hashrate from the last hour (last1h field from Antpool API) other fields are not supported

# Installation

Application is just a JAR file it can be build from source code using `gradle build` or downloaded from the releases on github. The JAR file can be launched by `java -jar antpool-monitor-1.0-SNAPSHOT.jar`. However, some simple configuration is neccessary to use it.

# Configuration
## Miniamal required configuration
It is necessary to put valid antpool API and telegram API params into `application-secrets.yaml`. The file also contains set of rules. A rule is basicaly a name of a worker and its minimal hashrate in kH/s. This file should be placed in the same directory as the JAR.
```yaml
antpool:
  key: "key"
  secret: "secret"
  user: "user" # Use antpool sub-account here. All your miners should be named user.minerName
telegram:
  apiKey: "apiKey"
  chatId: "chatId" # Chat or user Id where bot will send notifications
rules:
  - worker: "user.miner1"
    threshold: 3000000 # threshold is in kH/s
  - worker: "user.miner2"
    threshold: 3000000

```

## Additional configuration
Additional configuration can be changed by placing `application.yaml` file in the folder with JAR file. The default settings can be seen below.
``` yaml
antpool:
  url: "https://antpool.com/api/workers.htm"
telegram:
  timeout: 10 # in seconds
reportSchedule: "0 0 11 * * *" # Cron-like syntax. See https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html
monitorRate: 600 # in seconds
spring:
  application:
    name: "AntpoolMonitor"
  profiles:
    include: 'secrets'
hystrix:
  command:
    requestWorkers:

      # amount of failed requests that will stop requests to the pool
      circuitBreaker.requestVolumeThreshold: 2

      # Sleep time for which requests will not be sent to the pool
      circuitBreaker.sleepWindowInMilliseconds: 1800000
      execution.isolation.thread.timeoutInMilliseconds: 10000

      # Time window for which the amount of failed requests is calculated
      metrics.rollingStats.timeInMilliseconds: 3600000
```
You can add only subset of parameters that you wish to change to your configuration. For example, this is valid `application.yaml` that will check miner status each minute (60 seconds) instead of default 10 minutes.

``` yaml
monitorRate: 60
```
# Contribution
All contributions are welcome. Especially code reviews as I am not that experienced :). Bug reports, new features and solving issues are also very welcome.
