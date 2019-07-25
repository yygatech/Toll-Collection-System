package simulator.view;

import javax.swing.*;

public class StatsPanel extends JPanel {

    String name;
    StatsScrollPane statsScrollPane;

    StatsPanel(String name) {
        super();
        this.name = name;

        // add scroll pane
        statsScrollPane = new StatsScrollPane("StatsScrollPane");
        add(statsScrollPane.name, statsScrollPane);
    }
}
