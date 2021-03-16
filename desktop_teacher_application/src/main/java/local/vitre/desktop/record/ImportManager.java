
package local.vitre.desktop.record;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.scene.layout.HBox;
import local.vitre.desktop.Log;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.record.data.Flushable;
import local.vitre.desktop.task.TaskHandler;
import local.vitre.desktop.ui.CellMetadata;
import local.vitre.desktop.ui.FileChooserPaneHandler;

@SuppressWarnings("unchecked")
public class ImportManager implements Flushable {

	private ArrayList<DocumentImport> imports;
	private JSONObject masterImportJSON;
	private String jsonPath;
	private String currentJson;

	private FileChooserPaneHandler fileChooserHandler;

	public ImportManager() {
		this.imports = new ArrayList<>();
		this.fileChooserHandler = new FileChooserPaneHandler(this);
		this.jsonPath = Vitre.assets.getEntryValue("IMPORT_RECORD_FILE");
	}

	/**
	 * Writes the JSON file of the current imported files.
	 */
	public void write() {
		if (imports.isEmpty() && currentJson.isEmpty()) {
			Log.warn("No imported records to write.");
			return;
		}

		Log.info("IMPORT", "Creating JSON of current imports...");

		masterImportJSON = new JSONObject();

		// Extracts individual JSON data from each DocumentImport
		for (DocumentImport imp : imports) {
			String key = imp.getName();
			JSONObject value = imp.createJSON();
			masterImportJSON.put(key, value);
		}

		// Consumes writing if the state remains the same
		if (masterImportJSON.toJSONString().equals(currentJson)) {
			Log.info("IMPORT", "No changes found in imports.");
			return;
		}

		Log.info("IMPORT", "Writing JSON of imported records to " + jsonPath);

		// Write json file
		try (FileWriter file = new FileWriter(jsonPath, false)) {
			String json = masterImportJSON.toJSONString();
			file.write(json);
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.info("IMPORT", "Written JSON of imported records.");
	}

	/**
	 * Reads the last created JSON imported files.
	 */
	public void read() {
		JSONParser parser = new JSONParser();
		Log.info("IMPORT", "Importing files...");

		try {
			JSONObject readObj = (JSONObject) parser.parse(new FileReader(jsonPath));
			Iterator<Object> it = readObj.keySet().iterator();
			currentJson = readObj.toJSONString();

			while (it.hasNext()) {
				String name = (String) it.next();
				JSONObject values = (JSONObject) readObj.get(name);
				String path = (String) values.get("path");
				String schema = (String) values.get("schema");
				int sortIndex = ((Number) values.get("index")).intValue();

				File file = new File(path);

				if (!file.exists())
					continue;

				String filePath = file.getAbsolutePath();
				String fileName = file.getName();

				DocumentImport imp = new DocumentImport(fileName, filePath);
				imp.setCached(true);
				imp.setSchema(schema);
				imp.setSortIndex(sortIndex);

				String log = "Imported " + fileName;

				Log.info("ASSET", log);

				boolean isCreated = imp.create();

				if (isCreated) {
					addImport(imp);
				}
			}

			masterImportJSON = readObj;
		} catch (IOException | ParseException e) {
			Log.warn("Nothing imported.");
		}
	}

	public void flush() {
		for (DocumentImport i : imports) {
			i.flush();
		}
		imports.clear();
		masterImportJSON = null;
	}

	public void initUI(HBox fileChooserPane) {
		Log.info("IMPORT", "Initialized UI.");
		buildUI(fileChooserPane);
		if (!imports.isEmpty()) {
			redraw(fileChooserPane);
		}
	}

	public boolean addImport(DocumentImport newImport) {
		if (imports.contains(newImport)) {
			Log.warn("Attempted to import duplicate " + newImport.getName() + " record.");
			return false;
		}
		if (newImport.getSortIndex() <= 0) {
			Log.fine("IMPORT", "Default index set for " + newImport.getName());
			newImport.setSortIndex(getImportSize() + 1);
		}
		imports.add(newImport);
		return true;
	}

	public void handleReordering() {
		if (!imports.isEmpty()) {

		}
	}

	public boolean importAll(List<File> files) {
		if (files != null)
			if (!files.isEmpty()) {
				for (File file : files)
					TaskHandler.importRecord(file);
				return true;
			}
		return false;
	}

	public boolean removeImport(DocumentImport targetImport) {
		String name = targetImport.getName();
		if (masterImportJSON.containsKey(name))
			masterImportJSON.remove(name);
		fileChooserHandler.remove(name, Vitre.controller.fileChooserPane);

		return imports.remove(targetImport);
	}

	public void setActiveImport(String name) {
		DocumentImport imp = getImport(name);

		// Handle activation handling
		if (hasActiveImport()) {
			if (imports.size() > 1) {
				for (DocumentImport di : imports) {
					if (di.getName() != name && di.isActive()) {
						di.colorizeUI(CellMetadata.TP_CSS);
						di.setActivatedStyling(true);
						Vitre.getActiveContextor().write();
						Vitre.controller.destroy();
						di.setActive(false);
					}
				}
			}
		}

		// Deactivate procedure
		if (imp.isActive()) {
			Vitre.controller.destroy();
			Vitre.getActiveContextor().write();
			imp.colorizeUI(CellMetadata.TP_CSS);
			imp.setActivatedStyling(true);
			imp.setActive(false);
			return;
		}

		Vitre.controller.enable(true);
		Vitre.controller.menuBtnQuarter.setText("1st");
		TaskHandler.loadToView(imp.getWorkbookDocument(), 1);
		imp.setActivatedStyling(false);
		imp.setActive(true);

		Log.info("IMPORT", "Current active record: " + name);

	}

	public void destroyImport(DocumentImport imp) {
		imp.setActivatedStyling(false);
		if (imp.isActive()) {
			imp.setActive(false);
			Vitre.controller.destroy();
		}
	}

	public boolean hasActiveImport() {
		return getActiveImport() != null;
	}

	public DocumentImport getActiveImport() {
		for (DocumentImport imp : imports)
			if (imp.isActive())
				return imp;
		return null;
	}

	public DocumentImport getImport(String name) {
		for (DocumentImport imp : imports)
			if (imp.getName().equals(name))
				return imp;
		throw new NullPointerException("No imported record found with name: " + name);
	}

	public ArrayList<DocumentImport> getImports() {
		return imports;
	}

	public int getImportSize() {
		return imports.size();
	}

	public boolean empty() {
		return imports.isEmpty();
	}

	public void buildUI(HBox fileChooserPane) {
		fileChooserHandler.buildUI(fileChooserPane);
	}

	public void redraw(HBox fileChooserPane) {
		Log.fine("IMPORTER", "Redrew UI.");
		fileChooserHandler.redraw(fileChooserPane);
	}

}
