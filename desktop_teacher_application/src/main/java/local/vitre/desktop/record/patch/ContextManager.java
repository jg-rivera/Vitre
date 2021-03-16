package local.vitre.desktop.record.patch;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import local.vitre.desktop.Log;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.record.ClassRecord;
import local.vitre.desktop.record.ComponentType;
import local.vitre.desktop.record.patch.diff.PatchDifference;
import local.vitre.desktop.ui.CellMetadata.DataType;
import local.vitre.desktop.ui.UIHandler;

/**
 * The ContextManager handles all created {@link PatchContext} within a class
 * record. It writes patch contexts into a JSON and reads them for reuse.
 * 
 * @author Gab
 *
 */
@SuppressWarnings("unchecked")
public class ContextManager {

	private ArrayList<PatchContext> contexts;
	private String filePath;
	private ClassRecord record;
	private JSONObject contextStateJSON;

	private int itemCount;
	private int nextID;
	private int quarter;

	/**
	 * Creates a context manager for this classrecord with the given file name.
	 * 
	 * @param record
	 * @param filePath
	 */
	public ContextManager(ClassRecord record, String filePath) {
		this.record = record;
		this.itemCount = record.getAllItemCount();
		this.quarter = record.getCurrentQuarter();
		this.filePath = filePath + "_Q" + quarter + PatchContext.CONTEXT_EXTENSION;
		contexts = new ArrayList<>();
	}

	/**
	 * Writes all created patch contexts to a serialized JSON file.
	 */
	public void write() {
		Log.info("CONTEXT", "Writing context JSON to " + filePath);

		contextStateJSON = allPatchContextToJSON();

		try (FileWriter file = new FileWriter(filePath, false)) {
			String json = contextStateJSON.toJSONString();
			file.write(json);
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads the available patch contexts in the current record.
	 */
	public void read() {
		Log.info("CONTEXT", "Reading context JSON from " + filePath);
		JSONParser parser = new JSONParser();

		try {
			JSONObject obj = (JSONObject) parser.parse(new FileReader(filePath));
			Iterator<Object> it = obj.keySet().iterator();

			while (it.hasNext()) {
				String seed = (String) it.next();

				JSONObject values = (JSONObject) obj.get(seed);
				int id = ((Long) values.get("contextIndex")).intValue();
				int itemIndex = ((Long) values.get("itemIndex")).intValue();
				int componentIndex = ((Long) values.get("componentIndex")).intValue();
				long stamp = (long) values.get("stamp");

				double cap = (double) values.get("cap");
				String description = (String) values.get("description");
				String component = (String) values.get("component");

				PatchContext con = new PatchContext(record.getCurrentQuarter(), false);
				con.setID(id);
				con.setTimeStamp(stamp);
				con.setItemIndex(itemIndex);
				con.setComponentIndex(componentIndex);
				con.setCap(cap);
				con.setDescription(description);
				con.setComponentName(component);
				con.setSeed(seed);

				addContext(con, false);

				if (!it.hasNext())
					nextID = id;
			}

			Log.info("CONTEXT", "Read " + contexts.size() + " patch context(s)");
		} catch (IOException | ParseException e) {
			Log.warn("Nothing imported by context manager.");

		}
	}

	public void sort() {
		Collections.sort(contexts, new Comparator<PatchContext>() {
			public int compare(PatchContext o1, PatchContext o2) {
				if (o1.getItemIndex() == o2.getItemIndex())
					return 0;
				return o1.getItemIndex() < o2.getItemIndex() ? -1 : 1;
			}
		});
	}

	public void sortType(ComponentType type) {

	}

	public boolean hasAllContext() {
		return contexts.size() == itemCount;
	}

	public void showContextList() {
		sort();
		try {
			UIHandler.showPatchContextListFX(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addContext(PatchContext con, boolean write) {
		con.setID(nextID);
		nextID++;
		contexts.add(con);

		if (write) {
			write();
			if (Vitre.hasView())
				renderFullCells();
		}
	}

	public boolean updateContext(int itemIndex, int componentIndex, String desc) {
		if (!contexts.isEmpty())
			for (PatchContext con : contexts)
				if (con.getItemIndex() == itemIndex && con.getComponentIndex() == componentIndex) {
					con.setDescription(desc);
					renderFullCells();
					redrawCache();
					write();
					return true;
				}
		return false;
	}

	private void redrawCache() {
		Patcher patcher = record.getPatcher();
		PatchDifference diff = patcher.getDiff();
		if (patcher.isSynchronized()) {
			diff.updateTableCells();
		}
	}

	public void renderFullCells() {
		if (!contexts.isEmpty()) {

			for (PatchContext c : contexts) {
				c.updateCells(record.getStudentsCount(), DataType.CONTEXT);
			}

			// TODO Hard handle of cache
			redrawCache();
			Log.fine("CONTEXT", "Refreshed UI data cells");
		}
	}

	public void removeContext(PatchContext con) {
		int id = con.getID();
		if (contextStateJSON.containsKey(id))
			contextStateJSON.remove(id);
		if (Vitre.hasView()) {
			con.updateCells(record.getStudentsCount(), DataType.BLANK);
			// redrawCache();
		}
		contexts.remove(con);
	}

	public PatchContext getContextByItemIndex(int index) {
		if (!contexts.isEmpty())
			for (PatchContext con : contexts)
				if (con.getItemIndex() == index)
					return con;
		return null;
	}

	public PatchContext getContext(int itemIndex, int componentIndex) {
		if (!contexts.isEmpty())
			for (PatchContext con : contexts)
				if (con.getItemIndex() == itemIndex && con.getComponentIndex() == componentIndex)
					return con;
		return null;
	}

	public PatchContext getContextByID(int id) {
		if (!contexts.isEmpty())
			for (PatchContext con : contexts)
				if (con.getID() == id)
					return con;
		return null;
	}

	public ArrayList<PatchContext> getContexts(ComponentType type) {
		ArrayList<PatchContext> temp = new ArrayList<>();
		if (!contexts.isEmpty())
			for (PatchContext con : contexts)
				if (con.getComponentIndex() == type.ordinal())
					temp.add(con);
		return temp;
	}

	public ArrayList<PatchContext> getContexts() {
		return contexts;
	}
	
	public int getContextsSize() {
		return contexts.size();
	}

	public boolean hasContext(int itemIndex, int componentIndex) {
		if (!contexts.isEmpty())
			for (PatchContext con : contexts)
				if (con.getItemIndex() == itemIndex && con.getComponentIndex() == componentIndex)
					return true;
		return false;
	}

	public boolean removeContext(int itemIndex, int componentIndex) {
		if (!contexts.isEmpty()) {
			Iterator<PatchContext> it = contexts.iterator();
			while (it.hasNext()) {
				PatchContext con = it.next();
				if (con.getItemIndex() == itemIndex && con.getComponentIndex() == componentIndex) {
					int id = con.getID();

					if (Vitre.hasView()) {
						con.updateCells(record.getStudentsCount(), DataType.BLANK);
						// redrawCache();
					}
					it.remove();
					shiftId(id);
					write();
					return true;
				}
			}
		}
		return false;
	}

	public void shiftId(int id) {
		if (!contexts.isEmpty() && contexts.size() > 1)
			for (PatchContext con : contexts)
				if (con.getID() > id)
					con.setID(con.getID() - 1);
	}

	public JSONObject allPatchContextToJSON() {
		JSONObject ctx = new JSONObject();
		if (!contexts.isEmpty())
			for (PatchContext con : contexts)
				ctx.put(con.getSeed(), con.createJSON());
		return ctx;
	}

	public JSONObject getContextStateJSON() {
		return contextStateJSON;
	}
}
