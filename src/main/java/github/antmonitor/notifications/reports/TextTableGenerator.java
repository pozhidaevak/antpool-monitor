package github.antmonitor.notifications.reports;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import github.antmonitor.worker.Worker;
import java.util.List;
import org.springframework.stereotype.Component;

// Todo make pakage local
@Component
public class TextTableGenerator implements IReportGenerator {

  @Override
  public String generate(List<Worker> workers) {
    AsciiTable table = new AsciiTable();

    table.addRule();
    table.addRow("Worker Name", "Last 1h");
    table.addRule();
    for (Worker worker : workers) {
      table.addRow(worker.getName(), formatDouble(worker.getLast1h()));
      table.addRule();
    }
    CWC_LongestLine cwc = new CWC_LongestLine();
    table.getRenderer().setCWC(cwc);
    return table.render();
  }

  @Override
  public boolean requireMonospace() {
    return true;
  }

  protected String formatDouble(double hashrate) {
    return Integer.toString((int) Math.floor(hashrate / 1000));
  }

}
