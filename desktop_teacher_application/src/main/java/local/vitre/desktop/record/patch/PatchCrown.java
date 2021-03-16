
package local.vitre.desktop.record.patch;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javafx.application.Platform;
import local.vitre.desktop.FlagHandler.Flag;
import local.vitre.desktop.Log;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.http.Cypher;
import local.vitre.desktop.http.NetworkManager;
import local.vitre.desktop.record.ClassRecord;
import local.vitre.desktop.ui.UIHandler;
import local.vitre.desktop.util.Utils;

@SuppressWarnings("unchecked")
public class PatchCrown {

	private int classID;
	private int subjectID;
	private int teacherID;
	private String signature;
	private ArrayList<String> registryStudentNames;
	private boolean unregistered;
	private boolean signed;

	private JSONObject crownTree;

	public PatchCrown() {
		crownTree = new JSONObject();
		registryStudentNames = new ArrayList<>();
		Log.fine("PATCH", "Created new patch crown.");
	}

	/**
	 * Attach crown body to this JSON tree.
	 * 
	 * @param json
	 */
	public void attach(JSONObject json) {
		if (!signed) {
			Log.warn("Cannot attach unsigned crown header.");
			return;
		}

		json.put("crown", crownTree); // Crown tree
	}

	public void validate(ClassRecord record) {
		Log.fine("PATCH", "Retrieving crown IDs...");

		// Retrives network IDs from the server
		retrieveCrownIds(record);

		Platform.runLater(() -> record.updateSignatureUI(signature));

		if (unregistered) {
			Log.warn(record.getName() + " is unrecognized in server.");
			return;
		}

		signature = generateSignature(record);

		// Populate other data
		retrieveCrownChunk(record);
		retrieveCrownInput(record);
		retrieveCrownCount(record);
		signed = true;

		Platform.runLater(() -> UIHandler.toggleCheck(Vitre.controller.checkLabelVerified, true));

		Log.info("PATCH", "Crown signature for CR [" + record.getName() + "]: " + signature);
	}

	private String generateSignature(ClassRecord record) {
		String c = record.getClassSignature();
		String s = record.getSubjectSignature();
		String t = record.getTeacherSignature();

		String data = c + ":" + classID + ".";
		data += s + ":" + subjectID + ".";
		data += t + ":" + teacherID;

		Log.fine("Generating crown pre-signature for CR [" + record.getName() + "]: " + data);
		Log.info("PATCH", "Generated signature.");
		return Cypher.sha256(data);
	}

	public String getSignature() {
		if (!signed) {
			Log.warn("No signature from unsigned crown.");
			return null;
		}
		return signature;
	}

	public boolean isSigned() {
		return signed;
	}

	private void retrieveCrownCount(ClassRecord record) {
		JSONObject count = new JSONObject();
		count.put("total", record.getStudentsCount());
		count.put("male", record.getMaleStudentsCount());
		count.put("female", record.getFemaleStudentsCount());
		crownTree.put("count", count);
	}

	private void retrieveCrownIds(ClassRecord record) {
		JSONObject syncData;
		JSONObject branch = new JSONObject();

		try {
			String c = record.getClassSignature();
			String s = record.getSubjectSignature();
			String t = record.getTeacherSignature();

			syncData = NetworkManager.retrieveNetworkIDs(c, s, t);
		} catch (IOException | ParseException e) {
			Log.severe("PATCH", "Failed to retrieve network IDs");
			Vitre.getFlagger().add(Flag.CANNOT_CONNECT);
			return;
		}

		Vitre.getFlagger().remove(Flag.CANNOT_CONNECT);

		Log.fine("PATCH", "Attempting to attach and verify returned IDs to header...");

		/// Retrieves individual IDs from the sync JSON reply.
		classID = retrieveID("class", syncData);
		subjectID = retrieveID("subject", syncData);
		teacherID = retrieveID("teacher", syncData);

		boolean emptyClass = true;
		if (syncData.containsKey("emptyClass"))
			emptyClass = (boolean) syncData.get("emptyClass");

		boolean hasClassID = classID > 0;
		boolean hasSubjectID = subjectID > 0;
		boolean hasTeacherID = teacherID > 0;

		if (!hasClassID) {
			Log.warn("No class ID in server.");
			Vitre.getFlagger().add(Flag.UNREGISTERED_CLASS);
			unregistered = true;
		}

		if (!hasSubjectID) {
			Log.warn("No subject ID in server.");
			Vitre.getFlagger().add(Flag.UNREGISTERED_SUBJECT);
			unregistered = true;
		}

		if (!hasTeacherID) {
			Log.warn("No teacher ID in server.");
			Vitre.getFlagger().add(Flag.UNREGISTERED_TEACHER);
			unregistered = true;
		}

		if (emptyClass) {
			Log.warn("Empty population within class.");
			Vitre.getFlagger().add(Flag.EMPTY_CLASS);
		}

		record.setNetworkIDs(classID, teacherID, subjectID);
		Platform.runLater(() -> record.colorizeAuthData(hasClassID, hasSubjectID, hasTeacherID));

		// Populate branch with fetched IDs
		if (!unregistered && !emptyClass) {
			branch.put("class", classID);
			branch.put("subject", subjectID);
			branch.put("teacher", teacherID);
			branch.put("track", record.trackType.ordinal());
			branch.put("quarter", record.getCurrentQuarter());

			// Add registry student names to list
			JSONArray array = (JSONArray) syncData.get("registry");
			registryStudentNames.addAll(array);

			crownTree.put("id", branch);

			if (!emptyClass) {
				Log.fine("CROWN", "Class populated.");
				Vitre.getFlagger().remove(Flag.EMPTY_CLASS);
				// Vitre.controller.registerBtn.setDisable(true);
			}

			Vitre.getFlagger().remove(Flag.UNREGISTERED_CLASS);
			Vitre.getFlagger().remove(Flag.UNREGISTERED_SUBJECT);
			Vitre.getFlagger().remove(Flag.UNREGISTERED_TEACHER);

			return;
		}

		// Unregistered = true
		Log.severe("PATCH", "Failed to retrieve crown IDs for header.");
	}

	private int retrieveID(String key, JSONObject ids) {
		int cachedID = ((Long) ids.get(key + "ID")).intValue();
		return cachedID;
	}

	private void retrieveCrownChunk(ClassRecord record) {
		JSONObject chunk = new JSONObject();
		chunk.put("class", record.getClassChunk());
		chunk.put("subject", record.getSubjectChunk());
		chunk.put("teacher", record.getTeacherChunk());
		crownTree.put("chunk", chunk);
	}

	private void retrieveCrownInput(ClassRecord record) {
		JSONObject input = new JSONObject();
		input.put("class", Utils.namify(record.gradeSection));
		input.put("subject", record.subject);
		input.put("teacher", Utils.namify(record.teacher));
		input.put("track", record.track);
		input.put("semester", Utils.namify(record.semester));
		crownTree.put("input", input);
	}

	public ArrayList<String> getRegistryStudentNames() {
		return registryStudentNames;
	}

	public boolean hasRegistry() {
		return registryStudentNames.size() > 0;
	}
}
