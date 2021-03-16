package local.vitre.desktop.ui.swing;

import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import local.vitre.desktop.Assets;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.record.patch.ContextManager;
import local.vitre.desktop.record.patch.PatchContext;
import local.vitre.desktop.ui.UIHandler;

public class ContextPatchPopupMenu extends JPopupMenu {

	private double cap;
	private int column;
	private int itemIndex;
	private int componentIndex;
	private int occupiedCount;
	private int quarter;
	private String columnName;

	private boolean hasContext;

	public ContextPatchPopupMenu(String columnName) {
		this.columnName = columnName;
	}

	private void create() {
		ContextManager contextMan = Vitre.getActiveContextor();
		hasContext = contextMan.hasContext(itemIndex, componentIndex);

		JMenuItem title = new JMenuItem(columnName);
		ImageIcon icon = new ImageIcon(Assets.readFile("context.png").getPath());
		title.setIcon(icon);

		JMenuItem newContext = new JMenuItem();
		if (!hasContext)
			newContext.setText("New context...");
		else
			newContext.setText("Update existing context...");

		newContext.addActionListener((e) -> {
			Platform.runLater(() -> {
				try {
					if (!hasContext)
						newContext();
					else
						viewContext();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
		});

		JMenuItem removeContext = new JMenuItem("Remove existing patch context...");
		removeContext.addActionListener((e) -> {
			Platform.runLater(() -> {
				Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to remove this context?", ButtonType.YES, ButtonType.NO);
				alert.showAndWait();
				
				if (alert.getResult() == ButtonType.YES) contextMan.removeContext(itemIndex, componentIndex);
			});
		});

		add(title);
		addSeparator();
		add(newContext);
		if (hasContext)
			add(removeContext);
	}

	private void newContext() throws IOException {
		UIHandler.showNewPatchContextCreateFX(quarter, column, itemIndex, componentIndex, cap, occupiedCount,
				columnName);
	}

	private void viewContext() throws IOException {
		PatchContext context = Vitre.getActiveContextor().getContext(itemIndex, componentIndex);
		UIHandler.showFilledPatchContextCreateFX(occupiedCount, context);
	}

	public void setQuarter(int quarter) {
		this.quarter = quarter;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public void setCap(double cap) {
		this.cap = cap;
	}

	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}

	public void setComponentIndex(int componentIndex) {
		this.componentIndex = componentIndex;
	}

	public void setOccupiedCount(int occupiedCount) {
		this.occupiedCount = occupiedCount;
	}

	public void show(JTable table, MouseEvent e) {
		create();
		show(table, e.getX(), e.getY());
	}
}
