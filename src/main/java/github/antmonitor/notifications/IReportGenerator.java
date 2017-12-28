package github.antmonitor.notifications;

import github.antmonitor.worker.Worker;

import java.util.List;

public interface IReportGenerator {
  String generate(List<Worker> workers);
  boolean requireMonospace();
}
