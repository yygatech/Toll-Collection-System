package gate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DisplayAPI {
    private static final Logger logger = LogManager.getLogger("DisplayAPI");

    // TODO: implementation
    void display(float toll) {
        logger.info("display toll: " + toll);
    }
}
