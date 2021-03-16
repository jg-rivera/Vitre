package local.vitre.desktop.record.data;

import java.util.ArrayList;

import local.vitre.desktop.Log;

public class PreviewedDataPoint {

	private String tag;
	private String coordinate;
	private int sheetIndex;

	private WorkbookDocument workDoc;

	public PreviewedDataPoint(WorkbookDocument tool, String tag, String coordinate, int sheetIndex) {
		this.tag = tag;
		this.coordinate = coordinate;
		this.sheetIndex = sheetIndex;
		this.workDoc = tool;
		Log.fine("DATA", "Created new PreviewedDataPoint '" + tag + "' @ " + coordinate);
	}

	private LoadedDataPoint load() {
		return new LoadedDataPoint(workDoc, tag, coordinate, sheetIndex, true);
	}

	public Object getValue() {
		return load().getValue();
	}

	public ArrayList<Object> getValues() {
		return load().getValues();
	}

	public String getTag() {
		return tag;
	}

	public String getCoordinate() {
		return coordinate;
	}

	public int getSheetIndex() {
		return sheetIndex;
	}
}
