package gate.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DisplayAPI {
    private static final Logger logger = LogManager.getLogger("DisplayAPI");

    // TODO: real implementation
    public void display(int laneId, float toll) {
        logger.info("[display " + String.format("%.2f", toll) + " at lane " + laneId + "]");
    }
}
