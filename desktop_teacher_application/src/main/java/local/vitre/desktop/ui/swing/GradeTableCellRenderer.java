package local.vitre.desktop.ui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import local.vitre.desktop.record.GradingComponent;
import local.vitre.desktop.ui.CellMetadata;

public class GradeTableCellRenderer extends DefaultTableCellRenderer {

	public GradeTableCellRenderer() {

	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		JComponent jc = (JComponent) c;
		GradeTableModel model = (GradeTableModel) table.getModel();
		GradingComponent gc = model.getComponent();

		if (row == 0 && column == 1) {
			setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 14));
		}

		if (row == 0) {
			jc.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
		}

		if (row > 0) {
			if (column <= 1)
				jc.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
			if (column == gc.getScoreColumnStart() + 2)
				jc.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK));
		}

		if (column == 10) {
			c.setFocusable(false);
		}
		if (row != 0 && column == 1) {
			setHorizontalAlignment(JLabel.LEFT);
		} else
			setHorizontalAlignment(JLabel.CENTER);

		CellMetadata metadata = model.getMetadataAt(row, column);
		setOpaque(true);

		c.setForeground(metadata.getForegroundColor());

		c.setBackground(metadata.getBackgroundColor());

		return c;
	}
}
