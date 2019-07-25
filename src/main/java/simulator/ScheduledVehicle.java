package simulator;

import gate.Lane;
import gate.api.SensorAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class ScheduledVehicle implements Runnable {

    private static final Logger logger = LogManager.getLogger("ScheduledVehicle");

    int ezpayId;
    Lane lane;
    int lag;

    ScheduledVehicle(int ezpayId, Lane lane, int lag) {
        this.ezpayId = ezpayId;
        this.lane = lane;
        this.lag = lag;
    }

    // implementation
    public void run() {
        try {
            Thread.sleep(lag);  // schedule pass by lag time

            SensorAPI sensorAPI = lane.getGate().getSensorAPI();
            logger.trace("\n\n" + this + " passing");
            JSONObject sensorMsg = new JSONObject();
            try {
                sensorMsg.put("laneId", lane.getLaneId());
                sensorMsg.put("ezpayId", ezpayId);
                sensorAPI.detect(sensorMsg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "(ezpayId-" + ezpayId + ", gateId-" + lane.getGateId() + ", laneId-" + lane.getLaneId() + ", lag-" + lag + ")";
    }
}
