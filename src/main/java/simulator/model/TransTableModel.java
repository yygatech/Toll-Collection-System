package simulator.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransTableModel extends DefaultTableModel {

    private static final Logger logger = LogManager.getLogger("TransTableModel");

    String[] columnNames = new String[] {"No.","Timestamp", "Authorization", "Ezpay", "Plate", "Gate Type", "Gate", "Lane", "Toll"};
    Object[][] data = new Object[1][9];

    public TransTableModel() {
        super();

        setDataVector(data, columnNames);
    }

    // insert new table model data
    public void insert(JSONObject lastTransaction) {
        logger.trace(">>>> Start inserting into trans table model >>>>");
        insertRow(0, extractData(lastTransaction));
        logger.trace("current trans model: " + getDataVector());
        logger.trace("<<<< Finish inserting into trans table model <<<<");
    }

    private Object[] extractData(JSONObject msg) {
        logger.trace("lastTransaction: " + msg);
        Object[] dataLine = new Object[getColumnCount()];
        try {
            Date timestamp = (Date) msg.get("timestamp");
            String ts = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss").format(timestamp);
            boolean authorized = msg.getBoolean("authorized");
            Integer ezpayId = msg.getInt("ezpayId");
            if (ezpayId <= 0) ezpayId = null;
            String vehicleId = msg.getString("vehicleId");
            int gateType = msg.getInt("gateType");
            int gateId = msg.getInt("gateId");
            int laneId = msg.getInt("laneId");
            int tollX100 = msg.getInt("tollX100");
            float toll = 0.01f * tollX100;

            dataLine = new Object[] {
                    getRowCount(),
                    ts,
                    authorized ? "Y" : "N",
                    ezpayId == null ? "N/A" : ezpayId,
                    vehicleId,
                    gateType == 1 ? "G1" : "G2",
                    gateId,
                    laneId,
                    String.format("$%.2f", toll)};
            return dataLine;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return dataLine;
        }
    }
}
