package broker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import server.Server;

import java.net.ConnectException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class Gate2ServerProcessor extends Thread {

    private static final Logger logger = LogManager.getLogger("Gate2ServerProcessor");
    private static final Random rand = new Random();

//    private Broker broker;
    private Map<Integer, Server> servers;
    private BlockingQueue<JSONObject> gate2ServerQueue;

    // status
    private boolean running = false;

    Gate2ServerProcessor(String name, Broker broker) {
        super(name);
//        this.broker = broker;
        servers = broker.servers;
        gate2ServerQueue = broker.gate2ServerQueue;
    }

    @Override
    public void run() {
        logger.info("broker-gate2Server-processor starts >>>>");
        running = true;
        while (running) {
            try {
                synchronized (this) {
                    wait();
                }
                if (!gate2ServerQueue.isEmpty()) {
                    JSONObject req = gate2ServerQueue.poll();
                    logger.trace("gate2ServerQueue poll: " + req);
                    try {
                        if (servers.size() == 0) {
                            throw new ConnectException("no server registered");
                        }
                        Server server = servers.get(rand.nextInt(servers.size())+1);    // TODO: may use Round-Robin
                        server.receiveFrGate(req);
                    } catch (ConnectException ex) {
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
