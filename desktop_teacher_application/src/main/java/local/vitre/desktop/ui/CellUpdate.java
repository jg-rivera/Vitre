package local.vitre.desktop.ui;

import local.vitre.desktop.record.ComponentType;
import local.vitre.desktop.ui.CellMetadata.DataType;

public class CellUpdate {

	public int row;
	public int column;

	public CellMetadata metadata;
	public Object value;
	public ComponentType type;

	@Override
	public String toString() {
		return type.name() + " (" + row + ", " + column + ") -> " + value;
	}

	public DataType getDataType() {
		return metadata.type;
	}
}
