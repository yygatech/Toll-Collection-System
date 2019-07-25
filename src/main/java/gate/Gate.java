package gate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import simulator.Broker;

import java.net.ConnectException;
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

<<<<<<< HEAD
    BlockingQueue<JSONObject> sensorInputQueue = new LinkedBlockingQueue<JSONObject>();
    BlockingQueue<JSONObject> respQueue = new LinkedBlockingQueue<JSONObject>();
    private SensorInputProcessor sensorInputProcessorThread;
    private ResponseProcessor responseProcessorThread;
=======
    Broker broker;
>>>>>>> parent of cc686aa... v0.2

    BlockingQueue<Integer> frSensor = new LinkedBlockingQueue<Integer>();
    BlockingQueue<JSONObject> frServer = new LinkedBlockingQueue<JSONObject>();

    public Gate(int id, int type) {
        this.id = id;
        this.type = type;
        counter = 0;
        totalToll = 0f;
<<<<<<< HEAD
        lanes[0] = new Lane(1, this);
        sensorInputProcessorThread = new SensorInputProcessor("gate-" + id + "-sensor-input-processor", this);
        responseProcessorThread = new ResponseProcessor("gate-" + id + "-response-processor", this);
=======
>>>>>>> parent of cc686aa... v0.2
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

<<<<<<< HEAD
    public void setBroker(Broker broker) {
        this.broker = broker;
    }

=======
>>>>>>> parent of cc686aa... v0.2
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
<<<<<<< HEAD
        synchronized (sensorInputProcessorThread) {
            sensorInputProcessorThread.notify();
        }
    }

    public void receiveFrServer(JSONObject resp) {
        respQueue.offer(resp);
        synchronized (responseProcessorThread) {
            responseProcessorThread.notify();
        }
=======
    }

    public void receiveFrServer(JSONObject resp) {
        frServer.offer(resp);
>>>>>>> parent of cc686aa... v0.2
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
<<<<<<< HEAD
    }

    public String takePicture(int laneId) {
        return cameraAPI.takePicture(laneId);
    }

    public void display(int laneId, float toll) {
        displayAPI.display(laneId, toll);
    }

    public void lightGreen(int laneId) {
        lightAPI.lightGreen(laneId);
    }

    public void lightYellow(int laneId) {
        lightAPI.lightYellow(laneId);
    }

    // implementation
    public void run() {
        logger.info("gate-" + id + "  starts >>>>");

        // sensor input processor
        sensorInputProcessorThread.start();

        // response processor
        responseProcessorThread.start();
    }

    public void shutdown() {
        sensorInputProcessorThread.shutdown();
        synchronized (sensorInputProcessorThread) {
            sensorInputProcessorThread.notify();
        }
        responseProcessorThread.shutdown();
        synchronized (responseProcessorThread) {
            responseProcessorThread.notify();
        }
        logger.info("<<<< gate shutdown <<<<");
=======

>>>>>>> parent of cc686aa... v0.2
    }
}
