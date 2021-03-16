package local.vitre.desktop.ui.swing;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import local.vitre.desktop.Vitre;
import local.vitre.desktop.record.patch.PatchContext;
import local.vitre.desktop.ui.CellMetadata;

public class GradeTableHeaderRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
				column);
		GradeTableModel model = (GradeTableModel) table.getModel();

		c.setToolTipText(null);

		// Tool tip assignment for table header
		if (GradeTableModel.isItemColumn(column)) {
			if (model.isItemColumnOccupied(column)) {
				int item = GradeTableModel.getItemIndex(column);
				PatchContext context = Vitre.getActiveContextor().getContext(item, model.getComponentIndex());

				if (context != null) { // Has context
					String seed = context.getSeed();
					String desc = context.getDescription();
					c.setToolTipText("<html> <font color=\"#0073e6\"><b>" + model.getFullColumnName(column)
							+ "</b></font><br>Seed: " + seed + "<br> Description: " + desc + "</html>");
				} else {
					c.setToolTipText("<html> <font color=\"#808080\"><b>" + model.getFullColumnName(column)
							+ "</b></font><br> Seed: n/a <br> Description </html>");
				}
			}
		}

		c.setBorder(BorderFactory.createEtchedBorder());

		CellMetadata metadata = model.getHeaderMetadataAt(column);

		c.setForeground(metadata.getForegroundColor());
		c.setBackground(metadata.getBackgroundColor());

		return c;
	}
}
