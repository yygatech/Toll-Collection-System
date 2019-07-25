package server.db;

import simulator.RandomGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Repository {

    private static File db = new File("src/main/resources/db.txt");

    static BufferedWriter out = null;

    // TODO: real implementation
    public static String queryVehicleId(int ezpayId) {
        return RandomGenerator.generateVehicleId();
    }

    // TODO: real implementation
    public static void updateDB(String ts, boolean au, Integer ez, String vc, int ga, int gt, int ln, float tl) {
        try {
            out = new BufferedWriter(new FileWriter(db, true));
            String line = ts + " " + au + " " + ez + " " + vc + " " + ga + " " + gt + " " + ln + " " + String.format("%.2f", tl);
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

}
