package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import simulator.view.Monitor;

import java.util.concurrent.LinkedBlockingDeque;

@Deprecated
public class TransactionsCache extends LinkedBlockingDeque<JSONObject> {

//    private static final Logger logger = LogManager.getLogger("TransactionsCache");
//
//    private final Server server;
//    private Monitor monitor;
//
//    public TransactionsCache(int capacity, Server server) {
//        super(capacity);
//        this.server = server;
//    }
//
//    public Server getServer() {
//        return server;
//    }
//
//    public void setMonitor(Monitor monitor) {
//        this.monitor = monitor;
//    }
}
