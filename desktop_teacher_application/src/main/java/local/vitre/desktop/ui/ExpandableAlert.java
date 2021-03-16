package local.vitre.desktop.ui;

import org.apache.commons.lang3.exception.ExceptionUtils;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import local.vitre.desktop.Vitre;

public class ExpandableAlert {

	TextArea expandableText;
	Alert alert;

	public ExpandableAlert(AlertType type, String header, String contentText) {
		alert = new Alert(type);
		alert.initOwner(Vitre.getMainStage());
		alert.setTitle("Alert!");
		alert.setHeaderText(header);

		expandableText = new TextArea();
		expandableText.setEditable(false);
		expandableText.setWrapText(true);

		expandableText.setMaxWidth(Double.MAX_VALUE);
		expandableText.setMaxHeight(Double.MAX_VALUE);

		Label contentLabel = new Label(contentText);

		VBox content = new VBox();
		content.setMaxWidth(Double.MAX_VALUE);
		content.getChildren().add(contentLabel);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(expandableText, 0, 0);

		GridPane.setVgrow(expandableText, Priority.ALWAYS);
		GridPane.setHgrow(expandableText, Priority.ALWAYS);

		alert.getDialogPane().setContent(content);
		alert.getDialogPane().setExpandableContent(expContent);
		alert.getButtonTypes().setAll(ButtonType.OK);
	}

	public void show() {
		alert.show();
	}

	public void showAndWait() {
		alert.showAndWait();
	}

	public void attach(String value) {
		expandableText.setText(value);
	}

	public void attach(ReadOnlyObjectProperty<Throwable> exception) {
		String stackTrace = ExceptionUtils.getStackTrace(exception.get());
		expandableText.setText(stackTrace);
	}
	
	public void attach(Exception e) {
		String stackTrace = ExceptionUtils.getStackTrace(e);
		expandableText.setText(stackTrace);
	}
}
