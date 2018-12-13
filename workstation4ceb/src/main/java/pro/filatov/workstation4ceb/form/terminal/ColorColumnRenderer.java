package pro.filatov.workstation4ceb.form.terminal;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by yuri.filatov on 16.09.2016.
 */
public class ColorColumnRenderer extends DefaultTableCellRenderer
{
    Color bkgndColor;

    public ColorColumnRenderer(Color bkgnd) {
        super();
        bkgndColor = bkgnd;
    }

    public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component cell = super.getTableCellRendererComponent
                (table, value, isSelected, hasFocus, row, column);

        cell.setBackground( bkgndColor );
        cell.setFont(cell.getFont().deriveFont(Font.BOLD));
        return cell;
    }
}