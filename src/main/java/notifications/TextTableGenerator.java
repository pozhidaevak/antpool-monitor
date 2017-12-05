package notifications;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.asciithemes.a7.A7_Grids;
import worker.Worker;


import java.util.List;

public class TextTableGenerator {

    final private String tableString;
    public TextTableGenerator(List<Worker> workers) {
        /*StringBuilder builder = new StringBuilder();
        builder.append("WorkerName \tLast 10m MH \tLast 1h MH\n");
        for (Worker worker: workers) {
            builder.append(worker.getName() + "\t" + formatDouble(worker.getLast10m())
                    + "\t" + formatDouble(worker.getLast1h()) + "\n");
        }*/
        AsciiTable table = new AsciiTable();

        table.addRule();
        table.addRow("Worker Name", "Last 1h");
        table.addRule();
        for (Worker worker: workers) {
            table.addRow(worker.getName(), formatDouble(worker.getLast1h()));
            table.addRule();
        }
        CWC_LongestLine cwc = new CWC_LongestLine();
        table.getRenderer().setCWC(cwc);
        tableString =  table.render();

    }
    private String formatDouble(double hashrate) {
        return Integer.toString((int) Math.floor(hashrate / 1000));
    }

    @Override
    public String toString() {
        return tableString;
    }
}
