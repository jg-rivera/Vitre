package local.vitre.desktop.ui.fx;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import local.vitre.desktop.Log;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.http.NetworkManager;
import local.vitre.desktop.record.ClassRecord;
import local.vitre.desktop.record.ComponentType;
import local.vitre.desktop.record.ImportManager;
import local.vitre.desktop.record.Student;
import local.vitre.desktop.record.data.WorkbookDocument;
import local.vitre.desktop.record.patch.ContextManager;
import local.vitre.desktop.task.TaskHandler;
import local.vitre.desktop.ui.SignaturePopOver;
import local.vitre.desktop.ui.UIHandler;
import local.vitre.desktop.util.IconBuilder;
import local.vitre.desktop.util.Utils;

public class MainController {

	@FXML public SwingNode writtenWorkNode;
	@FXML public SwingNode performanceTasksNode;
	@FXML public SwingNode quarterlyAssessmentNode;
	@FXML public SwingNode studentsListNode;

	@FXML public Label checkLabelParsed;
	@FXML public Label checkLabelSync;
	@FXML public Label checkLabelVerified;

	@FXML public Label dataSubjectValue;
	@FXML public Label dataTrackValue;
	@FXML public Label dataSectionValue;
	@FXML public Label dataTeacherValue;
	@FXML public Label dataSemesterValue;
	@FXML public Label dataStudentsTotalValue;
	@FXML public Label dataStudentsMaleValue;
	@FXML public Label dataStudentsFemaleValue;

	@FXML public Label onlineValue;
	@FXML public Label latencyValue;
	@FXML public Label ipValue;
	@FXML public Label flagCount;

	@FXML public Label currentSection, currentSubject, currentTeacher;
	@FXML public Label hashRecord;

	@FXML public MenuButton menuBtnQuarter;
	@FXML public Button patchBtn;
	@FXML public Button registerBtn;

	@FXML public Label selectedRowColValue;
	@FXML public Label selectedStudentValue;
	@FXML public Label selectedItemValue;
	@FXML public Label selectedContextValue;
	@FXML public Label selectedStateValue;

	@FXML public ListView<Student> dataStudentsList;


	@FXML public TabPane componentTabPane;
	@FXML public TabPane inputDataTabPane;

	@FXML public TitledPane recordsTitlePane;
	@FXML public TitledPane recordViewTitlePane;

	@FXML public AnchorPane helpPane;
	@FXML public AnchorPane currentRecordPane;
	@FXML public AnchorPane columnInfoPane;

	@FXML public HBox fileChooserPane;

	@FXML public MaterialDesignIconView registeredIcon;

	@FXML public ToolBar toolbar;

	public boolean hidden;
	public boolean hasView;

	// Events.

	@FXML
	void onViewContextList(ActionEvent event) {
		if (Vitre.hasActiveDocument())
			Vitre.getActiveContextor().showContextList();
	}

	@FXML
	void onViewHistory(ActionEvent event) {
		try {
			UIHandler.showPatchHistoryFX();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void onSaveExit(ActionEvent event) {
		Platform.exit();
		System.exit(0);
	}

	@FXML
	void onNetRefresh(MouseEvent event) {
		NetworkManager.safeCheckOnline();
		NetworkManager.updateUI();

		// PatchProgressNotification notif = new
		// PatchProgressNotification("12-STEM", "General Biology");

		// Platform.runLater(() -> notif.show());
	}

	@FXML
	void onViewFlagList(ActionEvent event) throws IOException {
		UIHandler.showFlagViewerFX();
	}

	@FXML
	void onViewRefresh(ActionEvent event) {
		UIHandler.repaintAll(writtenWorkNode, performanceTasksNode, quarterlyAssessmentNode);
	}

	@FXML
	void menuImportAction(ActionEvent event) {
		List<File> files = UIHandler.recordChooser().showOpenMultipleDialog(fileChooserPane.getScene().getWindow());
		Vitre.importer.importAll(files);
	}

	@FXML
	void onSync(ActionEvent event) {
		if (Vitre.hasActiveDocument())
			TaskHandler.sync();
	}

	@FXML
	void onApplyPatch(ActionEvent event) throws ClientProtocolException, IOException, ParseException {
		if (Vitre.hasActiveDocument()) {
			ClassRecord record = Vitre.getActiveClassRecord();
			ContextManager ctx = record.getContextManager();
			WorkbookDocument doc = Vitre.getActiveDocument();

			if (!ctx.hasAllContext()) {
				Alert alert = UIHandler.alert(AlertType.WARNING, "Missing column descriptions",
						"Please create a patch context for every grade column.");
				alert.showAndWait();
				return;
			}

			if (record.getPatcher().isSynchronized())
				// Handle on TaskHandler
				doc.patch();
		}
	}

	@FXML
	void onRegisterStudents(ActionEvent event) throws ClientProtocolException, IOException, ParseException {
		if (Vitre.hasActiveDocument()) {
			ClassRecord record = Vitre.getActiveClassRecord();
			UIHandler.showRegisterFX(record);
		}
	}
	
	@FXML
	void onQuarterSelect(ActionEvent event) {
		MenuItem m = (MenuItem) event.getSource();
		if (m.getText() != menuBtnQuarter.getText())
			menuBtnQuarter.setText(m.getText());

		if (menuBtnQuarter.getText().equalsIgnoreCase("1st")) {
			UIHandler.resetToggleCheck();
			TaskHandler.loadActiveDocumentToView(1);
		} else if (menuBtnQuarter.getText().equalsIgnoreCase("2nd")) {
			UIHandler.resetToggleCheck();
			TaskHandler.loadActiveDocumentToView(2);
		}
	}

	@FXML
	void onHideView(ActionEvent event) {
		ToggleButton b = (ToggleButton) event.getSource();
		if (!hidden) {
			GaussianBlur blur = new GaussianBlur(15.5);
			writtenWorkNode.setEffect(blur);
			performanceTasksNode.setEffect(blur);
			quarterlyAssessmentNode.setEffect(blur);
			writtenWorkNode.setMouseTransparent(true);
			performanceTasksNode.setMouseTransparent(true);
			quarterlyAssessmentNode.setMouseTransparent(true);
			b.setGraphic(IconBuilder.get().eye);
			b.setText("Show View");
			hidden = true;
		} else {
			writtenWorkNode.setEffect(null);
			performanceTasksNode.setEffect(null);
			quarterlyAssessmentNode.setEffect(null);
			writtenWorkNode.setMouseTransparent(false);
			performanceTasksNode.setMouseTransparent(false);
			quarterlyAssessmentNode.setMouseTransparent(false);
			b.setGraphic(IconBuilder.get().eyeOff);
			b.setText("Hide View");
			hidden = false;
		}
	}

	/*
	 * ------------------------------------------- BUILD
	 * -------------------------------------------
	 */

	public void build(ClassRecord record) {
		buildCurrentData(record);
		buildDocumentData(record);
		try {
			buildTables(record);
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}

		if (!hasView)
			hasView = true;
		Log.info("FX", "Built user interface elements.");
	}

	public void destroy() {
		Log.info("FX", "Destroyed UI nodes.");

		if (hasView) {
			destroyAuthData();
			destroyDocumentData();
			destroySwingNodes();
			registeredIcon.setVisible(false);
			enable(false);
			hasView = false;
		}
	}

	/**
	 * Primary initialization method.
	 */
	public void init() {
		// bodySplitPane.lookupAll(".split-pane-divider").stream().forEach(div
		// -> div.setMouseTransparent(true));
		// rootSplitPane.lookupAll(".split-pane-divider").stream().forEach(div
		// -> div.setMouseTransparent(true));
		// contentSplitPane.lookupAll(".split-pane-divider").stream().forEach(div
		// -> div.setMouseTransparent(true));

		enable(false);
		patchBtn.setDisable(true);

		UIHandler.addTooltip(registeredIcon, "This section is registered in the Vitre.");
		registeredIcon.setVisible(false);

		buildImporter();
		// buildRetraction();
		buildRepaintFocus();

		NetworkManager.updateUI();
	}

	public void enable(boolean bool) {
		bool = !bool;
		componentTabPane.setDisable(bool);
		inputDataTabPane.setDisable(bool);
		helpPane.setDisable(bool);
		currentRecordPane.setDisable(bool);
		columnInfoPane.setDisable(bool);
		toolbar.setDisable(bool);
	}

	public void buildImporter() {
		ImportManager importer = Vitre.importer;

		if (!importer.empty()) {
			int size = importer.getImportSize();
			recordsTitlePane.setText("Class Records (" + size + " loaded)");
		}

		importer.initUI(fileChooserPane);
	}

	public void buildRepaintFocus() {

		Stage stage = Vitre.getMainStage();

		stage.widthProperty().addListener((obs, oldVal, newVal) -> {
			UIHandler.repaintAll(writtenWorkNode, performanceTasksNode, quarterlyAssessmentNode);
		});

		stage.heightProperty().addListener((obs, oldVal, newVal) -> {
			UIHandler.repaintAll(writtenWorkNode, performanceTasksNode, quarterlyAssessmentNode);
		});

		componentTabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			int val = newValue.intValue();
			if (val == ComponentType.WRITTEN_WORK.ordinal()) {
				// UIHandler.repaint(writtenWorkNode);
				UIHandler.focus(writtenWorkNode);
			} else if (val == ComponentType.PERFORMANCE_TASK.ordinal()) {
				// UIHandler.repaint(performanceTasksNode);
				UIHandler.focus(performanceTasksNode);
			} else if (val == ComponentType.QUARTERLY_ASSESSMENT.ordinal()) {
				// UIHandler.repaint(quarterlyAssessmentNode);
				UIHandler.focus(quarterlyAssessmentNode);
			}

			UIHandler.repaintAll(writtenWorkNode, performanceTasksNode, quarterlyAssessmentNode);
		});
	}

	public void buildCurrentData(ClassRecord record) {
		currentSection.setText(record.gradeSection);
		currentSubject.setText(Utils.namify(record.subject));
		currentTeacher.setText(Utils.namify(record.teacher));
	}

	public void buildAuthData(SignaturePopOver pop) {

		currentSection.setOnMouseEntered((e) -> {
			pop.setDetached(true);
			pop.show(currentSection, e.getScreenX(), e.getScreenY() + 10);
		});

		currentSection.setOnMouseExited((e) -> {
			pop.hide();
		});

		hashRecord.setText(pop.descriptor.patchSignature);
		registeredIcon.setVisible(true);
	}

	public void destroyAuthData() {
		// Unregister
		currentSection.setOnMouseEntered(null);
		currentSection.setOnMouseExited(null);
	}

	public void buildDocumentData(ClassRecord record) {
		buildLabel(dataSubjectValue, Utils.namify(record.subject));
		buildLabel(dataTrackValue, Utils.namify(record.track));
		buildLabel(dataTeacherValue, Utils.namify(record.teacher));
		buildLabel(dataSectionValue, record.gradeSection);
		buildLabel(dataSemesterValue, Utils.namify(record.semester));
		buildLabel(dataStudentsTotalValue, "" + record.getStudentsCount());
		buildLabel(dataStudentsMaleValue, "" + record.getMaleStudentsCount());
		buildLabel(dataStudentsFemaleValue, "" + record.getFemaleStudentsCount());
	}

	public void updateGraphicDocumentData(int state) {
		changeGraphic(dataSubjectValue, state);
		changeGraphic(dataTrackValue, state);
		changeGraphic(dataTeacherValue, state);
		changeGraphic(dataSectionValue, state);
		changeGraphic(dataSemesterValue, state);
		changeGraphic(dataStudentsTotalValue, state);
		changeGraphic(dataStudentsMaleValue, state);
		changeGraphic(dataStudentsFemaleValue, state);
	}

	public void buildLabel(Label label, String text) {
		label.setText(text);
		label.setGraphicTextGap(1.5);
		label.setGraphic(IconBuilder.get().sync());
	}

	public void changeGraphic(Label label, int state) {
		switch (state) {
		case 0:
			label.setGraphic(IconBuilder.get().check());
			break;
		}
	}

	public void buildTables(ClassRecord record) throws InvocationTargetException, InterruptedException {
		UIHandler.attachStudentList(record.getStudents(), studentsListNode);
		UIHandler.attachGradeTables(record.writtenWork, writtenWorkNode);
		UIHandler.attachGradeTables(record.performanceTask, performanceTasksNode);
		UIHandler.attachGradeTables(record.quarterlyAssessment, quarterlyAssessmentNode);
		UIHandler.focus(writtenWorkNode);
	}

	public void destroyDocumentData() {
		destroyLabel(currentSection);
		destroyLabel(currentSubject);
		destroyLabel(currentTeacher);
		destroyLabel(dataSubjectValue);
		destroyLabel(dataTrackValue);
		destroyLabel(dataTeacherValue);
		destroyLabel(dataSectionValue);
		destroyLabel(dataSemesterValue);
		destroyLabel(dataStudentsTotalValue);
		destroyLabel(dataStudentsMaleValue);
		destroyLabel(dataStudentsFemaleValue);
		destroyLabel(selectedContextValue);
		destroyLabel(selectedRowColValue);
		destroyLabel(selectedItemValue);
		destroyLabel(selectedStateValue);
		destroyLabel(selectedStudentValue);
		destroyLabel(hashRecord);

		// Patchings
		patchBtn.setDisable(true);
		registerBtn.setDisable(false);
		UIHandler.toggleCheck(checkLabelParsed, false);
		UIHandler.toggleCheck(checkLabelVerified, false);
		UIHandler.toggleCheck(checkLabelSync, false);
	}

	public void destroyLabel(Label label) {
		label.setText("-");
		if (label.getGraphic() != null)
			label.setGraphic(null);
	}

	public void destroySwingNodes() {
		studentsListNode.getContent().removeAll();
		writtenWorkNode.getContent().removeAll();
		performanceTasksNode.getContent().removeAll();
		quarterlyAssessmentNode.getContent().removeAll();
		UIHandler.repaintAll(writtenWorkNode, performanceTasksNode, quarterlyAssessmentNode, studentsListNode);
	}
}
