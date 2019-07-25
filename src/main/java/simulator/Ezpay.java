package simulator;

import broker.Broker;
import gate.Gate;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import server.Server;
import simulator.model.StatsTableModel;
import simulator.model.TransTableModel;
import simulator.view.Monitor;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Ezpay {

    private static final Logger logger = LogManager.getLogger("Ezpay");
    private static final Level level = Level.TRACE;
    private static final Properties prop = new Properties();
    private static final Random rand = new Random();

    // parameter properties
    private int nGate = 3;
    private int maxLanePerGate = 2;

    private int session = 5*1000;   // in millisecond
    private int nSession = 2;
    private int maxFlowPerSession = 10;

    private int warmup = 2000;      // in millisecond
    private int cooldown = 3000;    // in millisecond

    // vehicle sequence number (not preset)
    private int vehicleSN = 1;

    Ezpay() {
        // set logger level
        Configurator.setRootLevel(level);

        // load properties
        loadProperties();

        // clean database
        cleanDB();

        // random generator
        RandomGenerator.init(prop);

        // generate broker
        Broker broker = new Broker();

        // generate server
        Server server = new Server(1);
        server.setnGate(nGate);
        server.setBroker(broker);
        broker.registerServer(1, server);

        // generate gates with lanes
        Gate[] gates = new Gate[nGate];
        for (int i = 0; i < nGate; i++) {
            int gateId = i+1;
            int nLane = rand.nextInt(maxLanePerGate)+1;                 // (random) number lanes at this gate
            Gate gate = new Gate(gateId, rand.nextInt(2) + 1, nLane);
            gates[i] = gate;
            gate.setBroker(broker);
            broker.registerGate(gateId, gate);
        }

        logger.info("Server: " + server);
        logger.info("Gates: " + Arrays.toString(gates));
        logger.info("Broker: " + broker);

        // run simulator
        logger.info("start broker/server/gates >>>>");
//        int count = 1 + 2 + gates.length;
//        CountDownLatch latch = new CountDownLatch(count);

        Thread brokerThread = new Thread(broker, "broker");
        brokerThread.start();

        Thread serverThread = new Thread(server, "server-"+server.getServerId());
        serverThread.start();

        Thread[] gateThreads = new Thread[gates.length];
        for (int i = 0; i < gates.length; i++) {
            gateThreads[i] = new Thread(gates[i], "gate-"+gates[i].getGateId());
            gateThreads[i].start();
        }
//        CountDownLatch latch = new CountDownLatch(count);
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // install model and view
        logger.info(">>>> Start monitor >>>>");
        Monitor monitor = new Monitor();
        TransTableModel transTableModel = monitor.getTransTableModel();
        StatsTableModel statsTableModel = monitor.getStatsTableModel();
        server.setTransTableModel(transTableModel);
        server.setStatsTableModel(statsTableModel);

        try {
            Thread.sleep(warmup);                 // wait for broker/server/gates initialization
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info(">>>> Complete warmup <<<<\n");

        // scheduled vehicle factory
        ScheduledVehicleFactory factory = new ScheduledVehicleFactory(gates, session);
        ExecutorService es = Executors.newFixedThreadPool(maxFlowPerSession);
        int r = 1;
        while (r <= nSession) {
            logger.info("\n\nRound: " + r);
            int nFlow = rand.nextInt(maxFlowPerSession)+1;
            for (int i = 0;  i < nFlow; i++) {
                es.execute(factory.newScheduledVehicle(vehicleSN++));
            }

            try {
                Thread.sleep(session);                  // let scheduled vehicles pass through
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            r++;
        }

        // shut down vehicles/server/broker
        logger.info(">>>> Start cooldown: " + cooldown/1000 + "s >>>>\n");
        es.shutdown();
        try {
            Thread.sleep(cooldown);                 // wait for all transactions
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.shutdown();
        broker.shutdown();
        for (Gate gate: gates) {
            gate.shutdown();
        }
        logger.info("<<<< Complete cooldown <<<<\n");

        showStats(gates, server);                   // log stats
    }

    private void loadProperties() {
        InputStream input = null;
        try {
            ClassLoader cl = this.getClass().getClassLoader();
            input = cl.getResourceAsStream("config.properties");
            prop.load(input);

            nGate = Integer.parseInt(prop.getProperty("number.gate"));
            logger.debug("nGate: " + nGate);

            maxLanePerGate = Integer.parseInt(prop.getProperty("max.number.lane.per.gate"));
            logger.debug("maxLanePerGate: " + maxLanePerGate);

            session = Integer.parseInt(prop.getProperty("session"));
            logger.debug("session: " + session);

            nSession = Integer.parseInt(prop.getProperty("number.session"));
            logger.debug("nSession: " + nSession);

            maxFlowPerSession = Integer.parseInt(prop.getProperty("max.number.flow.per.session"));
            logger.debug("maxFlowPerSession: " + maxFlowPerSession);

            warmup = Integer.parseInt(prop.getProperty("warmup"));
            logger.debug("warmup: " + warmup);

            cooldown = Integer.parseInt(prop.getProperty("cooldown"));
            logger.debug("cooldown: " + cooldown);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void cleanDB() {
        BufferedWriter out = null;
        try {
            File db = new File("db.txt");
            out = new BufferedWriter(new FileWriter(db));
            String line = "timestamp authorization ezpay vehicle gate type lane toll";
            out.write(line);
            out.newLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showStats(Gate[] gates, Server server) {
        // simulator stats
        logger.info("Simulator: counter: " + (vehicleSN-1));

        // gates stats
        int counterGates = 0;
        float totalTollGates = 0f;
        for (Gate gate: gates) {
            counterGates += gate.getCounter();
            totalTollGates += gate.getTotalToll();
        }
        logger.info("Gates: counter: " + counterGates + " totalToll: " + String.format("%.2f", totalTollGates));

        // server stats
        logger.info("Server: counter: " + server.getCounter() + " totalToll: " + String.format("%.2f", server.getTotalToll()));
    }

    public static void main(String[] args) {
        new Ezpay();
    }
}
