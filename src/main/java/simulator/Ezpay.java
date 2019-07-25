package simulator;

import gate.Gate;
import gate.SensorAPI;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import server.Server;
import simulator.model.StatsTableModel;
import simulator.model.TransTableModel;
import simulator.view.Monitor;

import java.util.*;
import java.util.concurrent.*;

public class Ezpay {
    private static final Logger logger = LogManager.getLogger("Ezpay");

    private Random rand = new Random();

    int nGate = 3;
    int maxLanePerGate = 2;

<<<<<<< HEAD
    private int warmup = 2000;      // in millisecond
    private int cooldown = 3000;    // in millisecond
=======
    int round = 4;
    int session = 5*1000; // in millisecond
    int maxFlowPerSession = 10;
>>>>>>> parent of cc686aa... v0.2

    int vehicleSN = 1;

    Ezpay() {
        Configurator.setRootLevel(Level.INFO);
        // generate broker
        Broker broker = new Broker();

        // generate server
        Server server = new Server(1);
        server.connectBroker(broker);
        broker.registerServer(1, server);

        // generate gates
        Gate[] gates = new Gate[nGate];
        Map<Integer, Sensor> sensors = new HashMap<Integer, Sensor>();
        int sensorGlobalId = 1;
        for (int i = 0; i < nGate; i++) {
            int gateId = i+1;
            int nLane = rand.nextInt(maxLanePerGate)+1;
            Gate gate = new Gate(gateId, rand.nextInt(2) + 1, nLane);
            gates[i] = gate;
            gate.connectBroker(broker);
            broker.registerGate(gateId, gate);

            // generate sensors
            SensorAPI sensorAPI = gate.getSensorAPI();
            for (int j = 0; j < nLane; j++) {
                int sensorLocalId = j+1;
                Sensor sensor = new Sensor(sensorAPI, sensorLocalId, sensorGlobalId);
                sensors.put(sensorGlobalId, sensor);
                sensorGlobalId++;
            }
        }

        System.out.println("server: " + server);
        System.out.println("gates: " + Arrays.toString(gates));
        System.out.println("broker: " + broker);

        // run simulator
        logger.info("start broker/server/gates >>>>");
//        int count = 1 + 2 + gates.length;
//        CountDownLatch latch = new CountDownLatch(count);

<<<<<<< HEAD
        Thread brokerThread = new Thread(broker, "broker");
        brokerThread.start();

        Thread serverThread = new Thread(server, "server-"+server.getServerId());
        serverThread.start();

        Thread[] gateThreads = new Thread[gates.length];
        for (int i = 0; i < gates.length; i++) {
            gateThreads[i] = new Thread(gates[i], "gate-"+gates[i].getGateId());
            gateThreads[i].start();
=======
        new Thread(broker, "brkr").start();
        new Thread(server, "sr-"+server.getServerId()).start();
        for (int i = 0; i < gates.length; i++) {
            new Thread(gates[i], "gt-"+gates[i].getGateId()).start();
>>>>>>> parent of cc686aa... v0.2
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
            Thread.sleep(1000);             // wait for broker/server/gates initialization
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // scheduled vehicle factory
        ScheduledVehicleFactory factory = new ScheduledVehicleFactory(sensors, session);
        ExecutorService es = Executors.newFixedThreadPool(maxFlowPerSession);
        int n = 1;
        while (n++ < round) {
            logger.info("\n\nround: " + (n-1));
            int nFlow = rand.nextInt(maxFlowPerSession)+1;
            for (int i = 0;  i < nFlow; i++) {
                es.execute(factory.newScheduledVehicle(vehicleSN++));
            }

            try {
                Thread.sleep(session);      // let scheduled vehicles pass through
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
<<<<<<< HEAD

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
=======
        es.shutdown();

        showStats(gates, server);
>>>>>>> parent of cc686aa... v0.2

        showStats(gates, server);                   // log stats
    }

    void showStats(Gate[] gates, Server server) {
        // simulator stats
        logger.info("Simulater:\ncounter: " + (vehicleSN-1));

        // gates stats
        int counterGates = 0;
        float totalTollGates = 0f;
        for (Gate gate: gates) {
            counterGates += gate.getCounter();
            totalTollGates += gate.getTotalToll();
        }
        logger.info("Gates:\ncounter: " + counterGates + " totalToll: " + totalTollGates);

        // server stats
        logger.info("Server:\ncounter: " + server.getCounter() + " totalToll: " + server.getTotalToll());
    }

    public static void main(String[] args) {
        new Ezpay();
    }
}
