package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import simulator.Broker;

import java.net.ConnectException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements Runnable {
    private static final Logger logger = LogManager.getLogger("Server");

    int id = 0;

    int counter = 0;
    float totalToll = 0;

    Broker broker;

    BlockingQueue<JSONObject> frGate = new LinkedBlockingQueue<JSONObject>();

    public Server(int id) {
        this.id = id;
    }

    public int getServerId() {
        return id;
    }

    public int getCounter() {
        return counter;
    }

    public float getTotalToll() {
        return totalToll;
    }

    @Override
    public String toString() {
        return "(" + id + ")";
    }

    public void connectBroker(Broker broker) {
        this.broker = broker;
    }

    public void receiveFrGate(JSONObject msg) {
        frGate.offer(msg);
        counter++;
    }

    // implementation
    public void run() {
        logger.info("server starts >>>>");
        new Thread("server-processor") {
            @Override
            public void run() {
                logger.info("server-processor starts >>>>");
                while (true) {
                    if (!frGate.isEmpty()) {
                        JSONObject req = frGate.poll();
                        logger.trace("frGate poll: " + req);
                        try {
                            int gateId = req.getInt("gateId");
                            boolean authorized = req.getBoolean("authorized");

                            if (authorized) {
                                int ezpayId = req.getInt("ezpayId");
                            } else {
                                String vehicleId = req.getString("vehicleId");
                            }

                            Date timestamp = (Date)req.get("timestamp");

                            float toll = calculateToll();
                            totalToll += toll;
                            updateDB();

                            // compose response
                            req.put("toll", toll);
                            sendToGate(req);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    // TODO
    private float calculateToll() {
        return 0.1f;
    }

    // TODO
    private void updateDB() {

    }

    private void sendToGate(JSONObject resp) {
        logger.trace("start sending to gate");
        try {
            if (broker == null) {
                throw new ConnectException("No connection to broker");
            }
            broker.sendToGate(resp);
            logger.trace("finish sending to gate");
        } catch (ConnectException ex) {
            ex.printStackTrace();
        }
    }
}
