package broker;

import gate.Gate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import server.Server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// TODO: may be saved in DB
public class Broker implements Runnable {

    private static final Logger logger = LogManager.getLogger("Broker");

    Map<Integer, Server> servers = new HashMap<Integer, Server>();
    Map<Integer, Gate> gates = new HashMap<Integer, Gate>();

    BlockingQueue<JSONObject> gate2ServerQueue = new LinkedBlockingQueue<JSONObject>();
    BlockingQueue<JSONObject> server2GateQueue = new LinkedBlockingQueue<JSONObject>();
    private Gate2ServerProcessor gate2ServerProcessorThread = new Gate2ServerProcessor("broker-gate2Server-processor", this);
    private Server2GateProcessor server2GateProcessorThread = new Server2GateProcessor("broker-server2Gate-processor", this);

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("server: ");
        sb.append(servers.toString());
        sb.append("\t");
        sb.append("gates: ");
        sb.append(gates.toString());
        return sb.toString();
    }

    public void registerServer(int serverId, Server server) {
        servers.put(serverId, server);
    }

    public void registerGate(int gateId, Gate gate) {
        gates.put(gateId, gate);
    }

    public void sendToServer(JSONObject req) {
        gate2ServerQueue.offer(req);
        synchronized (gate2ServerProcessorThread) {
            gate2ServerProcessorThread.notify();
        }
    }

    public void sendToGate(JSONObject resp) {
        server2GateQueue.offer(resp);
        synchronized (server2GateProcessorThread) {
            server2GateProcessorThread.notify();
        }
    }

    @Override
    public void run() {
        logger.info("broker starts >>>>");

        // gate to server processor
        gate2ServerProcessorThread.start();

        // server to gate processor
        server2GateProcessorThread.start();
    }

    public void shutdown() {
        gate2ServerProcessorThread.shutdown();
        synchronized (gate2ServerProcessorThread) {
            gate2ServerProcessorThread.notify();
        }
        server2GateProcessorThread.shutdown();
        synchronized (server2GateProcessorThread) {
            server2GateProcessorThread.notify();
        }
        logger.info("<<<< broker shutdown <<<<");
    }
}
