package local.vitre.desktop.task;

import java.io.File;
import java.io.IOException;

import org.controlsfx.control.Notifications;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;
import local.vitre.desktop.FlagHandler.Flag;
import local.vitre.desktop.Log;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.http.NetworkManager;
import local.vitre.desktop.record.ClassRecord;
import local.vitre.desktop.record.DocumentImport;
import local.vitre.desktop.record.ImportManager;
import local.vitre.desktop.record.data.RecordConfiguration;
import local.vitre.desktop.record.data.WorkbookDocument;
import local.vitre.desktop.record.patch.Patcher;
import local.vitre.desktop.ui.ExpandableAlert;
import local.vitre.desktop.ui.TaskPopOver;
import local.vitre.desktop.ui.UIHandler;
import local.vitre.desktop.ui.fx.MainController;
import local.vitre.desktop.util.IconBuilder;
import local.vitre.desktop.util.Utils;

/**
 * Invokes commonly used tasks by the Vitre system.
 * 
 * @author Gab
 *
 */
public class TaskHandler {

	/**
	 * Invokes a task that imports an Excel document to the
	 * {@link ImportManager}. It must be a valid class record for it is compared
	 * to loaded class record models given by the {@link RecordConfiguration}.
	 * 
	 * @see local.vitre.desktop.record.data.RecordConfiguration
	 * @param file
	 *            - valid Excel document
	 * @throws IllegalArgumentException
	 *             Unidentifiable record.
	 */
	public static void importRecord(File file) {
		ImportManager importer = Vitre.importer;

		TaskPopOver pop = new TaskPopOver("Importing " + file.getName());
		UIHandler.showTaskPopOverOnIV(pop);

		FileOperationTask<Void> task = new FileOperationTask<Void>(file) {
			DocumentImport imp;

			protected Void call() throws Exception {
				String filePath = file.getAbsolutePath();
				String fileName = file.getName();
				imp = new DocumentImport(fileName, filePath);
				boolean created = imp.create();

				if (created) {
					importer.addImport(imp);
					return null;
				}
				throw new IllegalArgumentException("Unidentifiable record");
			}

			@Override
			protected void succeeded() {
				pop.hide();
				importer.redraw(Vitre.controller.fileChooserPane);
			}

			@Override
			protected void failed() {
				pop.hide();
				Alert alert = UIHandler.alert(AlertType.ERROR, "Invalid workbook document.",
						"Unable to identify and import: " + file.getName());
				alert.showAndWait();
			}
		};

		task.exceptionProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				Exception ex = (Exception) newValue;
				ex.printStackTrace();
			}
		});

		Thread thread = new Thread(task);
		thread.setName("DocImport Thread");
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Loads the currently active class record to the Record View Pane.
	 * 
	 * @param quarter
	 *            - the quarter in which the data is loaded from
	 */
	public static void loadActiveDocumentToView(int quarter) {
		loadToView(Vitre.getActiveDocument(), quarter);
	}

	/**
	 * Loads a {@link WorkbookDocument} to the Record View Pane.
	 * 
	 * @param quarter
	 *            - the quarter in which the data is loaded from
	 */
	public static void loadToView(WorkbookDocument workDoc, int quarter) {
		MainController controller = Vitre.controller;
		TaskPopOver pop = new TaskPopOver("Activating...");
		UIHandler.showTaskPopOverOnCRV(pop);

		WorkbookOperationTask<Void> task = new WorkbookOperationTask<Void>(workDoc) {
			@Override
			protected Void call() throws Exception {

				updateProgress(0, 100);
				if (document.hasRepresentation())
					document.destroyRepresentation();
				updateProgress(10, 100);

				Vitre.getFlagger().clear();
				updateProgress(20, 100);

				document.createRepresentation(quarter);
				updateProgress(80, 100);

				document.getClassRecord().parse();
				updateProgress(90, 100);
				return null;
			}

			@Override
			protected void succeeded() {
				controller.componentTabPane.setDisable(false);
				controller.build(document.getClassRecord());
				UIHandler.toggleCheck(Vitre.controller.checkLabelParsed, true);

				document.getClassRecord().getContextManager().renderFullCells();
				updateProgress(100, 100);
				pop.hide();
			}

		};

		pop.progressProperty().bind(task.progressProperty());

		task.exceptionProperty().addListener((observable, oldValue, newValue) -> {
			// Listen on error messages.
			if (newValue != null) {
				Exception ex = (Exception) newValue;
				ex.printStackTrace();
			}
		});

		task.setQuarter(quarter);

		Thread thread = new Thread(task);
		thread.setName("DocLoadToView Thread");
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Synchronizes the current record with the Vitre server.
	 * 
	 * @throws IOException
	 *             Cannot connect to server.
	 */
	public static void sync() {
		TaskPopOver pop = new TaskPopOver("Synchronizing...");
		UIHandler.showTaskPopOverOnCRV(pop);

		Task<Void> task = new Task<Void>() {

			boolean connected, synced;

			@Override
			protected Void call() throws Exception {
				ClassRecord record = Vitre.getActiveClassRecord();
				Patcher patcher = record.getPatcher();

				NetworkManager.checkOnline();
				updateProgress(30, 100);

				if (NetworkManager.isOnline()) {

					NetworkManager.retrieveIP();
					connected = true;

					updateProgress(60, 100);
					patcher.synchronize();

					synced = true;
					updateProgress(80, 100);
				}

				return null;
			}

			@Override
			protected void succeeded() {
				updateProgress(100, 100);
				pop.hide();
				NetworkManager.updateUI();

				Vitre.controller.updateGraphicDocumentData(0);
				Vitre.getActiveContextor().renderFullCells();

				Vitre.getFlagger().remove(Flag.CANNOT_CONNECT);
				Vitre.getFlagger().remove(Flag.CANNOT_SYNC);
			}

			@Override
			protected void failed() {
				updateProgress(100, 100);
				pop.hide();

				NetworkManager.updateUI();

				String errorMessage = "";

				if (!connected) {
					errorMessage += "Error in connecting to the Vitre network. \n";
					Vitre.getFlagger().add(Flag.CANNOT_CONNECT);
				}

				if (!synced) {
					errorMessage += "Error in retrieving network IDs from the Vitre network.";
					Vitre.getFlagger().add(Flag.CANNOT_SYNC);
				}

				ExpandableAlert alert = new ExpandableAlert(AlertType.ERROR, "Cannot synchronize with server.",
						errorMessage);
				alert.attach(exceptionProperty());
				alert.showAndWait();
			}
		};

		pop.progressProperty().bind(task.progressProperty());

		task.exceptionProperty().addListener((observable, oldValue, newValue) -> {
			// Listen on error messages.
			if (newValue != null) {
				Exception ex = (Exception) newValue;
				ex.printStackTrace();
			}
		});

		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Patching thread.
	 * 
	 * @param record
	 */
	public static void patch(ClassRecord record) {
		TaskPopOver pop = new TaskPopOver("Queuing patch...");
		UIHandler.showTaskPopOverOnCRV(pop);

		Notifications progressNotif = Notifications.create().title("Patching in progress!")
				.text("Uploading data to the Vitre network...").position(Pos.TOP_CENTER)
				.graphic(IconBuilder.get().patching).hideAfter(Duration.seconds(1.5));
		progressNotif.show();

		RecordOperationTask<Void> task = new RecordOperationTask<Void>(record) {
			@Override
			protected Void call() throws Exception {
				updateProgress(0, 100);
				Platform.runLater(() -> {
					pop.hide();
				});
				record.getPatcher().registerGrades();
				updateProgress(100, 100);

				return null;
			}

			@Override
			protected void succeeded() {
				Log.info("Patched successfully.");
				Notifications notif = Notifications.create().title("Patch Success!")
						.text("Successfully uploaded patch to Vitre. \n" + "Class Record: " + record.getName()
								+ "\nSection: " + Utils.namify(record.gradeSection) + "\nSubject: "
								+ Utils.namify(record.subject) + "\nTeacher: " + Utils.namify(record.teacher))
						.position(Pos.TOP_CENTER).hideAfter(Duration.seconds(7)).graphic(IconBuilder.get().vitre);
				notif.show();
				sync(); //TODO Investigate if it is optimal
			}
		};

		task.exceptionProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				Exception ex = (Exception) newValue;
				ex.printStackTrace();

				Platform.runLater(() -> {
					Notifications notif = Notifications.create().title("Error occured in patch").text(ex.toString())
							.graphic(IconBuilder.get().vitre);
					notif.showError();
				});
			}
		});

		pop.progressProperty().bind(task.progressProperty());

		Thread thread = new Thread(task);
		thread.setName("Patch Thread");
		thread.setDaemon(true);
		thread.start();
	}
}
