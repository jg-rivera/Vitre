package local.vitre.desktop.record.patch;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.json.simple.JSONObject;

import local.vitre.desktop.record.ComponentType;
import local.vitre.desktop.ui.CellMetadata;
import local.vitre.desktop.ui.CellMetadata.DataType;
import local.vitre.desktop.ui.CellUpdate;
import local.vitre.desktop.ui.UIHandler;
import local.vitre.desktop.util.Utils;

public class PatchContext {

	public static final String CONTEXT_EXTENSION = ".ctx";

	private String description;
	private String component;
	private String seed;
	private long timeStamp;

	private double cap;
	private boolean rendered;

	private int quarter;
	private int itemIndex;
	private int componentIndex;
	private int columnIndex;
	private int id;

	public PatchContext(int quarter, boolean generate) {
		if (generate) {
			timeStamp = Utils.time();
			this.seed = Utils.createUniqueHash();
		}
	}

	@SuppressWarnings("unchecked")
	public JSONObject createJSON() {
		JSONObject values = new JSONObject();
		values.put("contextIndex", id);
		values.put("itemIndex", itemIndex);
		values.put("cap", cap);
		values.put("description", description);
		values.put("component", component);
		values.put("componentIndex", componentIndex);
		values.put("stamp", timeStamp);
		return values;
	}

	public void updateCells(int rowCount, DataType type) {
		ArrayList<CellUpdate> updates = new ArrayList<>();

		for (int row = 0; row <= rowCount; row++) {
			CellUpdate upd = cellUpdate(row, type);
			updates.add(upd);
		}

		for (CellUpdate upd : updates) {
			SwingUtilities.invokeLater(() -> UIHandler.fireCellMetadataUpdate(upd));
		}

		CellUpdate update = new CellUpdate();
		update.column = itemIndex + 2;
		update.type = ComponentType.values()[componentIndex];
		update.metadata = new CellMetadata(update.column, type);

		SwingUtilities.invokeLater(() -> UIHandler.fireCellHeaderMetadataUpdate(update));
		setRendered(true);
	}

	private CellUpdate cellUpdate(int row, DataType type) {
		CellUpdate update = new CellUpdate();
		update.column = itemIndex + 2;
		update.row = row;
		update.metadata = new CellMetadata(update.row, update.column, type);
		update.type = ComponentType.values()[componentIndex];
		return update;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PatchContext)
			return ((PatchContext) obj).getID() == id;
		return super.equals(obj);
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	public boolean isRendered() {
		return rendered;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCap(double cap) {
		this.cap = cap;
	}

	public void setItemIndex(int index) {
		this.itemIndex = index;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public void setComponentName(String component) {
		this.component = component;
	}

	public void setComponentIndex(int index) {
		this.componentIndex = index;
	}

	public void setColumn(int col) {
		this.columnIndex = col;
	}

	public void setID(int id) {
		this.id = id;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}

	public double getCap() {
		return cap;
	}

	public int getID() {
		return id;
	}

	public int getQuarter() {
		return quarter;
	}

	public String getComponentName() {
		return component;
	}

	public String getDescription() {
		return description;
	}

	public int getItemIndex() {
		return itemIndex;
	}

	public int getComponentIndex() {
		return componentIndex;
	}

	public String getSeed() {
		return seed;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public int getColumn() {
		return columnIndex;
	}
}
