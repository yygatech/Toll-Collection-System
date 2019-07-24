package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import server.db.Repository;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class RequestProcessor extends Thread {

    private static final Logger logger = LogManager.getLogger("RequestProcessor");
    private static final Random rand = new Random();

    private Server server;
    private BlockingQueue<JSONObject> reqQueue;


    RequestProcessor(String name, Server server) {
        super(name);
        this.server = server;
        reqQueue = server.getReqQueue();
    }

    @Override
    public void run() {
        logger.info(this.getName() + " starts >>>>");
        while (true) {
            try {
                synchronized (this) {
                    this.wait();
                }
                if (!reqQueue.isEmpty()) {
                    JSONObject req = reqQueue.poll();
                    logger.trace("frGate poll: " + req);
                    try {
                        boolean authorized = req.getBoolean("authorized");
                        Integer ezpayId = null;
                        String vehicleId = null;
                        if (authorized) {
                            ezpayId = req.getInt("ezpayId");
                            vehicleId = Repository.queryVehicleId(ezpayId);
                            req.put("vehicleId", vehicleId);
                        } else {
                            vehicleId = req.getString("vehicleId");
                        }

                        int tollX100 = calculateTollX100(req);
                        req.put("tollX100", tollX100);
                        float toll = 0.01f * tollX100;
                        server.addToTotalToll(toll);

                        // update database
                        Date timestamp = (Date) req.get("timestamp");
                        String ts = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss").format(timestamp);
                        int gateType = req.getInt("gateType");
                        int gateId = req.getInt("gateId");
                        int laneId = req.getInt("laneId");

                        server.insertIntoTransTableModel(req);

                        Repository.updateDB(ts, authorized, ezpayId, vehicleId, gateId, gateType, laneId, toll);    // TODO: can be optimized

                        // compose response message
                        server.sendToGate(req);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int calculateTollX100(JSONObject req) {
        float toll = calculateToll(req);
        return Math.round(toll * 100);
    }

    // TODO: real implementation
    private float calculateToll(JSONObject req) {
        try {
//            boolean authorized = req.getBoolean("authorized");
            int type = req.getInt("gateType");
            if (type == 1) {
                if (server.getnGate() > 0) {
                    return 2f * (1+req.getInt("gateId"))/server.getnGate();
                } else {
                    throw new IllegalArgumentException("no gate bound");
                }
            } else if (type == 2) {
                return 2f * rand.nextFloat();
            } else {
                throw new IllegalArgumentException("no type assigned to the gate!");
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return 0f;
        }
        return 0f;
    }
}
