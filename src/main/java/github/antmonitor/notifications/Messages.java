package github.antmonitor.notifications;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public final class Messages {

  static private final Locale locale = Locale.getDefault();


  static private MessageSource messageSource;

  private Messages() {
  }

  static private String getMessage(String code, Object[] args) {
    return messageSource.getMessage(code, args, locale);
  }

  static private String getMessage(String code) {
    return messageSource.getMessage(code, null, locale);
  }

  static public String greeting() {
    return getMessage("greeting");
  }

  static public String bye() {
    return getMessage("bye");
  }

  static public String antpoolWorkingAgain() {
    return getMessage("antpoolWorkingAgain");
  }

  static public String antpoolCircuitOpened() {
    return getMessage("antpoolCircuitOpened");
  }

  static public String exceptionsInLoop(String eMessage) {
    return getMessage("exceptionInLoop", new Object[]{eMessage});
  }

  static public String workerNotFound(String workerName) {
    return getMessage("workerNotFound", new Object[]{workerName});
  }

  static public String workerLowHashRate(String workerName, double currentHashRate,
      double thresholdHashRate) {
    return getMessage("workerLowHashRate",
        new Object[]{workerName, currentHashRate, thresholdHashRate});
  }

  static public String workerNormalHashRate(String workerName) {
    return getMessage("workerNormalHashRate", new Object[]{workerName});
  }

  static public String noJson() {
    return getMessage("noJson");
  }

  static public String loopException() {
    return getMessage("loopException");
  }

  @Autowired
  private void setMessageSource(MessageSource messageSource) {
    Messages.messageSource = messageSource;
  }

}
