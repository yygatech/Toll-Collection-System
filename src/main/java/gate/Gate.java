package gate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import simulator.Broker;

import java.net.ConnectException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Gate implements Runnable {

    private static final Logger logger = LogManager.getLogger("Gate");

    int id = 0;
    int type;
    int nLane = 1;

    int counter;
    float totalToll;

    SensorAPI sensorAPI = new SensorAPI(this);
    CameraAPI cameraAPI = new CameraAPI();
    LightAPI lightAPI = new LightAPI();
    DisplayAPI displayAPI = new DisplayAPI();

    Broker broker;

    BlockingQueue<Integer> frSensor = new LinkedBlockingQueue<Integer>();
    BlockingQueue<JSONObject> frServer = new LinkedBlockingQueue<JSONObject>();

    public Gate(int id, int type) {
        this.id = id;
        this.type = type;
        counter = 0;
        totalToll = 0f;
    }

    public Gate(int id, int type, int nLane) {
        this(id, type);
        this.nLane = nLane;
    }

    public int getGateId() {
        return id;
    }

    public int getCounter() {
        return counter;
    }

    public float getTotalToll() {
        return totalToll;
    }

    public SensorAPI getSensorAPI() {
        return sensorAPI;
    }

    public CameraAPI getCameraAPI() {
        return cameraAPI;
    }

    public LightAPI getLightAPI() {
        return lightAPI;
    }

    public DisplayAPI getDisplayAPI() {
        return displayAPI;
    }

    @Override
    public String toString() {
        return "(id: " + id + ", type: " + type + ", nLane: " + nLane + ")";
    }

    public void connectBroker(Broker broker) {
        this.broker = broker;
    }

    public void receiveFrSensor(int ezpayId) {
        frSensor.offer(ezpayId);
        counter++;
    }

    public void receiveFrServer(JSONObject resp) {
        frServer.offer(resp);
    }

    // implementation
    public void run() {
        logger.info("gate-" + id + "  starts >>>>");
        // to server processor
        new Thread("gate-" + id + "-frSensor") {
            @Override
            public void run() {
                logger.info("gate-" + id + "-frSensor starts >>>>");
                while (true) {
                    if (!frSensor.isEmpty()) {
                        int ezpayId = frSensor.poll();
                        logger.trace("frSensor poll: " + ezpayId);
                        boolean authorized = (ezpayId > 0);

                        // create msg
                        JSONObject req = new JSONObject();
                        try {
                            req.put("gateId", id);
                            req.put("authorized", authorized);
                            if (authorized) {
                                lightAPI.lightGreen();
                                req.put("ezpayId", ezpayId);
                            } else {
                                lightAPI.lightYellow();
                                req.put("vehicleId", cameraAPI.takePicture());
                            }
                            req.put("timestamp", new Date());
                            sendToServer(req);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }.start();

        // from server processor
        new Thread("gate-" + id + "-frServer") {
            @Override
            public void run() {
                logger.info("gate-" + id + "-frServer starts >>>>");
                while (true) {
                    if (!frServer.isEmpty()) {
                        JSONObject resp = frServer.poll();
                        logger.trace("frServer poll: " +resp);
                        try {
                            float toll = (float)resp.getDouble("toll");
                            totalToll += toll;
                            // TODO: different lanes
                            displayAPI.display(toll);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }.start();

    }

    private void sendToServer(JSONObject req) {
        try {
            if (broker == null) {
                throw new ConnectException("No connection to broker");
            }
            broker.sendToServer(req);
        } catch (ConnectException ex) {
            ex.printStackTrace();
        }

    }
}
