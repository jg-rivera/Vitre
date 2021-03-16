package local.vitre.desktop.record.patch.file;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import local.vitre.desktop.record.ClassRecord;
import local.vitre.desktop.record.ComponentType;
import local.vitre.desktop.record.patch.PatchCrown;

@SuppressWarnings("unchecked")
public class GradePatch extends PatchFile {

	private PatchCrown crown;

	private JSONObject dataTree;
	private JSONObject metadataTree;

	private JSONObject all;
	private JSONObject writtenWork;
	private JSONObject performanceTask;
	private JSONObject quarterlyAssessment;

	private int maxItems;

	private int classID;
	private int subjectID;
	private int teacherID;

	public GradePatch() {
		super("temp");

		dataTree = new JSONObject();
		metadataTree = new JSONObject();

		// Component Branches
		all = new JSONObject();
		writtenWork = new JSONObject();
		performanceTask = new JSONObject();
		quarterlyAssessment = new JSONObject();
	}

	public void make() {
		/*
		 * dataTree.put("writtenWork", writtenWork);
		 * dataTree.put("performanceTask", performanceTask);
		 * dataTree.put("quarterlyAssessment", quarterlyAssessment);
		 * dataTree.put("all", all);
		 */
	}

	public void attachCrown(PatchCrown crown) {
		crown.attach(json);
		this.crown = crown;
	}

	/**
	 * Metadata tree creation.
	 * 
	 * @param section
	 * @param tableVector
	 */
	public void attachData(ClassRecord record) {
		// TODO Last mod timestamp

		// Attach contexts
		metadataTree.put("context", record.getContextManager().allPatchContextToJSON());
		json.put("metadata", metadataTree); // Metadata tree

		record.writtenWork.createContent(this);
		record.performanceTask.createContent(this);
		record.quarterlyAssessment.createContent(this);
		record.all.createContent(this);
		json.put("data", dataTree); // Data tree
	}

	public void storeCaps(ComponentType type, Object key, Object data) {
		getComponentBranch(type).put(key, data);
	}

	public void storeAsItem(ComponentType type, int itemIndex, int studentIndex, Object gradeData, Double eloData) {
		storeTask(getComponentBranch(type), itemIndex, studentIndex, gradeData, eloData);
	}

	public void storeItemCount(ComponentType type, int count) {
		storeCount(getComponentBranch(type), count);
	}

	public void storeAsAll(int studentIndex, Object gradeData) {
		if (!all.containsKey(studentIndex)) {
			JSONArray grades = new JSONArray();
			storeTaskAll(grades, gradeData);
			all.put(studentIndex, grades);
		} else {
			JSONArray grades = (JSONArray) all.get(studentIndex);
			storeTaskAll(grades, gradeData);
		}
	}

	private void storeTaskAll(JSONArray grades, Object gradeData) {
		if (gradeData instanceof Double) {
			Double d = (Double) gradeData;
			d = Math.floor(d);
			grades.add(d);
		} else
			grades.add(gradeData);
	}

	public void storeAsAggregate(ComponentType type, int studentIndex, Object gradeData) {
		JSONObject student = (JSONObject) getComponentBranch(type).get(studentIndex);
		JSONArray aggregate = (JSONArray) student.get("aggregate");

		if (aggregate.isEmpty() && type == ComponentType.QUARTERLY_ASSESSMENT) {
			JSONArray grades = (JSONArray) student.get("item");
			aggregate.add(grades.get(0));
		}

		aggregate.add(gradeData);
	}

	private void storeCount(JSONObject component, int count) {
		component.put("count", count);
	}

	private void storeTask(JSONObject component, int itemIndex, int studentIndex, Object gradeData, Double eloData) {
		if (!component.containsKey(studentIndex)) {
			// Create array and store first grade occurrence
			JSONObject student = new JSONObject();
			JSONArray grades = new JSONArray();
			JSONArray elo = new JSONArray();
			JSONArray aggregate = new JSONArray();

			elo.ensureCapacity(maxItems);
			grades.ensureCapacity(maxItems);

			for (int i = 0; i < maxItems; i++) {
				elo.add(PatchFile.NIL_DATA_VALUE);
				grades.add(PatchFile.NIL_DATA_VALUE);
			}

			elo.set(itemIndex, eloData);
			grades.set(itemIndex, gradeData);

			student.put("item", grades);
			student.put("elo", elo);
			student.put("aggregate", aggregate);

			component.put(studentIndex, student);
		} else {
			JSONObject student = (JSONObject) component.get(studentIndex);
			JSONArray grades = (JSONArray) student.get("item");
			JSONArray elo = (JSONArray) student.get("elo");

			elo.set(itemIndex, eloData);
			grades.set(itemIndex, gradeData);
		}
	}

	public JSONObject getComponentBranch(ComponentType type) {
		switch (type) {
		case WRITTEN_WORK:
			return writtenWork;
		case PERFORMANCE_TASK:
			return performanceTask;
		case QUARTERLY_ASSESSMENT:
			return quarterlyAssessment;
		case ALL:
			return all;
		default:
			return null;
		}
	}

	public boolean isSigned() {
		return crown.isSigned();
	}

	public PatchCrown getCrown() {
		return crown;
	}

	public JSONObject getDataTree() {
		return dataTree;
	}

	public void setMaxItemCapacity(int maxItems) {
		this.maxItems = maxItems;
	}

	public JSONObject getMetadataTree() {
		return metadataTree;
	}

	public JSONObject getWrittenWork() {
		return writtenWork;
	}

	public JSONObject getPerformanceTask() {
		return performanceTask;
	}

	public JSONObject getQuarterlyAssessment() {
		return quarterlyAssessment;
	}

	public int getContextsSize() {
		return ((JSONObject) metadataTree.get("context")).size();
	}

	public int getClassID() {
		return classID;
	}

	public int getSubjectID() {
		return subjectID;
	}

	public int getTeacherID() {
		return teacherID;
	}
}
