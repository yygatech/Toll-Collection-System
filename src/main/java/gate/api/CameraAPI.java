package gate.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulator.RandomGenerator;

public class CameraAPI {
    private static final Logger logger = LogManager.getLogger("CameraAPI");

    // TODO: real implementation
    public String takePicture(int laneId) {
        logger.info("[picture at lane " + laneId + "]");
        return RandomGenerator.generateVehicleId();
    }
}
