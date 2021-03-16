package local.vitre.desktop.ui;

import java.awt.Color;

import local.vitre.desktop.record.patch.file.PatchFile;

public class CellMetadata {

	private static int opacity = 100;

	/*
	 * Title pane styling for DocumentImports
	 */
	public static final String TP_ACTIVE_CSS = "linear-gradient(to right, #f9c667 0%,#f79621 100%);";
	public static final String TP_CSS = "linear-gradient(to bottom, rgba(76,144,162,1) 0%,rgba(76,144,162,1) 100%);";

	public static final Color UNKNOWN_COLOR = new Color(245, 245, 245, 255);
	public static final Color MISSING_COLOR = new Color(244, 0, 0, opacity);
	public static final Color SEEN_MISSING_COLOR = new Color(255, 146, 0, opacity);

	public static final Color UPDATE_COLOR = new Color(255, 232, 85, opacity);
	public static final Color BLANK_COLOR = new Color(229, 229, 229, opacity);
	public static final Color NEW_COLOR = new Color(152, 102, 254, opacity); // new
	public static final Color CACHED_COLOR = new Color(83, 255, 71, opacity); // green
	public static final Color CONTEXT_COLOR = new Color(0, 115, 230, 50);
	public static final Color CONTEXT_CACHED_COLOR = UIHandler.blend(CONTEXT_COLOR, CACHED_COLOR);
	public static final Color CONTEXT_FG = new Color(50, 69, 166, 255);
	public static final Color CACHED_FG = new Color(0, 80, 0, 255);
	public static final Color NEW_FG = new Color(71, 37, 77, 255);
	public static final Color selection = new Color(255, 224, 0, 200);

	public static final Color numberIndex = new Color(255, 0, 255, 50);
	public static final Color male = new Color(136, 236, 255, 200);
	public static final Color female = Color.pink;

	private Color backgroundColor;
	private Color foregroundColor;

	private Object data;

	public enum DataType {
		REQ_UPDATE(UPDATE_COLOR), CONTEXT(CONTEXT_COLOR, Color.BLUE),

		NEW(NEW_COLOR), MISSING(MISSING_COLOR), SEEN_MISSING(SEEN_MISSING_COLOR),

		CACHED(CACHED_COLOR,
				CACHED_FG), CACHED_CONTEXT(CONTEXT_CACHED_COLOR), UNKNOWN(UNKNOWN_COLOR), BLANK(BLANK_COLOR);

		private Color backgroundColor;
		private Color foregroundColor;

		DataType(Color backgroundColor) {
			this.backgroundColor = backgroundColor;
		}

		DataType(Color backgroundColor, Color foregroundColor) {
			this.backgroundColor = backgroundColor;
			this.foregroundColor = foregroundColor;
		}

		public Color getBackgroundColor() {
			return backgroundColor;
		}

		public Color getForegroundColor() {
			return foregroundColor;
		}
	}

	public DataType type;
	public int row, col;

	public CellMetadata(int rowIndex, int columnIndex, Object data) {
		this.row = rowIndex;
		this.col = columnIndex;
		this.data = data;
		process();
	}

	public CellMetadata(int rowIndex, int columnIndex, Object data, DataType type) {
		this.row = rowIndex;
		this.col = columnIndex;
		this.data = data;
		setType(type);
	}

	public CellMetadata(int columnIndex) {
		this.col = columnIndex;
	}

	public CellMetadata(int columnIndex, DataType type) {
		this.col = columnIndex;
		setType(type);
	}

	public CellMetadata(int rowIndex, int columnIndex, DataType type) {
		this.row = rowIndex;
		this.col = columnIndex;
		setType(type);
	}

	private void process() {
		if (data.equals(PatchFile.MISSING_DATA_VALUE)) {
			setType(DataType.MISSING);
			setData("MISS");
		} else if (data.equals(PatchFile.NIL_DATA_VALUE)) {
			setType(DataType.BLANK);
			setData("");
		} else {
			if (row > 0)
				setType(DataType.BLANK);
		}
	}

	public boolean matches(int rowIndex, int columnIndex) {
		return this.row == rowIndex && this.col == columnIndex;
	}

	public void setType(DataType type) {
		this.type = type;
		setBackgroundColor(type.getBackgroundColor());
		setForegroundColor(type.getForegroundColor());
	}

	public void setBackgroundColor(Color color) {
		this.backgroundColor = color;
	}

	public void setForegroundColor(Color color) {
		this.foregroundColor = color;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Color getForegroundColor() {
		return foregroundColor;
	}

	public boolean hasBackground() {
		return backgroundColor != null;
	}

	public boolean hasForeground() {
		return foregroundColor != null;
	}

	public Object getData() {
		return data;
	}
}
