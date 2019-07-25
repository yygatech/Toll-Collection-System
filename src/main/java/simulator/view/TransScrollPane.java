package simulator.view;

import simulator.model.TransTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class TransScrollPane extends JScrollPane {

    private static final int[] MIN_COL_WIDTHS = new int[] {50,150,100,75,100,50,50,50,50};
    private static final int[] PREFERRED_COL_WIDTHS = new int[] {50,150,100,100,100,75,75,75,75};

    String name;
    JTable transTable;

    TransScrollPane(String name) {
        super(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.name = name;
        setPreferredSize(new Dimension(800, 400));

        // add JTable
        transTable = new JTable(new TransTableModel());
        setViewportView(transTable);

        // format JTable
        transTable.setFocusable(false);
        transTable.setRowSelectionAllowed(false);
        ((DefaultTableCellRenderer) transTable.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerCellRenderer = new DefaultTableCellRenderer();
        centerCellRenderer.setHorizontalAlignment(JLabel.CENTER);
        TableColumnModel colModel = transTable.getColumnModel();
        for (int i = 0; i < colModel.getColumnCount(); i++) {
            TableColumn col = colModel.getColumn(i);
            col.setMinWidth(MIN_COL_WIDTHS[i]);
            col.setPreferredWidth(PREFERRED_COL_WIDTHS[i]);
            col.setCellRenderer(centerCellRenderer);
        }
    }
}
