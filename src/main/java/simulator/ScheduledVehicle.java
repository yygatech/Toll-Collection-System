package simulator;

import gate.Gate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScheduledVehicle implements Runnable {

    private static final Logger logger = LogManager.getLogger("ScheduledVehicle");

    Sensor sensor;
    int ezpayId;
    int lag;

    ScheduledVehicle(int ezpayId, Sensor sensor, int lag) {
        this.ezpayId = ezpayId;
        this.sensor = sensor;
        this.lag = lag;
    }

    // implementation
    public void run() {
        try {
            Thread.sleep(lag);  // schedule pass by lag time

            Gate gate = sensor.getAPI().getGate();
            logger.trace("\n\n" + this + " passes gate-" + gate.getGateId());
            gate.receiveFrSensor(ezpayId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "(sensorId: " + sensor.getGlobalId() + ", ezpayId: " + ezpayId + ", lag: " + lag + ")";
    }
}
