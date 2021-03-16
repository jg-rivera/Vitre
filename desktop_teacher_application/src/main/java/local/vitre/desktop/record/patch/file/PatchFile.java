package local.vitre.desktop.record.patch.file;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;

import local.vitre.desktop.Log;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.http.Cypher;
import local.vitre.desktop.util.Utils;

public class PatchFile {

	/**
	 * Main JSON object tree
	 */
	protected JSONObject json;

	/**
	 * Handshake code
	 */

	protected String name;
	protected Random random;
	protected boolean doEncrypt = true;

	private String directory;

	public enum PatchType {
		CONTENT(".content", Vitre.assets.getEntryValue("PATCH_STORE_DIR")), 
		CACHE(".cache", Vitre.assets.getEntryValue("PATCH_STORE_DIR")), 
		CLASS(".class", Vitre.assets.getEntryValue("PATCH_STORE_DIR"));

		public String extension, directory;

		private PatchType(String extension, String directory) {
			this.extension = extension;
			this.directory = directory;
		}
	}

	public static final int NIL_DATA_VALUE = -2;
	public static final int MISSING_DATA_VALUE = -1;

	public PatchFile(String name) {
		this.name = name;
		this.directory = Vitre.assets.getEntryValue("PATCH_STORE_DIR");
		json = new JSONObject();
		random = new Random();
	}

	/**
	 * Create and encrypt the patch file.
	 */
	public String write(PatchType type) {
		Log.info("PATCH", "Writing patch file...");

		String handshakeKey = Utils.createUniqueHash(8);
		String headerKey = RandomStringUtils.randomAlphanumeric(1);
		String headerEncrKey = Cypher.encrypt(Cypher.truncate(handshakeKey), headerKey);

		String headerData = headerEncrKey + headerKey + ".";
		String bodyData = json.toJSONString();

		Log.info("PATCH", bodyData);

		if (doEncrypt) {
			bodyData = Cypher.encrypt(json.toJSONString(), handshakeKey);
			Log.fine("PATCH", "Encrypted patch file contents with key.");
		}

		try (FileWriter file = new FileWriter(type.directory + name + type.extension)) {
			file.write(headerData);
			file.write(bodyData);
			file.flush();
		} catch (IOException e) {
			Log.severe("PATCH", "Error writing patch file to FS: " + name);
			e.printStackTrace();
		}

		return headerData.concat(bodyData);
	}

	public void setEncrypt(boolean doEncrypt) {
		this.doEncrypt = doEncrypt;
	}

	public void setFileName(String name) {
		this.name = name;
	}

	public String getFileName() {
		return name;
	}

	public String getDirectory() {
		return directory;
	}

	public String toString() {
		return json.toJSONString();
	}
}
