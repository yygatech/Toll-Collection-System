package simulator;

import gate.Gate;
import gate.Lane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class ScheduledVehicleFactory {

    private static final Logger logger = LogManager.getLogger("ScheduledVehicleFactory");

    private Random rand = new Random();

    Gate[] gates;
    int session;

    ScheduledVehicleFactory(Gate[] gates, int session) {
        this.gates = gates;
        this.session  = session;
    }

    ScheduledVehicle newScheduledVehicle(int sn) {
        logger.trace("schedule vehicle: " + sn);

        // random lane
        Lane[] lanes = gates[rand.nextInt(gates.length)].getLanes();
        Lane randomLane = lanes[rand.nextInt(lanes.length)];
        // random lag
        int randomLag = rand.nextInt(session);

        return new ScheduledVehicle(RandomGenerator.generateEzpayId(), randomLane, randomLag);
    }
}
