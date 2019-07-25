package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
<<<<<<< HEAD
import broker.Broker;
import simulator.model.StatsTableModel;
import simulator.model.TransTableModel;
=======
import simulator.Broker;
>>>>>>> parent of cc686aa... v0.2

import java.net.ConnectException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements Runnable {
    private static final Logger logger = LogManager.getLogger("Server");

    int id = 0;

    int counter = 0;
    float totalToll = 0;

<<<<<<< HEAD
    //    final TransactionsCache transactions = new TransactionsCache(100, this);

    BlockingQueue<JSONObject> reqQueue = new LinkedBlockingQueue<JSONObject>();
    private RequestProcessor requestProcessorThread;

    private Broker broker;
    private TransTableModel transTableModel;
    private StatsTableModel statsTableModel;
=======
    Broker broker;

    BlockingQueue<JSONObject> frGate = new LinkedBlockingQueue<JSONObject>();
>>>>>>> parent of cc686aa... v0.2

    public Server(int id) {
        this.id = id;
        requestProcessorThread = new RequestProcessor("server-request-processor", this);
    }

<<<<<<< HEAD
//    public TransactionsCache getTransactions() {
//        return transactions;
//    }

=======
>>>>>>> parent of cc686aa... v0.2
    public int getServerId() {
        return id;
    }

    public int getCounter() {
        return counter;
    }

    public synchronized void incrementCounter() { counter++; }

    public float getTotalToll() {
        return totalToll;
    }

    @Override
    public String toString() {
        return "(" + id + ")";
    }

    public void connectBroker(Broker broker) {
        this.broker = broker;
    }

    public void receiveFrGate(JSONObject msg) {
        frGate.offer(msg);
        counter++;
    }

<<<<<<< HEAD
    public void setTransTableModel(TransTableModel transTableModel) {
        this.transTableModel = transTableModel;
    }

    public void setStatsTableModel(StatsTableModel statsTableModel) {
        this.statsTableModel = statsTableModel;
=======
    // implementation
    public void run() {
        logger.info("server starts >>>>");
        new Thread("server-processor") {
            @Override
            public void run() {
                logger.info("server-processor starts >>>>");
                while (true) {
                    if (!frGate.isEmpty()) {
                        JSONObject req = frGate.poll();
                        logger.trace("frGate poll: " + req);
                        try {
                            int gateId = req.getInt("gateId");
                            boolean authorized = req.getBoolean("authorized");

                            if (authorized) {
                                int ezpayId = req.getInt("ezpayId");
                            } else {
                                String vehicleId = req.getString("vehicleId");
                            }

                            Date timestamp = (Date)req.get("timestamp");

                            float toll = calculateToll();
                            totalToll += toll;
                            updateDB();

                            // compose response
                            req.put("toll", toll);
                            sendToGate(req);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }.start();
>>>>>>> parent of cc686aa... v0.2
    }

    // TODO
    private float calculateToll() {
        return 0.1f;
    }

<<<<<<< HEAD
    public void receiveFrGate(JSONObject msg) {
        logger.trace(">>>> start offering to reqQueue >>>>");
        reqQueue.offer(msg);
        synchronized (requestProcessorThread) {
            requestProcessorThread.notify();
        }
        incrementCounter();
        logger.trace("<<<< finish offering to reqQueue <<<<");
=======
    // TODO
    private void updateDB() {

>>>>>>> parent of cc686aa... v0.2
    }

    private void sendToGate(JSONObject resp) {
        logger.trace("start sending to gate");
        try {
            if (broker == null) {
                throw new ConnectException("No connection to broker");
            }
            broker.sendToGate(resp);
            logger.trace("finish sending to gate");
        } catch (ConnectException ex) {
            ex.printStackTrace();
        }
    }
<<<<<<< HEAD

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
=======
>>>>>>> parent of cc686aa... v0.2
}
