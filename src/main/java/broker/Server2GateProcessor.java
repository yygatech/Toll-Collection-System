package broker;

import gate.Gate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Server2GateProcessor extends Thread {

    private static final Logger logger = LogManager.getLogger("Server2GateProcessor");

//    private Broker broker;
    private Map<Integer, Gate> gates;
    private BlockingQueue<JSONObject> server2GateQueue;

    // status
    private boolean running = false;

    Server2GateProcessor(String name, Broker broker) {
        super(name);
//        this.broker = broker;
        gates = broker.gates;
        server2GateQueue = broker.server2GateQueue;
    }

    @Override
    public void run() {
        logger.info("broker-server2Gate-processor starts >>>>");
        running = true;
        while (running) {
            try {
                synchronized (this) {
                    wait();
                }
                if (!server2GateQueue.isEmpty()) {
                    JSONObject resp = server2GateQueue.poll();
                    logger.trace("server2GateQueue poll: " + resp);
                    try {
                        int gateId = resp.getInt("gateId");
                        if (!gates.containsKey(gateId)) {
                            throw new ConnectException("gate not registered");
                        }
                        gates.get(gateId).receiveFrServer(resp);
                    } catch (JSONException | ConnectException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        logger.info("<<<< " +  this.getName() + " stops <<<<");
    }

    public void shutdown() {
        running = false;
    }
}
