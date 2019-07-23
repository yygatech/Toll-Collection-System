package simulator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Random;

public class ScheduledVehicleFactory {

    private static final Logger logger = LogManager.getLogger("ScheduledVehicleFactory");

    private Random rand = new Random();

    Map<Integer, Sensor> sensors;
    int nSensor;
    int session;

    ScheduledVehicleFactory(Map<Integer, Sensor> sensors, int session) {
        this.sensors = sensors;
        nSensor = sensors.size();
        this.session  = session;
    }

    ScheduledVehicle newScheduledVehicle(int sn) {
        logger.trace("schedule vehicle: " + sn);

        int ezpayId = -1;   // TODO: ezpayId generator
        if (rand.nextInt() < 0.9) {
            ezpayId = rand.nextInt(10);
        }
        int sensorId = rand.nextInt(nSensor)+1;
        int lag = rand.nextInt(session);

        return new ScheduledVehicle(ezpayId, sensors.get(sensorId), lag);
    }
}
