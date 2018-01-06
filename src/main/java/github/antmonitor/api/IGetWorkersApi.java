package github.antmonitor.api;

import github.antmonitor.worker.Worker;
import java.io.IOException;
import java.util.Map;

public interface IGetWorkersApi {
   Map<String, Worker> requestWorkers() throws IOException;
   String getUrl();
}
