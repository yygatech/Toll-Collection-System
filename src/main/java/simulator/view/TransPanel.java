package simulator.view;

import javax.swing.*;

public class TransPanel extends JPanel {

    String name;
    TransScrollPane transScrollPane;

    TransPanel(String name) {
        super();
        this.name = name;

        // add scroll pane
        transScrollPane = new TransScrollPane("TransScrollPanel");
        add(transScrollPane.name, transScrollPane);
    }
}
