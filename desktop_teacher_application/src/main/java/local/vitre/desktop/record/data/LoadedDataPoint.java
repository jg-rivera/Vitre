package local.vitre.desktop.record.data;

import java.util.ArrayList;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.CellType;

import local.vitre.desktop.Log;

public class LoadedDataPoint implements Flushable {

	private String tag;
	private String coordinate;
	private int sheetIndex;

	private Object value;
	private ArrayList<Object> values;

	private WorkbookDocument tool;
	private boolean multiValued;
	private boolean allowEmpty;
	private int emptyCount;

	public LoadedDataPoint(WorkbookDocument tool, String tag, String coordinate, int sheetIndex, boolean allowEmpty) {
		this.tool = tool;
		this.tag = tag;
		this.values = new ArrayList<>();
		this.allowEmpty = allowEmpty;
		this.coordinate = coordinate;
		this.sheetIndex = sheetIndex;
		process();
		if (allowEmpty)
			fill();
	}

	public LoadedDataPoint(WorkbookDocument tool, String tag, String coordinate, int sheetIndex) {
		this.tool = tool;
		this.tag = tag;
		this.values = new ArrayList<>();
		this.allowEmpty = false;
		this.coordinate = coordinate;
		this.sheetIndex = sheetIndex;
		process();
		if (allowEmpty)
			fill();
	}

	public LoadedDataPoint(WorkbookDocument tool, String tag, String value) {
		this.tool = tool;
		this.tag = tag;
		this.value = value;
		this.values = new ArrayList<>();
		Log.fine("DATA", "Found ConfPoint '" + tag + "' -> " + value);
	}

	/**
	 * Joins an external data point value array to this data point's value
	 * array.
	 * 
	 * @return Unmodifiable concatenated list
	 */
	public MergedDataList<Object> merge(LoadedDataPoint point) {
		if (!multiValued && !point.isMultiValued())
			Log.severe("DATA", "Datapoint '" + tag + "' from " + coordinate + " IS NOT multivalued! Cannot merge!");
		ArrayList<Object> valuesToMerge = point.getValues();

		if (valuesToMerge.isEmpty()) {
			Log.severe("DATA", "Data point to merge is empty.");
			throw new IllegalStateException("Cannot merge an empty data point");
		}
		return new MergedDataList<>(values, valuesToMerge);
	}

	public void setAll(Object value) {
		if (multiValued) {
			for (int i = 0; i < values.size(); i++) {
				values.set(i, value);
			}
		}
	}

	private void fill() {
		if (multiValued) {
			for (int i = 0; i < values.size(); i++) {
				Object o = values.get(i);
				if (o instanceof String) {
					String s = (String) o;
					values.set(i, NumberUtils.toInt(s, -1));
					emptyCount++;
				}
			}
		}
	}

	private void process() {
		/*
		 * Multivalued data processing
		 */
		// Vertical
		if (coordinate.contains("[")) {
			String[] raw = coordinate.split("\\[");
			values.addAll(tool.verticalScan(sheetIndex, raw[0], Integer.parseInt(raw[1]), allowEmpty));
			if (Log.debug)
				Log.fine("DATA", "Created new MULTIVAL LoadedDataPoint with VER_SCAN '" + tag + "' @ " + raw[0] + " to "
						+ raw[1] + " -> LIST(" + values.size() + ")");
			multiValued = true;
		}
		// Horizontal
		else if (coordinate.contains("]")) {
			String[] raw = coordinate.split("\\]");
			values.addAll(tool.horizontalScan(sheetIndex, raw[0], raw[1]));
			if (Log.debug)
				Log.fine("DATA", "Created new MULTIVAL LoadedDataPoint with HOR_SCAN '" + tag + "' @ " + raw[0] + " to "
						+ raw[1] + " -> LIST(" + values.size() + ")");
			multiValued = true;
		}
		// Primitive or Single-valued
		else {
			CellType type = tool.getCellValueTypeAt(sheetIndex, coordinate);
			switch (type) {
			case STRING:
				value = tool.getCellStringValueAt(sheetIndex, coordinate);
				break;
			case NUMERIC:
				value = tool.getCellNumericValueAt(sheetIndex, coordinate);
				break;
			case FORMULA:
				value = tool.getCellStringValueAt(sheetIndex, coordinate);
			default:
				break;
			}
			Log.fine("DATA", "Created new PRIMITIVE LoadedDataPoint " + type.name() + " '" + tag + "' @ " + coordinate + " -> "
					+ value);
		}
	}

	public String getTag() {
		return tag;
	}

	public boolean isMultiValued() {
		return multiValued;
	}

	public boolean hasEmptyValues() {
		return getEmptyCount() > 0;
	}

	public boolean isEmpty() {
		if (multiValued)
			return values.isEmpty();
		return false;
	}

	public String getCoordinate() {
		return coordinate;
	}

	public int getNonEmptyCount() {
		if (allowEmpty && multiValued)
			return values.size() - emptyCount;

		Log.severe("DATA", "Datapoint '" + tag + "' from " + coordinate + " does not allow empty values.");
		throw new IllegalArgumentException();
	}

	public int getEmptyCount() {
		if (allowEmpty && multiValued)
			return emptyCount;
		Log.severe("DATA", "Datapoint '" + tag + "' from " + coordinate + " does not allow empty values.");
		throw new IllegalArgumentException();
	}

	public Object getValue() {
		if (multiValued)
			Log.severe("DATA", "Datapoint '" + tag + "' from " + coordinate + " IS multivalued!");
		return value;
	}

	public ArrayList<Object> getValues() {
		if (!multiValued)
			Log.severe("DATA", "Datapoint '" + tag + "' from " + coordinate + " IS NOT multivalued!");
		return values;
	}

	public int getIntegerValue() {
		return Integer.parseInt((String) value);
	}

	public void flush() {
		value = null;
		values.clear();
	}

}
