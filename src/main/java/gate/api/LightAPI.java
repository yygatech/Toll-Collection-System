package gate.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LightAPI {
    private static final Logger logger = LogManager.getLogger("LightAPI");

    // TODO: implementation
    public void lightGreen(int laneId) {
        logger.info("[green at lane " + laneId + "]");
    }

    // TODO: implementation
    public void lightYellow(int laneId) {
        logger.info("[yellow at lane " + laneId + "]");
    }
}
