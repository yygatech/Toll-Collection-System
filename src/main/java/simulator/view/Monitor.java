package simulator.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulator.model.StatsTableModel;
import simulator.model.TransTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Monitor extends JFrame {

    private static final Logger logger = LogManager.getLogger("Monitor");

    Map<String, Component> components = new HashMap<>();
    TransPanel transPanel;
    StatsPanel statsPanel;

    public Monitor() {
        super("Monitor");

        // pre-setting
        Container content = getContentPane();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // add JPanel
        transPanel = new TransPanel("TransPanel");
        content.add(transPanel.name, transPanel);
        components.put("transPanel.name", transPanel);

        statsPanel = new StatsPanel("StatsPanel");
        content.add(statsPanel.name, statsPanel);
        components.put(statsPanel.name, statsPanel);

        // post-setting
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public TransTableModel getTransTableModel() {
        logger.debug(">>>> getting transTableModel <<<<");
        TransTableModel model = (TransTableModel) transPanel.transScrollPane.transTable.getModel();
        return model;
    }

    public StatsTableModel getStatsTableModel() {
        logger.debug(">>>> getting statsTableModel <<<<");
        StatsTableModel model = (StatsTableModel) statsPanel.statsScrollPane.statsTable.getModel();
        return model;
    }
}
