package local.vitre.desktop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class VersionHandler {

	protected int build;
	protected String version;

	public void check() throws FileNotFoundException, IOException, ParseException {
		Log.fine("Checking version...");
		File file = new File(Vitre.assets.getEntryValue("UPDATE_MANIFEST"));
		JSONParser parser = new JSONParser();
		JSONObject localJson = (JSONObject) parser.parse(new FileReader(file));
		int currentBuild = Integer.parseInt(localJson.get("build_number").toString());
		String currentVersion = (String) localJson.get("build_version");

		this.build = currentBuild;
		this.version = currentVersion;
		Log.info("Current version: " + version + ", build " + build);
	}

	public int getBuildNumber() {
		return build;
	}

	public String getVersion() {
		return version;
	}
}
