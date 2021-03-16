package local.vitre.desktop.record.patch.queue;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import local.vitre.desktop.Log;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.record.ClassRecordDescriptor;
import local.vitre.desktop.record.patch.diff.PatchDifference;

public class PatchQueueManager {

	private String filePath;
	private ArrayList<PatchTracker> trackers;

	public PatchQueueManager() {
		trackers = new ArrayList<>();
		filePath = Vitre.assets.getEntryValue("PATCH_TRACKER_FILE");
	}

	@SuppressWarnings("unchecked")
	public void read() {
		JSONParser parser = new JSONParser();
		Log.info("QUEUE", "Reading tracker.json...");
		try {
			JSONObject obj = (JSONObject) parser.parse(new FileReader(filePath));
			Iterator<Object> it = obj.keySet().iterator();

			while (it.hasNext()) {
				String index = (String) it.next();
				JSONObject json = (JSONObject) obj.get(index);
				add(json);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void write() {
		JSONObject allJSON = new JSONObject();
		Log.info("QUEUE", "Writing tracker.json...");

		if (!trackers.isEmpty())
			for (PatchTracker t : trackers) {
				allJSON.put(t.getIndex(), t.toJSON());
			}

		try (FileWriter file = new FileWriter(filePath, false)) {
			String json = allJSON.toJSONString();
			file.write(json);
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void add(PatchDifference diff, ClassRecordDescriptor descriptor) {
		PatchTracker q = new PatchTracker(trackers.size(), diff, descriptor);
		trackers.add(q);
		Log.info("QUEUE", "Added new tracker from descriptor: " + q.getIndex() + " -> " + descriptor.document);
	}

	public void add(JSONObject json) {
		PatchTracker q = new PatchTracker(trackers.size(), json);
		trackers.add(q);
		Log.info("QUEUE", "Added tracker from JSON: " + q.getIndex() + " -> " + q.document);
	}

	public ArrayList<PatchTracker> getTrackers() {
		return trackers;
	}
}
