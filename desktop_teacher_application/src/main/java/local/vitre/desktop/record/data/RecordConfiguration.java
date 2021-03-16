package local.vitre.desktop.record.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import local.vitre.desktop.Log;

public class RecordConfiguration implements Flushable {

	public ArrayList<LoadedDataPoint> loadedData;
	public ArrayList<PreviewedDataPoint> previewedData;

	private String name;
	private File file;
	private WorkbookDocument workDoc;
	private String path;

	public RecordConfiguration(WorkbookDocument workDoc, String configPath) {
		this.workDoc = workDoc;
		this.path = configPath;
		this.file = new File(path);
		this.name = file.getName();
	}

	public void preview() {
		Log.info("CONFIG", "Previewing configuration data from " + path);
		previewedData = new ArrayList<>();
		createPreviewedDataPoints();
		Log.info("CONFIG", "Previewed " + previewedData.size() + " configuration entries from configuration file.");
	}

	private void createPreviewedDataPoints() {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			int sheetIndex = 0;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#") || line.isEmpty())
					continue;

				if (line.startsWith("_S")) {
					if (line.length() > 2) {
						sheetIndex = Integer.parseInt(line.substring(2));
						Log.fine("CONFIG", "Processing sheet index: " + sheetIndex);
					}
					continue;
				}

				String trimmed = line.replaceAll("\\s", "");
				String[] raw = trimmed.split("=");

				if (trimmed.startsWith("~")) {
					previewedData.add(new PreviewedDataPoint(workDoc, raw[0].substring(1), raw[1], sheetIndex));
					continue;
				}
				previewedData.add(new PreviewedDataPoint(workDoc, raw[0], raw[1], sheetIndex));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.severe("CONFIG", "Error reading configuration file.");
		}
	}

	public void load() {
		Log.info("CONFIG", "Reading configuration data from " + path);
		loadedData = new ArrayList<>();
		createLoadedDataPoints();
		Log.info("CONFIG", "Read " + loadedData.size() + " entries from configuration file.");
	}

	private void createLoadedDataPoints() {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			int sheetIndex = 0;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#") || line.isEmpty())
					continue;

				if (line.startsWith("_S")) {
					if (line.length() > 2) {
						sheetIndex = Integer.parseInt(line.substring(2));
						Log.fine("CONFIG", "Processing sheet index: " + sheetIndex);
					}
					continue;
				}

				String trimmed = line.replaceAll("\\s", "");
				String[] raw = trimmed.split("=");

				if (trimmed.startsWith("~")) {
					loadedData.add(new LoadedDataPoint(workDoc, raw[0].substring(1), raw[1]));
					continue;
				}
				loadedData.add(new LoadedDataPoint(workDoc, raw[0], raw[1], sheetIndex));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.severe("CONFIG", "Error reading configuration file.");
		}
	}

	public LoadedDataPoint newData(String tag, String coordinate, int sheetIndex, boolean allowEmpty) {
		return new LoadedDataPoint(workDoc, tag, coordinate, sheetIndex, allowEmpty);
	}

	public LoadedDataPoint getData(String tag) {
		for (LoadedDataPoint p : loadedData) {
			if (p.getTag().equals(tag))
				return p;
		}
		throw new NullPointerException("LoadedData '" + tag + "' not found in configuration file!");
	}

	public Object getDataValue(String tag) {
		return getData(tag).getValue();
	}

	public ArrayList<Object> getDataValues(String tag) {
		return getData(tag).getValues();
	}

	public PreviewedDataPoint getSuspendedData(String tag) {
		for (PreviewedDataPoint p : previewedData) {
			if (p.getTag().equals(tag))
				return p;
		}
		throw new NullPointerException("PreviewedData '" + tag + "' not found in configuration file!");
	}

	public WorkbookDocument getTool() {
		return workDoc;
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	@Override
	public void flush() {
		if (previewedData != null) {
			previewedData.clear();
			previewedData = null;
		}
		if (loadedData != null) {
			loadedData.clear();
			loadedData = null;
		}
	}
}
