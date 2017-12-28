package github.antmonitor.notifications;

import github.antmonitor.notifications.INotifier;

public class StdOutNotifier implements INotifier {
    @Override
    public void send(String message) {
        System.out.println(message);
    }
}
