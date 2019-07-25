package simulator.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.table.DefaultTableModel;

public class StatsTableModel extends DefaultTableModel {

    private static final Logger logger = LogManager.getLogger("StatsTableModel");

    String[] columnNames = new String[] {"Total Flow", "Total Toll"};
    Object[][] data = new Object[1][2];

    public StatsTableModel() {
        super();

        setDataVector(data, columnNames);
    }

    // update stats
    public void update(JSONObject stats) {
        logger.trace(">>>> Start updating stats table model >>>>");
        removeRow(0);
        addRow(extractData(stats));
        logger.trace("current stats model: " + getDataVector());
        logger.trace("<<<< Finish updating stats table model <<<<");
    }

    private Object[] extractData(JSONObject msg) {
        logger.trace("stats: " + msg);
        Object[] dataLine = new Object[getColumnCount()];
        try {
            int totalFlow = msg.getInt("totalFlow");
            int totalTollX100 = msg.getInt("totalTollX100");
            float totalToll = 0.01f * totalTollX100;

            dataLine = new Object[] {
                    totalFlow,
                    String.format("$%.2f", totalToll)};
            return dataLine;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return dataLine;
        }
    }
}
