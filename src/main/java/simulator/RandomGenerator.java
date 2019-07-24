package simulator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.Random;

public class RandomGenerator {

    private static final Logger logger = LogManager.getLogger("RandomGenerator");
    private static final Random rand = new Random();
    private static final String alphaNumericPool = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // parameter properties
    private static int totalEzpay = 10000;
    private static double ezpayRate = 0.9;
    private static int vehicleIdLen = 7;

    static void init(Properties prop) {
        totalEzpay = Integer.parseInt(prop.getProperty("total.ezpay"));
        logger.debug("totalEzpay: " + totalEzpay);

        ezpayRate = Double.parseDouble(prop.getProperty("rate.ezpay"));
        logger.debug("ezpayRate: " + ezpayRate);

        vehicleIdLen = Integer.parseInt(prop.getProperty("length.vehicleId"));
        logger.debug("vehicleIdLen: " + vehicleIdLen);
    }

    public static int generateEzpayId() {
        int ezpayId = -1;                       // default: without ezpay
        if (rand.nextDouble() < ezpayRate) {    // alternative: with ezpay
            ezpayId = rand.nextInt(totalEzpay);
        }
        return ezpayId;
    }

    // vehicle id
    public static String generateVehicleId() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vehicleIdLen; i++) {
            sb.append(randomAlphaNumeric());
        }
        return sb.toString();
    }

    private static char randomAlphaNumeric() {
        return alphaNumericPool.charAt(rand.nextInt(alphaNumericPool.length()));
    }
}
