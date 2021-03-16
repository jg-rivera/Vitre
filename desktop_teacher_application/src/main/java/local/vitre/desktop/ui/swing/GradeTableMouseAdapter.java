package local.vitre.desktop.ui.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import local.vitre.desktop.ui.UIHandler;

public class GradeTableMouseAdapter extends MouseAdapter {

	protected JTable table;

	public GradeTableMouseAdapter(JTable table) {
		this.table = table;
	}

	public void mouseClicked(MouseEvent e) {
		if (!table.contains(e.getPoint()))
			return;

		int row = table.rowAtPoint(e.getPoint());
		int col = table.columnAtPoint(e.getPoint());
		GradeTableModel model = (GradeTableModel) table.getModel();

		if (row >= 1 && col >= 2 && col <= model.getMaxItemCount() + 1) {
			if (model.isItemColumnOccupied(col))
				if (SwingUtilities.isRightMouseButton(e)) {
					ContextPatchPopupMenu menu = new ContextPatchPopupMenu(model.getFullColumnName(col));
					menu.setCap(model.getCap(col));
					menu.setColumn(col);
					menu.setItemIndex(GradeTableModel.getItemIndex(col));
					menu.setComponentIndex(model.getComponentIndex());
					menu.setOccupiedCount(model.getOccupiedRowCount(col));
					menu.show(table, e);
				}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!table.contains(e.getPoint()))
			return;
		int row = table.rowAtPoint(e.getPoint());
		int col = table.columnAtPoint(e.getPoint());
		GradeTableModel model = (GradeTableModel) table.getModel();
		UIHandler.handleSelection(row, col, model);
	}
}
