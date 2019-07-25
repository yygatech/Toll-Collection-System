package gate;

import gate.api.CameraAPI;
import gate.api.DisplayAPI;
import gate.api.LightAPI;
import gate.api.SensorAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import broker.Broker;

import java.net.ConnectException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Gate implements Runnable {

    private static final Logger logger = LogManager.getLogger("Gate");

    private int id = 0;
    private int type;
    private int nLane = 1;

    private int counter;
    private float totalToll;

    private Lane[] lanes = new Lane[1];
    private SensorAPI sensorAPI = new SensorAPI(this);
    private CameraAPI cameraAPI = new CameraAPI();
    private LightAPI lightAPI = new LightAPI();
    private DisplayAPI displayAPI = new DisplayAPI();

    BlockingQueue<JSONObject> sensorInputQueue = new LinkedBlockingQueue<JSONObject>();
    BlockingQueue<JSONObject> respQueue = new LinkedBlockingQueue<JSONObject>();
    private SensorInputProcessor sensorInputProcessorThread;
    private ResponseProcessor responseProcessorThread;

    private Broker broker;

    public Gate(int id, int type) {
        this.id = id;
        this.type = type;
        counter = 0;
        totalToll = 0f;
        lanes[0] = new Lane(1, this);
        sensorInputProcessorThread = new SensorInputProcessor("gate-" + id + "-sensor-input-processor", this);
        responseProcessorThread = new ResponseProcessor("gate-" + id + "-response-processor", this);
    }

    public Gate(int id, int type, int nLane) {
        this(id, type);
        this.nLane = nLane;
        lanes = new Lane[nLane];
        for (int i = 0; i < nLane; i++) {
            lanes[i] = new Lane(i+1, this);
        }
    }

    public int getGateId() {
        return id;
    }

    public int getGateType() {
        return type;
    }

    public int getCounter() {
        return counter;
    }

    public float getTotalToll() {
        return totalToll;
    }

    public synchronized void addToTotalToll(float toll) {
        totalToll += toll;
    }

    public Lane[] getLanes() {
        return lanes;
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

    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    @Override
    public String toString() {
        return "(id: " + id + ", type: " + type + ", nLane: " + nLane + ")";
    }

    public void receiveFrSensor(JSONObject sensorMsg) {
        sensorInputQueue.offer(sensorMsg);
        counter++;
        synchronized (sensorInputProcessorThread) {
            sensorInputProcessorThread.notify();
        }
    }

    public void receiveFrServer(JSONObject resp) {
        respQueue.offer(resp);
        synchronized (responseProcessorThread) {
            responseProcessorThread.notify();
        }
    }

    public void sendToServer(JSONObject req) {
        logger.trace("start sending to server >>>>");
        try {
            if (broker == null) {
                throw new ConnectException("No connection to broker");
            }
            broker.sendToServer(req);
            logger.trace("finish sending to server <<<<");
        } catch (ConnectException ex) {
            ex.printStackTrace();
        }
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
    }
}
