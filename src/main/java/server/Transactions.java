package server;

import server.view.Monitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;

@Deprecated
public class Transactions extends LinkedBlockingDeque<JSONObject> {

//    private static final Logger logger = LogManager.getLogger("Transactions");
//
//    private final Server server;
//    private Monitor monitor;
//
//    public Transactions(int capacity, Server server) {
//        super(capacity);
//        this.server = server;
//
//        JSONObject sample = new JSONObject();
//        try {
//            sample.put("timestamp", new Date());
//            sample.put("authorized", true);
//            sample.put("ezpayId", 1);
//            sample.put("vehicleId", "SAMPLE0");
//            sample.put("gateId", 1);
//            sample.put("gateType", 1);
//            sample.put("laneId", 1);
//            sample.put("tollX100", 10);
//            push(sample);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Server getServer() {
//        return server;
//    }
//
//    public void setMonitor(Monitor monitor) {
//        this.monitor = monitor;
//    }
//
//    synchronized void pushAndMonitor(JSONObject jsonObject) {
//        logger.debug("start to add transaction >>>>");
//        logger.trace("remaining capacity: " + remainingCapacity());
//        if (remainingCapacity() == 0) {
//            logger.info("capacity reached");
//            removeLast();
//        }
//        push(jsonObject);
//    }
}
