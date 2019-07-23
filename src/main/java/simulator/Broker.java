package simulator;

import gate.Gate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import server.Server;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// TODO: may be saved in DB
public class Broker implements Runnable {
    private static final Logger logger = LogManager.getLogger("Broker");

    // TODO: may use round-robin
    Random rand = new Random(); // for server chosen

    Map<Integer, Server> servers = new HashMap<Integer, Server>();
    Map<Integer, Gate> gates = new HashMap<Integer, Gate>();

    BlockingQueue<JSONObject> toServer = new LinkedBlockingQueue<JSONObject>();
    BlockingQueue<JSONObject> toGate = new LinkedBlockingQueue<JSONObject>();

    Broker() {
    }

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

    void registerServer(int serverId, Server server) {
        servers.put(serverId, server);
    }

    void registerGate(int gateId, Gate gate) {
        gates.put(gateId, gate);
    }

    public void sendToServer(JSONObject req) {
        toServer.offer(req);
    }

    public void sendToGate(JSONObject resp) {
        toGate.offer(resp);
    }

    public void run() {
        logger.info("broker starts >>>>");
        // gate to server processor
        new Thread("broker-toServer") {
            @Override
            public void run() {
                logger.info("broker-toServer starts >>>>");
                while (true) {
                    if (!toServer.isEmpty()) {
                        JSONObject req = toServer.poll();
                        logger.trace("toServer poll: " + req);
                        try {
                            if (servers.size() == 0) {
                                throw new ConnectException("no server registered");
                            }
                            Server server = servers.get(rand.nextInt(servers.size())+1);
                            server.receiveFrGate(req);
                        } catch (ConnectException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }.start();

        // server to gate processor
        new Thread("broker-toGate") {
            @Override
            public void run() {
                logger.info("broker-" + "-toGate starts >>>>");
                while (true) {
                    if (!toGate.isEmpty()) {
                        JSONObject resp = toGate.poll();
                        logger.trace("toGate poll: " + resp);
                        try {
                            int gateId = resp.getInt("gateId");
                            if (!gates.containsKey(gateId)) {
                                throw new ConnectException("gate not registered");
                            }
                            gates.get(gateId).receiveFrServer(resp);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        } catch (ConnectException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }.start();

    }
}
