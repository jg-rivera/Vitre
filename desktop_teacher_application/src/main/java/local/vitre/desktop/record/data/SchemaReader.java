package local.vitre.desktop.record.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import local.vitre.desktop.Log;
import local.vitre.desktop.Vitre;

public class SchemaReader {

	private ArrayList<Recognizer> schemas;
	private String name;
	private File file;

	public SchemaReader() {
		this.file = Vitre.assets.getEntryFile("RECORD_SCHEMA_FILE");
		this.name = file.getName();
		this.schemas = new ArrayList<>();
	}

	public void read() {

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			int index = -1;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#") || line.isEmpty())
					continue;

				if (line.startsWith("[")) {
					String name = line.substring(1, line.length() - 1);
					Recognizer sch = new Recognizer(name);
					schemas.add(sch);
					index = schemas.size() - 1;
					continue;
				}

				if (index >= 0) {
					String trimmed = line;
					boolean trim = false;
					if (!line.split("=")[1].trim().startsWith("\""))
						trimmed = line.replaceAll("\\s", "");
					else
						trim = true;

					String[] raw = trimmed.split("=");
					String key = raw[0];

					String value = raw[1];
					if (trim) {
						key = key.trim();
						value = value.replace("\"", "");
					}
					Recognizer sch = schemas.get(index);

					if (key.equalsIgnoreCase("config_path")) {
						if (!value.contains(":")) {
							Log.warn(key + " for " + sch.getName() + " does not have locale identifier.");
							break;
						}
						String[] valueArray = value.split(":");
						sch.setFileHandle(valueArray[0]);
						sch.setConfigPath(valueArray[1]);
						continue;
					} else if (key.equalsIgnoreCase("sheet_count")) {
						sch.setSheetCount(Integer.parseInt(value));
						continue;
					} else if (key.equalsIgnoreCase("sheets")) {
						if (value.contains(",")) {
							ArrayList<String> values = new ArrayList<>();
							String[] valueArray = value.split(",");
							for (String s : valueArray)
								values.add(s.trim());
							sch.setSheets(values);
							continue;
						}
						ArrayList<String> values = new ArrayList<>();
						values.add(value);
						sch.setSheets(values);
						continue;
					} else {
						Log.warn("Unidentified key value: " + key + ". Handle not supported; Ignoring key.");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.severe("SCHEMA", "Error reading schema file.");
		}

	}

	public String getName() {
		return name;
	}

	public Recognizer determineSchema(WorkbookDocument workDoc) throws IllegalArgumentException {
		for (Recognizer sch : schemas) {
			ArrayList<String> sheetNames = workDoc.getSheetNames();
			int sheetCount = workDoc.getSheetCount();

			Log.fine("SCHEMA", "Comparing schema contents " + sheetNames.toString() + " to workdocument "
					+ sch.getSheets().toString());
			if (sch.getSheets().equals(sheetNames) && sch.getSheetCount() == sheetCount) {
				Log.fine("SCHEMA",
						"Using appropriate schema: " + sch.getName() + " for workdocument " + workDoc.getName());
				return sch;
			}
		}
		throw new IllegalArgumentException("No working schema found for workdocument: " + workDoc.getName());
	}

	public RecordConfiguration getConfig(WorkbookDocument workDoc) {
		return determineSchema(workDoc).getConfig(workDoc);
	}

	public Recognizer getSchema(String name) {
		for (Recognizer sch : schemas)
			if (sch.getName().equalsIgnoreCase(name))
				return sch;
		Log.warn("Schema not found: " + name);
		return null;
	}

	public ArrayList<Recognizer> getSchemas() {
		return schemas;
	}
}
