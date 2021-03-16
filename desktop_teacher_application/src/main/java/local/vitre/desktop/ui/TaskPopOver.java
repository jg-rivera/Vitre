package local.vitre.desktop.ui;

import org.controlsfx.control.PopOver;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

public class TaskPopOver extends PopOver {

	private ProgressBar bar;
	private Label workText;
	private VBox vbox;

	public TaskPopOver(String task) {
		bar = new ProgressBar();

		vbox = new VBox(5, bar);
		vbox.setPrefWidth(160);
		vbox.setAlignment(Pos.CENTER);

		setTitle(task);

		setContentNode(vbox);
		setAnimated(false);
		setCloseButtonEnabled(false);
		setDetachable(true);
		setDetached(true);
		setArrowLocation(ArrowLocation.TOP_CENTER);
	}

	public ProgressBar getBar() {
		return bar;
	}

	public DoubleProperty progressProperty() {
		return bar.progressProperty();
	}

	public VBox getVbox() {
		return vbox;
	}

	public Label getWorkText() {
		return workText;
	}
}
