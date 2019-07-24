package server.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.model.TransTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Monitor extends JFrame {

    private static final Logger logger = LogManager.getLogger("Monitor");

    Map<String, Component> components = new HashMap<>();

    class TransPanel extends JPanel {
        int nr = 10, nc=8;
        String[] columnNames = {"timestamp", "authorization", "ezpayId", "vehicleId", "gateId", "gateType", "laneId", "toll"};
        Object[][] data = new Object[nr][nc];

        TransTableModel transTableModel = new TransTableModel(data, columnNames);
        JTable transTable = new JTable(transTableModel);

        TransPanel() {
            super();
            add(transTable);
        }

        public TransTableModel getTransTableModel() {
            return transTableModel;
        }
    }

    public Monitor() {
        super("Monitor");

        Container content = getContentPane();
        content.setLayout(new FlowLayout());

        TransPanel transPanel = new TransPanel();
        content.add("TransPanel", transPanel);
        components.put("TransPanel", transPanel);

//        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public TransTableModel getTransTableModel() {
        return ((TransPanel) components.get("TransPanel")).getTransTableModel();
    }
}
