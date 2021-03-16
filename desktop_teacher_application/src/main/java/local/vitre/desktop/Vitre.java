package local.vitre.desktop;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import local.vitre.desktop.http.NetworkManager;
import local.vitre.desktop.record.ClassRecord;
import local.vitre.desktop.record.ImportManager;
import local.vitre.desktop.record.data.SchemaReader;
import local.vitre.desktop.record.data.WorkbookDocument;
import local.vitre.desktop.record.patch.ContextManager;
import local.vitre.desktop.record.patch.queue.PatchQueueManager;
import local.vitre.desktop.ui.fx.MainController;
import local.vitre.desktop.ui.fx.SplashController;

public class Vitre extends Application {

	public static SimpleDateFormat dateFormat;

	public static MainController controller;
	public static SplashController splashController;

	public static ImportManager importer;
	public static FlagHandler flagger;
	public static VersionHandler versionHandler;
	public static SchemaReader schemaReader;
	public static PatchQueueManager queueManager;

	public static Assets assets;

	private static Stage mainStage;

	public Vitre() {
		dateFormat = new SimpleDateFormat("EEEE, MM/dd/yyyy; hh:mm a");
		initLogger(Level.ALL);
	}

	private void initLogger(Level level) {
		Handler[] handlers = Log.getLogger().getHandlers();
		for (Handler handler : handlers) {
			Log.getLogger().removeHandler(handler);
		}

		Handler h = new ConsoleHandler();
		h.setLevel(level);
		Log.getLogger().addHandler(h);
		Log.getLogger().setLevel(level);
		Log.getLogger().setUseParentHandlers(false);
	}

	@Override
	public void start(Stage stage) throws Exception {

		mainStage = stage;
		mainStage.getIcons().add(new Image(Assets.getURLPath("/icon.png").toString()));

		loadSplashInterface(stage);

		Log.info("ASSET", "Reading and importing necessary files...");
		double max = 100;
		Task<Void> init = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				updateProgress(0, max);
				updateMessage("Importing assets...");
				updateProgress(30, max);

				assets = new Assets("vitre.init");
				assets.read();

				updateMessage("Checking version...");
				updateProgress(50, max);
				versionHandler = new VersionHandler();
				versionHandler.check();

				Platform.runLater(() -> {
					splashController.versionLbl.setText("Version " + versionHandler.version);
					splashController.buildLbl.setText("Build " + versionHandler.build);
				});

				updateMessage("Reading schema files...");
				updateProgress(60, max);
				schemaReader = new SchemaReader();
				schemaReader.read();

				updateMessage("Importing referenced documents...");
				updateProgress(70, max);
				importer = new ImportManager();
				importer.read();

				updateMessage("Reading patch trackers...");
				updateProgress(80, max);
				queueManager = new PatchQueueManager();
				queueManager.read();

				updateMessage("Initializing flagger...");
				updateProgress(90, max);
				flagger = new FlagHandler();

				return null;
			}

			@Override
			protected void succeeded() {

				updateMessage("Done!");
				updateProgress(100, max);

				try {
					loadMainInterface(stage);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Network stuff

			}
		};

		splashController.progressBar.progressProperty().bind(init.progressProperty());
		splashController.workText.textProperty().bind(init.messageProperty());

		init.exceptionProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				Exception ex = (Exception) newValue;
				ex.printStackTrace();
			}
		});

		Thread thread = new Thread(init);
		thread.setName("DocImport Thread");
		thread.setDaemon(true);
		thread.start();
	}

	public void loadSplashInterface(Stage stage) throws IOException, InterruptedException {
		FXMLLoader loader = new FXMLLoader(MainController.class.getResource("Splash.fxml"));
		Parent root = loader.load();
		splashController = loader.getController();

		Scene scene = new Scene(root);
		scene.getStylesheets().add(MainController.class.getResource("sys_font.css").toExternalForm());
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
	}

	public void loadMainInterface(Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(MainController.class.getResource("MainRealResponsive.fxml"));
		Parent root = loader.load();
		controller = loader.getController();

		Scene scene = new Scene(root);

		scene.getStylesheets().add(MainController.class.getResource("style.css").toExternalForm());
		scene.getStylesheets().add(MainController.class.getResource("sys_font.css").toExternalForm());

		mainStage = new Stage();
		mainStage.getIcons().add(new Image(Assets.getURLPath("/icon.png").toString()));
		mainStage.initStyle(StageStyle.DECORATED);
		mainStage.setScene(scene);
		mainStage.setResizable(true);

		// Set the bare minimum
		mainStage.setMinWidth(660);
		mainStage.setMinHeight(700);

		// Set the bare maximum
		Rectangle2D screen = Screen.getPrimary().getBounds();
		double sw = screen.getWidth();
		double sh = screen.getHeight();
		mainStage.setMaxWidth(sw);
		mainStage.setMaxHeight(sh);

		Log.info("Maximum at " + sw + "x" + sh + " resolution.");

		mainStage.setTitle("Vitre Desktop Teacher Application (" + versionHandler.getVersion() + " b"
				+ versionHandler.getBuildNumber() + ")");

		PauseTransition pause = new PauseTransition(Duration.seconds(0));
		pause.setOnFinished(e -> {
			mainStage.show();
			stage.close();

			double w = mainStage.getWidth();
			double h = mainStage.getHeight();

			Log.info("Launched at " + w + "x" + h + " resolution.");
		});

		pause.play();

		// Initialize thecontroller
		controller.init();

		// Validate network
		NetworkManager.safeCheckOnline();
		NetworkManager.updateUI();

	}

	public static void main(String[] args) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %5$s%6$s%n");
		launch(args);
	}

	public static FlagHandler getFlagger() {
		return flagger;
	}

	public static boolean hasView() {
		return controller.hasView;
	}

	public static void setView(boolean bool) {
		controller.hasView = bool;
	}

	public static boolean hasActiveDocument() {
		return importer.hasActiveImport();
	}

	public static WorkbookDocument getActiveDocument() {
		return importer.getActiveImport().getWorkbookDocument();
	}

	public static ClassRecord getActiveClassRecord() {
		return getActiveDocument().getClassRecord();
	}

	public static ContextManager getActiveContextor() {
		return getActiveDocument().getContextManager();
	}

	public static PatchQueueManager getQueueManager() {
		return queueManager;
	}

	public static Stage getMainStage() {
		return mainStage;
	}

	@Override
	public void stop() throws Exception {
		importer.write();
		queueManager.write();
		if (hasActiveDocument())
			getActiveContextor().write();

		super.stop();
	}
}
