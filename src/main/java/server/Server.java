package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import broker.Broker;
import server.model.TransTableModel;
import server.view.Monitor;

import java.net.ConnectException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements Runnable {
    private static final Logger logger = LogManager.getLogger("Server");
    private static final Random rand = new Random();

    private int id = 0;
    private int nGate = 0;

    private int counter = 0;
    private float totalToll = 0;

    //    final Transactions transactions = new Transactions(100, this);
    private final Monitor monitor = new Monitor();
    private final TransTableModel transTableModel = monitor.getTransTableModel();

    private BlockingQueue<JSONObject> reqQueue = new LinkedBlockingQueue<JSONObject>();
    private Thread requestProcessorThread = new RequestProcessor("server-request-processor", this);

    private Broker broker;

    public Server(int id) {
        this.id = id;
    }

//    public Transactions getTransactions() {
//        return transactions;
//    }

    public int getServerId() {
        return id;
    }

    public int getnGate() {
        return nGate;
    }

    public void setnGate(int nGate) {
        this.nGate = nGate;
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

    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    public Broker getBroker() {
        return broker;
    }

    public BlockingQueue<JSONObject> getReqQueue() {
        return reqQueue;
    }

    @Override
    public String toString() {
        return "(" + id + ")";
    }

    public void receiveFrGate(JSONObject msg) {
        reqQueue.offer(msg);
        synchronized (requestProcessorThread) {
            requestProcessorThread.notify();
        }
        counter++;
    }

    public void sendToGate(JSONObject resp) {
        logger.trace("start sending to gate >>>>");
        try {
            if (broker == null) {
                throw new ConnectException("No connection to broker");
            }
            broker.sendToGate(resp);
            logger.trace("finish sending to gate <<<<");
        } catch (ConnectException ex) {
            ex.printStackTrace();
        }
    }

    public void insertIntoTransTableModel(JSONObject transaction) {
        transTableModel.insert(transaction);
    }

    // implementation
    public void run() {
        logger.info("server starts >>>>");
        // request processor
        requestProcessorThread.start();
    }
}
