package local.vitre.desktop.ui.swing;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import local.vitre.desktop.Vitre;
import local.vitre.desktop.ui.CellMetadata;

public class StudentListCellRenderer extends DefaultListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {

		Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (Vitre.hasActiveDocument() && Vitre.hasView()) {
			if (Vitre.getActiveClassRecord().getPatcher().isSynchronized()) {
				c.setBackground(CellMetadata.CACHED_COLOR);
				return c;
			}
			c.setBackground(CellMetadata.CONTEXT_COLOR);
			return c;
		}

		c.setBackground(CellMetadata.UNKNOWN_COLOR);
		return c;
	}
}
