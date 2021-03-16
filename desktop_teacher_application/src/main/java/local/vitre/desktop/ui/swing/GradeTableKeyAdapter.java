package local.vitre.desktop.ui.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTable;

import local.vitre.desktop.ui.UIHandler;

public class GradeTableKeyAdapter extends KeyAdapter {

	protected JTable table;

	public GradeTableKeyAdapter(JTable table) {
		this.table = table;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			handleTraversal();
			break;
		case KeyEvent.VK_DOWN:
			handleTraversal();
			break;
		case KeyEvent.VK_LEFT:
			handleTraversal();
			break;
		case KeyEvent.VK_RIGHT:
			handleTraversal();
			break;
		}
	}

	private void handleTraversal() {
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();
		GradeTableModel model = (GradeTableModel) table.getModel();
		UIHandler.handleSelection(row, col, model);
	}
}
