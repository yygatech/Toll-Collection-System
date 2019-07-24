package server.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransTableModel extends DefaultTableModel {

    private static final Logger logger = LogManager.getLogger("TransTableModel");

    public TransTableModel(Object[][] data, String[] columnNames) {
        super(data, columnNames);
    }

    // update table model data
    public void insert(JSONObject lastTransaction) {
        logger.trace(">>>> Updating trans table model <<<<");
        insertRow(0, extractData(lastTransaction));
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
            dataLine = new Object[] {ts, authorized, "" + ezpayId, vehicleId, gateType, gateId, laneId, tollX100, toll};
            return dataLine;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return dataLine;
        }
    }
}
