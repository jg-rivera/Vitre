package local.vitre.desktop.record.patch.queue;

import org.json.simple.JSONObject;

import local.vitre.desktop.record.ClassRecordDescriptor;
import local.vitre.desktop.record.patch.diff.PatchDifference;
import local.vitre.desktop.record.patch.file.PatchFile.PatchType;
import local.vitre.desktop.util.Utils;

public class PatchTracker {

	public int index;
	public int addOpCount, updateOpCount, removeOpCount;

	public PatchType type;
	public QueueState state;
	public long timestamp;

	public String document;
	public String gradeSection;
	public String subject;
	public String teacher;

	public String recordSignature;
	public String classSignature;
	public String subjectSignature;
	public String teacherSignature;

	public int classID;
	public int subjectID;
	public int teacherID;

	public PatchTracker(int index, PatchDifference diff, ClassRecordDescriptor desc) {
		this.index = index;
		this.state = QueueState.SUCCESS; //TODO states
		this.timestamp = Utils.time();

		// Retrieve operation counts
		this.addOpCount = diff.getAddOpCount();
		this.updateOpCount = diff.getUpdateOpCount();
		this.removeOpCount = diff.getRemoveOpCount();

		readFromDesc(desc);
	}

	public PatchTracker(int index, JSONObject json) {
		this.index = index;
		readFromJSON(json);
	}

	private void readFromDesc(ClassRecordDescriptor desc) {
		document = desc.document;
		gradeSection = desc.gradeSection;
		subject = desc.subject;
		teacher = desc.teacher;

		recordSignature = desc.patchSignature;
		classSignature = desc.classSignature;
		subjectSignature = desc.subjectSignature;
		teacherSignature = desc.teacherSignature;

		classID = desc.classID;
		subjectID = desc.subjectID;
		teacherID = desc.teacherID;
	}

	private void readFromJSON(JSONObject json) {
		timestamp = ((Number) json.get("timestamp")).longValue();

		int ord = ((Number) json.get("state")).intValue();
		state = QueueState.values()[ord];

		JSONObject names = (JSONObject) json.get("name");
		document = (String) names.get("document");
		gradeSection = (String) names.get("class");
		subject = (String) names.get("subject");
		teacher = (String) names.get("teacher");

		JSONObject signatures = (JSONObject) json.get("signature");
		recordSignature = (String) signatures.get("record");
		classSignature = (String) signatures.get("class");
		subjectSignature = (String) signatures.get("subject");
		teacherSignature = (String) signatures.get("teacher");

		JSONObject ids = (JSONObject) json.get("id");
		classID = ((Number) ids.get("class")).intValue();
		subjectID = ((Number) ids.get("subject")).intValue();
		teacherID = ((Number) ids.get("teacher")).intValue();

		JSONObject inst = (JSONObject) json.get("instructions");
		addOpCount = ((Number) inst.get("added")).intValue();
		removeOpCount = ((Number) inst.get("removed")).intValue();
		updateOpCount = ((Number) inst.get("updated")).intValue();
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("timestamp", timestamp);
		json.put("state", state.ordinal());

		// Names
		JSONObject names = new JSONObject();
		names.put("document", document);
		names.put("class", gradeSection);
		names.put("subject", subject);
		names.put("teacher", teacher);
		json.put("name", names);

		// Signatures
		JSONObject signatures = new JSONObject();
		signatures.put("record", recordSignature);
		signatures.put("class", classSignature);
		signatures.put("subject", subjectSignature);
		signatures.put("teacher", teacherSignature);
		json.put("signature", signatures);

		// IDs
		JSONObject ids = new JSONObject();
		ids.put("class", classID);
		ids.put("subject", subjectID);
		ids.put("teacher", teacherID);
		json.put("id", ids);

		// Instructions
		JSONObject inst = new JSONObject();
		inst.put("added", addOpCount);
		inst.put("updated", updateOpCount);
		inst.put("removed", removeOpCount);
		json.put("instructions", inst);

		return json;
	}

	public void setState(QueueState state) {
		this.state = state;
	}

	public QueueState getState() {
		return state;
	}

	public int getIndex() {
		return index;
	}

}
