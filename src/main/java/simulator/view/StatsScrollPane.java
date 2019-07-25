package simulator.view;

import simulator.model.StatsTableModel;
import simulator.model.TransTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class StatsScrollPane extends JScrollPane {

    private static final int[] MIN_COL_WIDTHS = new int[] {100,100};
    private static final int[] PREFERRED_COL_WIDTHS = new int[] {400, 400};

    String name;
    JTable statsTable;

    StatsScrollPane(String name) {
        super();
        this.name = name;

        // add JTable
        statsTable = new JTable(new StatsTableModel());
        setPreferredSize(new Dimension(800, 50));
        setViewportView(statsTable);

        // format JTable
        statsTable.setFocusable(false);
        statsTable.setRowSelectionAllowed(false);
        ((DefaultTableCellRenderer) statsTable.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerCellRenderer = new DefaultTableCellRenderer();
        centerCellRenderer.setHorizontalAlignment(JLabel.CENTER);
        TableColumnModel colModel = statsTable.getColumnModel();
        for (int i = 0; i < colModel.getColumnCount(); i++) {
            TableColumn col = colModel.getColumn(i);
            col.setMinWidth(MIN_COL_WIDTHS[i]);
            col.setPreferredWidth(PREFERRED_COL_WIDTHS[i]);
            col.setCellRenderer(centerCellRenderer);
        }
    }
}
