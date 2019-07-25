package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import broker.Broker;
import simulator.model.StatsTableModel;
import simulator.model.TransTableModel;

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

    //    final TransactionsCache transactions = new TransactionsCache(100, this);

    BlockingQueue<JSONObject> reqQueue = new LinkedBlockingQueue<JSONObject>();
    private RequestProcessor requestProcessorThread;

    private Broker broker;
    private TransTableModel transTableModel;
    private StatsTableModel statsTableModel;

    public Server(int id) {
        this.id = id;
        requestProcessorThread = new RequestProcessor("server-request-processor", this);
    }

//    public TransactionsCache getTransactions() {
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

    public synchronized void incrementCounter() { counter++; }

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

    public void setTransTableModel(TransTableModel transTableModel) {
        this.transTableModel = transTableModel;
    }

    public void setStatsTableModel(StatsTableModel statsTableModel) {
        this.statsTableModel = statsTableModel;
    }

    @Override
    public String toString() {
        return "(" + id + ")";
    }

    public void receiveFrGate(JSONObject msg) {
        logger.trace(">>>> start offering to reqQueue >>>>");
        reqQueue.offer(msg);
        synchronized (requestProcessorThread) {
            requestProcessorThread.notify();
        }
        incrementCounter();
        logger.trace("<<<< finish offering to reqQueue <<<<");
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
        if (transTableModel != null) {
            transTableModel.insert(transaction);
        } else {
            throw new NullPointerException("tranTableModel not set");
        }
    }

    public void updateStatsTableModel() {
        if (statsTableModel != null) {
            JSONObject stats = new JSONObject();
            try {
                stats.put("totalFlow", counter);
                stats.put("totalTollX100", Math.round(totalToll*100));
                statsTableModel.update(stats);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        } else {
            throw new NullPointerException("statsTableModel not set");
        }
    }

    // implementation
    public void run() {
        logger.info(">>>> server starts >>>>");
        // request processor
        requestProcessorThread.start();
    }

    public void shutdown() {
        requestProcessorThread.shutdown();
        synchronized (requestProcessorThread) {
            requestProcessorThread.notify();
        }
        logger.info("<<<< server shutdown <<<<");
    }
}
