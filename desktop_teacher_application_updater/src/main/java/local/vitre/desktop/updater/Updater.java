package local.vitre.desktop.updater;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

@SuppressWarnings("unchecked")
public class Updater {

	private static final String DROPBOX_ACCESS_TOKEN = "<REDACTED>";

	public int currentBuildNumber;
	public String currentPackageName;
	public String currentVersion;

	public int updateBuildNumber;
	public String updatePackageName;
	public String updateVersion;

	public boolean successUpdate;
	public boolean hasUpdate;
	private File home;

	private ProgressBar progress;
	private TextArea workText;

	public Updater(ProgressBar progress, TextArea workText) {
		this.progress = progress;
		this.workText = workText;
		this.home = getHome();
	}

	public void check() throws Exception {
		DbxClientV2 client = requestClient();
		checkUpdate(client);
		if (hasUpdate) {
			System.out.println("!! Update found !!");
			System.out.println("Latest: " + updateVersion + " (" + updateBuildNumber + "), Current: " + currentVersion
					+ " (" + currentBuildNumber + ")");
		} else {
			System.out.println("!! No update found !!");
		}
	}

	public void initialize() throws Exception {
		DbxClientV2 client = requestClient();
		doUpdate(client);
	}

	private void doUpdate(DbxClientV2 client) throws Exception {
		DbxDownloader<FileMetadata> downloader = null;
		double size;
		log("Downloading package: " + updatePackageName);

		// Download package name
		try {
			downloader = client.files().download("/packages/".concat(updatePackageName));
			size = downloader.getResult().getSize() / 1000;
		} catch (Exception e) {
			log("Unable to download package: " + updatePackageName);
			e.printStackTrace();

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Update failed!");
			alert.setContentText(
					"Tried to retrieve '" + updatePackageName + "'. Failed with " + e.getLocalizedMessage());
			alert.setHeaderText("Unable to download update.");
			alert.showAndWait();
			return;
		}

		try {
			String path = home + File.separator + Main.DROP_JAR_NAME;
			FileOutputStream out = new FileOutputStream(new File(path), false);
			downloader.download(out, (bytes) -> {
				double b = bytes / 1000;
				double prog = (b / size) * 100;
				Platform.runLater(() -> progress.progressProperty().set(prog));
				log("Downloading package: " + updatePackageName + " (" + b + " / " + size + " kB)");
			});
			log("Extracted to " + path);

			downloader.close();
			out.close();
		} catch (DbxException ex) {
			ex.printStackTrace();
		}

		log("Downloaded package: " + updatePackageName);
		log("Updated current package to latest.");

		// Update local update manifest.
		JSONObject localJson = new JSONObject();
		localJson.put("build_number", updateBuildNumber);
		localJson.put("build_version", updateVersion);
		localJson.put("build_package", updatePackageName);

		try (FileWriter newJsonFile = new FileWriter(home + "/update.mf", false)) {
			String json = localJson.toJSONString();
			newJsonFile.write(json); 
			newJsonFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		log("Updated local update manifest.");
		log("Successfully updated to version " + updateVersion + " (build " + updateBuildNumber + ")");
		successUpdate = true;
	}

	private void checkUpdate(DbxClientV2 client) throws Exception {

		DbxDownloader<FileMetadata> downloader = client.files().download("/update.mf");

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			long size = downloader.getResult().getSize() / 1000;
			downloader.download(out, (bytes) -> {
				// File download progress
				long b = bytes / 1000;
				log("Downloading server update manifest: update.mf (" + b + " / " + size + " kB)");
			});

			log("Downloaded server update manifest.");

			JSONParser parser = new JSONParser();
			JSONObject serverJson = (JSONObject) parser.parse(out.toString());
			int latestBuild = Integer.parseInt(serverJson.get("build_number").toString());
			String latestPackage = (String) serverJson.get("build_package");
			String latestVersion = (String) serverJson.get("build_version");

			File file = new File(home + "/update.mf");
			JSONObject localJson = (JSONObject) parser.parse(new FileReader(file));
			int currentBuild = Integer.parseInt(localJson.get("build_number").toString());
			String currentVersion = (String) localJson.get("build_version");

			this.hasUpdate = latestBuild > currentBuild;
			this.currentBuildNumber = currentBuild;
			this.currentVersion = currentVersion;

			this.updateBuildNumber = latestBuild;
			this.updateVersion = latestVersion;
			this.updatePackageName = latestPackage;

			downloader.close();
			out.close();
		} catch (DbxException ex) {
			ex.printStackTrace();
		}
	}

	public void log(String msg) {
		if (workText != null)
			Platform.runLater(() -> workText.appendText("\n" + msg));
		System.out.println("UPDATER: " + msg);
	}

	private DbxClientV2 requestClient() {
		DbxRequestConfig config = new DbxRequestConfig("dropbox/vitre-cloud");
		DbxClientV2 client = new DbxClientV2(config, DROPBOX_ACCESS_TOKEN);
		return client;
	}

	public File getHome() {
		String userHome = System.getProperty("user.home");
		if (userHome == null) {
			throw new IllegalStateException("No user home found.");
		}

		File home = new File(userHome + File.separator + ".vitre");

		if (!home.exists()) {
			home.mkdir();
		}
		return home;
	}
}
