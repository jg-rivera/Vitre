package local.vitre.desktop.updater;

import java.io.IOException;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Main extends Application {

	public Updater updater;

	static Stage stage;
	static final String alertTitle = "Vitre Updater";
	static final String DROP_JAR_NAME =  "dta.jar";
	static Alert alertDownloader;
	static TextArea workText;
	static ProgressBar progress;
	static double screenWidth;
	static double screenHeight;

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage s) {
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		screenWidth = bounds.getWidth();
		screenHeight = bounds.getHeight();

		// Dummy anchor pane for icon
		AnchorPane pane = new AnchorPane();
		Scene scene = new Scene(pane);

		s.setX(screenWidth / 2);
		s.setY(screenHeight / 3);
		s.setScene(scene);
		s.getIcons().add(new Image(Main.class.getResource("icon.png").toExternalForm()));

		createDownloaderDialog(s);

		// Check alert definition
		// Checking update for appropriate alert prompt
		Alert checking = createDialog(s, "Finding new updates in the cloud...", null, false);
		checking.setGraphic(attachIcon("checking.gif", 64));
		checking.show();

		updater = new Updater(progress, workText);

		Task<Void> checkTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				updater.check();
				return null;
			}

			@Override
			protected void succeeded() {
				checking.close();
				checkUpdate(s);
			}

			@Override
			protected void failed() {
				checking.close();
				Alert error = new Alert(AlertType.ERROR);
				error.initOwner(s);
				error.setTitle(alertTitle);
				error.setHeaderText("Cannot find updates.");
				error.setContentText("Please check your connection to server. Thanks!");
				error.showAndWait();
			}
		};

		Thread checkThread = new Thread(checkTask);
		checkThread.setName("Check Thread");
		checkThread.setDaemon(true);
		checkThread.start();
	}

	public void checkUpdate(Window s) {
		// Update found
		if (updater.hasUpdate) {
			Alert updateFound = createDialog(s, "Update found in the cloud!",
					"Ready to update to version: " + updater.updateVersion + " (b" + updater.updateBuildNumber + ")",
					true);
			updateFound.showAndWait();

			// Defining task
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					updater.initialize();
					return null;
				}

				@Override
				protected void succeeded() {
					alertDownloader.close();
					if (updater.successUpdate) {
						Alert alert = createDialog(s, "Updated successfully!",
								"Current Version: " + updater.updateVersion + " (b" + updater.updateBuildNumber
										+ "), Old Version: " + updater.currentVersion + " (b"
										+ updater.currentBuildNumber + ")",
								true);
						alert.showAndWait();
						runVitre();
						System.exit(0);
					}
				}
			};

			Thread thread = new Thread(task);
			thread.setName("Update Thread");
			thread.setDaemon(true);

			thread.start();
			alertDownloader.show();

		} else {
			// No updates found
			Alert noUpdates = createDialog(s, "Running latest version!",
					"Current version:  " + updater.currentVersion + " (b" + updater.currentBuildNumber + ")", false);
			noUpdates.show();
			runVitre();
			System.exit(0);
		}
	}

	public static Node attachIcon(String file, double size) {
		Image image = new Image(Main.class.getResource(file).toExternalForm());
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(size);
		imageView.setFitHeight(size);
		return imageView;
	}

	public void runVitre() {
		ProcessBuilder pb = new ProcessBuilder("java", "-jar", DROP_JAR_NAME);
		pb.directory(updater.getHome());
		try {
			pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Alert createDialog(Window s, String headerText, String contentText, boolean visibleOk) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initModality(Modality.NONE);
		alert.initOwner(s);
		alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(visibleOk);
		alert.setTitle(alertTitle);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		return alert;
	}

	public static void createDownloaderDialog(Window s) {
		alertDownloader = new Alert(AlertType.INFORMATION);
		alertDownloader.initOwner(s);
		alertDownloader.setTitle(alertTitle);
		alertDownloader.setHeaderText("Downloading updates...");
		alertDownloader.setGraphic(attachIcon("updating.gif", 64));

		workText = new TextArea();
		workText.setEditable(false);
		workText.setWrapText(true);

		progress = new ProgressBar();

		progress.setMaxWidth(Double.MAX_VALUE);
		progress.setMaxHeight(Double.MAX_VALUE);

		Label label = new Label("Update progress: ");
		label.setMaxWidth(Double.MAX_VALUE);
		workText.setMaxWidth(Double.MAX_VALUE);
		workText.setMaxHeight(Double.MAX_VALUE);

		VBox content = new VBox();
		content.setMaxWidth(Double.MAX_VALUE);
		// content.getChildren().add(label);
		content.getChildren().add(progress);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(workText, 0, 0);

		GridPane.setVgrow(workText, Priority.ALWAYS);
		GridPane.setHgrow(workText, Priority.ALWAYS);

		alertDownloader.getDialogPane().setContent(content);
		alertDownloader.getDialogPane().setExpandableContent(expContent);
		alertDownloader.getButtonTypes().setAll(ButtonType.CANCEL);
	}

}
