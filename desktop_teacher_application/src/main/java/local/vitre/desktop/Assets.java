package local.vitre.desktop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import local.vitre.desktop.Assets.InitializationEntry.EntryKeyType;

/**
 * The main asset handling class designed to retrieve resources needed by the
 * system.
 * 
 * @author Gab
 *
 */
public class Assets {

	/**
	 * The collection of entries read by the asset handler.
	 */
	private ArrayList<InitializationEntry> initEntries;

	/**
	 * The initialization file.
	 */
	private File initFile;

	/**
	 * The absolute path of the user's home directory.
	 */
	private String homeDir;

	/**
	 * An object representing an entry in the initialization file.
	 * 
	 * @author Gab
	 *
	 */
	public static class InitializationEntry {

		/**
		 * The type of value this initialization key stores.
		 * 
		 * @author Gab
		 *
		 */
		public enum EntryKeyType {
			/**
			 * File type entry. Usually denoted by a file name.
			 */
			FILE,
			/**
			 * File path type entry. Usually denoted either by an absolute or
			 * local file path.
			 */
			PATH,
			/**
			 * URL type entry.
			 */
			URL;
		}

		private EntryKeyType type;
		private String key;
		private String value;

		public InitializationEntry(String key, String type, String value) {
			this.key = key;
			this.value = value;
			this.type = EntryKeyType.valueOf(type);

			if (this.type == EntryKeyType.PATH) {
				if (!this.value.endsWith(File.separator))
					this.value += File.separator;
			}
		}

	}

	/**
	 * Creates the asset handler while referencing the initialization file.
	 * 
	 * @param fileName
	 */
	public Assets(String fileName) {
		File home = Assets.getHome();
		this.initFile = new File(home + File.separator + fileName);

		if (!initFile.exists()) {
			Log.warn("Vitre initialization file not found.");
			throw new NullPointerException();
		}

		this.homeDir = home.getAbsolutePath() + File.separator;
		this.initEntries = new ArrayList<>();
	}

	/**
	 * Reads the initialization file.
	 */
	public void read() {

		try (BufferedReader br = new BufferedReader(new FileReader(initFile))) {
			String line;

			while ((line = br.readLine()) != null) {

				if (line.startsWith("#") || line.isEmpty())
					continue;
				String[] raw = line.split("=");
				String key = raw[0].trim();
				String value = raw[1].trim();

				String[] rawKey = key.replaceAll("\\s", "").split(">");
				InitializationEntry entry = new InitializationEntry(rawKey[1], rawKey[0], value);

				initEntries.add(entry);
				Log.fine("ASSET", "Read: " + entry.key + " type=" + entry.type.name());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.severe("SCHEMA", "Error reading initialization file.");
		}
		proofread();
	}

	/**
	 * Proofreads {@link EntryKeyType#FILE} and {@link EntryKeyType#PATH} asset
	 * entries; ensuring that these keys exist.
	 */
	private void proofread() {

		for (InitializationEntry e : initEntries) {
			if (e.type == EntryKeyType.FILE || e.type == EntryKeyType.PATH) {
				e.value = homeDir.concat(e.value);
				Log.info("ASSET", "Proofreaded file or path: " + e.value);
			}

		}
		Log.info("ASSET", initEntries.size() + " asset entries found in initialization.");
	}

	/**
	 * Creates a file object from this {@link InitializationEntry}.
	 * 
	 * @param key
	 *            - the key of the entry
	 * @throws IllegalArgumentException
	 *             Entry is not a file nor a path.
	 */
	public File getEntryFile(String key) {
		InitializationEntry entry = getEntry(key);
		if (entry.type == EntryKeyType.FILE || entry.type == EntryKeyType.PATH)
			return new File(entry.value);
		throw new IllegalArgumentException("Not a file nor path.");
	}

	/**
	 * Retrieves the string value from this {@link InitializationEntry}
	 * 
	 * @param key
	 *            - the key of the entry
	 */
	public String getEntryValue(String key) {
		return getEntry(key).value;
	}

	/**
	 * Retrieves the {@link InitializationEntry} from the key name.
	 * 
	 * @param key
	 *            - the key of the entry
	 */
	public InitializationEntry getEntry(String key) {
		if (!initEntries.isEmpty())
			for (InitializationEntry e : initEntries)
				if (e.key.equalsIgnoreCase(key))
					return e;
		throw new NullPointerException();
	}

	public static URL getURLPath(String file) {
		return Vitre.class.getResource(file);
	}

	public static File getHome() {
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

	public static File readFileWithin(String fileName) {
		InputStream cpResource = Assets.class.getClassLoader().getResourceAsStream(fileName);
		File file = new File(fileName);
		try {
			FileUtils.copyInputStreamToFile(cpResource, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	public static File readFile(String fileName) {
		InputStream cpResource = Assets.class.getClassLoader().getResourceAsStream(fileName);
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile(fileName, null);
			FileUtils.copyInputStreamToFile(cpResource, tmpFile);
			tmpFile.deleteOnExit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tmpFile;
	}

	public static Path dir() {
		return Paths.get("assets").toAbsolutePath();
	}

}
