package github.antmonitor.notifications;

public class StdOutNotifier implements INotifier {

  @Override
  public void send(String message) {
    System.out.println(message);
  }
}
