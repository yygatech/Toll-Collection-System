package gate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class SensorInputProcessor extends Thread {

    private static final Logger logger = LogManager.getLogger("SensorInputProcessor");

    private Gate gate;
    private BlockingQueue<JSONObject> sensorInputQueue;

    public SensorInputProcessor(String name, Gate gate) {
        super(name);
        this.gate = gate;
        sensorInputQueue = gate.getSensorInputQueue();
    }

    @Override
    public void run() {
        logger.info(this.getName() + " starts >>>>");
        while (true) {
            try {
                synchronized (this) {
                    wait();
                }
                if (!sensorInputQueue.isEmpty()) {
                    JSONObject sensorMsg = sensorInputQueue.poll();
                    try {
                        logger.trace("frSensor poll: " + sensorMsg);
                        int laneId = sensorMsg.getInt("laneId");
                        int ezpayId = sensorMsg.getInt("ezpayId");
                        boolean authorized = (ezpayId > 0);

                        // create request msg
                        JSONObject req = sensorMsg;
                        req.put("gateId", gate.getGateId());
                        req.put("gateType", gate.getGateType());
                        req.put("authorized", authorized);
                        if (authorized) {
                            gate.lightGreen(laneId);
                            req.put("ezpayId", ezpayId);
                        } else {
                            gate.lightYellow(laneId);
                            req.put("vehicleId", gate.takePicture(laneId));
                        }
                        req.put("timestamp", new Date());
                        gate.sendToServer(req);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
