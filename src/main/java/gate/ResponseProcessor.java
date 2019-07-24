package gate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;

public class ResponseProcessor extends Thread {

    private static final Logger logger = LogManager.getLogger("ResponseProcessor");

    private Gate gate;
    private BlockingQueue<JSONObject> respQueue;

    public ResponseProcessor(String name, Gate gate) {
        super(name);
        this.gate = gate;
        respQueue = gate.getRespQueue();
    }

    @Override
    public void run() {
        logger.info(this.getName() + " starts >>>>");
        while (true) {
            try {
                synchronized (this) {
                    wait();
                }
                if (!respQueue.isEmpty()) {
                    JSONObject resp = respQueue.poll();
                    logger.trace("frServer poll: " +resp);
                    try {
                        int laneId = resp.getInt("laneId");
                        int tollX100 = resp.getInt("tollX100");
                        float toll = 0.01f * tollX100;
                        gate.addToTotalToll(toll);
                        gate.display(laneId, toll);
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
