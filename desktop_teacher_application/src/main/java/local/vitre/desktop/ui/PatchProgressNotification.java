package local.vitre.desktop.ui;

import org.controlsfx.control.Notifications;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import local.vitre.desktop.util.IconBuilder;

public class PatchProgressNotification {

	public ProgressBar bar;
	public Notifications notif;
	public String title;

	public PatchProgressNotification(String className, String subjectName) {
		BorderPane pane = new BorderPane();
		bar = new ProgressBar();
		pane.setLeft(IconBuilder.get().patching);
		pane.setCenter(bar);

		title = "Patching " + className + " - " + subjectName;
		notif = Notifications.create().title(title).text(null).graphic(pane).position(Pos.BOTTOM_RIGHT);
	}

	public void show() {
		notif.show();
	}
}
