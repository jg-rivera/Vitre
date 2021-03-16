package local.vitre.desktop.record.data;

import java.util.ArrayList;

import local.vitre.desktop.Vitre;

public class Recognizer {

	public enum SchemaFileHandle {
		ABSOLUTE("abs"), LOCAL("local");

		private String locale;

		SchemaFileHandle(String locale) {
			this.locale = locale;
		}

		public String getLocale() {
			return locale;
		}

		public static SchemaFileHandle getHandle(String locale) {
			for (SchemaFileHandle h : values())
				if (h.getLocale().equalsIgnoreCase(locale))
					return h;
			throw new NullPointerException("No file handle found with type: " + locale);
		}
	}

	private String name;
	private String parsedPath;
	private SchemaFileHandle fileHandle;
	private int sheetCount;
	private ArrayList<String> sheets;

	public Recognizer(String name) {
		this.name = name;
	}

	public void setConfigPath(String configPath) {
		this.parsedPath = configPath;
	}

	public void setSheetCount(int sheetCount) {
		this.sheetCount = sheetCount;
	}

	public void setSheets(ArrayList<String> sheets) {
		this.sheets = sheets;
	}

	public void setFileHandle(String locale) {
		this.fileHandle = SchemaFileHandle.getHandle(locale);
	}

	public String getName() {
		return name;
	}

	public String getConfigPath() {
		return parsedPath;
	}

	public RecordConfiguration getConfig(WorkbookDocument workDoc) {
		switch (fileHandle) {
		case ABSOLUTE:
			return new RecordConfiguration(workDoc, parsedPath);
		case LOCAL:
			return new RecordConfiguration(workDoc, Vitre.assets.getEntryValue("RCFG_STORE_DIR") + parsedPath);
		}
		return null;
	}

	public SchemaFileHandle getFileHandle() {
		return fileHandle;
	}

	public int getSheetCount() {
		return sheetCount;
	}

	public ArrayList<String> getSheets() {
		return sheets;
	}
}
