package local.vitre.desktop.ui.fx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.controlsfx.control.Notifications;
import org.json.simple.parser.ParseException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.record.ClassRecord;
import local.vitre.desktop.record.patch.PatchCrown;
import local.vitre.desktop.record.patch.Patcher;
import local.vitre.desktop.ui.UIHandler;
import local.vitre.desktop.util.IconBuilder;
import local.vitre.desktop.util.Utils;

public class RegistryController {

	@FXML public Label className, popClassRecord, popServerRegistry, accountSheetPath, helpText;

	@FXML public ListView<String> listClassRecord, listServerRegistry;

	@FXML public Button registryBtn, changeDirBtn;

	private String popPrefix = "Population: ";
	private String popSuffix = " students";
	private String outputPath;
	private String outputFolder;
	private String outputFileName;

	private Stage stage;

	public void create(Stage stage, ClassRecord record) {
		// Set label values
		className.setText(record.gradeSection);
		popClassRecord.setText(popPrefix + record.getStudents().size() + popSuffix);

		if (!record.isSynchronized()) {
			registryBtn.setDisable(true);
			changeDirBtn.setDisable(true);
			accountSheetPath.setDisable(true);
			helpText.setTextFill(Color.DARKRED);
			helpText.setText("Please synchronize this class record first.");
		} else {
			helpText.setTextFill(Color.DARKGREEN);
			helpText.setText("Upon registration, you will be assigned as its class adviser.");
		}

		// Acquire registry names from crown
		PatchCrown crown = record.getPatcher().getCrown();
		ArrayList<String> studentNames = record.getStudentNames();

		if (crown != null) {
			ArrayList<String> registryNames = crown.getRegistryStudentNames();
			popServerRegistry.setText(popPrefix + registryNames.size() + popSuffix);
			listServerRegistry.getItems().addAll(registryNames);

			// Disable btn if both are synced.
			if (registryNames.size() > 0) {
				registryBtn.setDisable(true);
				changeDirBtn.setDisable(true);
				accountSheetPath.setDisable(true);
				helpText.setTextFill(Color.DARKGREEN);
				helpText.setText("Already registered in server.");
			}
		}

		outputFolder = Vitre.assets.getEntryValue("ACCOUNTS_STORE_DIR");
		outputFileName = Utils.namify(record.gradeSection) + " Vitre Accounts.docx";

		outputPath = outputFolder + outputFileName;
		accountSheetPath.setText(outputPath);

		listClassRecord.getItems().addAll(studentNames);
		listClassRecord.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		listClassRecord.setPlaceholder(new Label("No students found."));

		listServerRegistry.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		listServerRegistry.setPlaceholder(new Label("No students found."));
	}

	@FXML
	public void onChangeDirectory(ActionEvent event) {
		DirectoryChooser chooser = UIHandler.sheetPathChooser(outputFolder);
		File selectedFolder = chooser.showDialog(stage);

		if (selectedFolder != null) {
			outputPath = selectedFolder.getAbsolutePath() + "\\";
			outputPath += outputFileName;
			accountSheetPath.setText(outputPath);
		}
	}

	@FXML
	public void onRegistry(ActionEvent event) throws ClientProtocolException, IOException, ParseException {
		if (Vitre.hasActiveDocument()) {
			ClassRecord record = Vitre.getActiveClassRecord();
			Patcher patcher = record.getPatcher();

			if (patcher.isSynchronized()) {
				patcher.registerStudents();
				patcher.createAccountSheet(outputPath);

				Stage stage = (Stage) className.getScene().getWindow();
				stage.close();
			}
		}
	}

	@FXML
	public void onCancel(ActionEvent event) {
		Stage stage = (Stage) className.getScene().getWindow();
		stage.close();
	}
}
