package local.vitre.desktop.record;

import java.util.ArrayList;

import local.vitre.desktop.Log;
import local.vitre.desktop.http.Cypher;
import local.vitre.desktop.record.Student.Gender;
import local.vitre.desktop.record.data.MergedDataList;
import local.vitre.desktop.record.data.RecordConfiguration;
import local.vitre.desktop.util.Utils;

public class RecordParser {

	private ClassRecord record;

	public RecordParser(ClassRecord record) {
		this.record = record;
	}

	public void parseInputSheet() {
		Log.info("RECORD", "Parsing input data sheet...");

		RecordConfiguration config = record.getConfig();

		record.teacher = (String) config.getDataValue("teacher");
		record.gradeSection = (String) config.getDataValue("gradeSection");
		record.track = (String) config.getDataValue("track");
		record.subject = (String) config.getDataValue("subject");
		record.schoolYear = (String) config.getDataValue("schoolYear");
		record.semester = (String) config.getDataValue("semester");

		String chunkClass = Utils.chunkify(record.gradeSection);
		String chunkTeacher = Utils.chunkify(record.teacher);
		String chunkSubject = Utils.chunkify(record.subject);

		record.classChunk = chunkClass;
		record.teacherChunk = chunkTeacher;
		record.subjectChunk = chunkSubject;
		record.classSignature = Cypher.sha256(chunkClass);
		record.teacherSignature = Cypher.sha256(chunkTeacher);
		record.subjectSignature = Cypher.sha256(chunkSubject);

		Log.info("-- Class Record --");
		Log.info("Last modified: " + config.getTool().getLastModifiedDate());
		Log.info("Grade and Section: " + record.gradeSection + " (Hsh=" + record.classSignature + ")");
		Log.info("Track: " + record.track);
		Log.info("Teacher: " + record.teacher + " (Hsh=" + record.teacherSignature + ")");
		Log.info("Subject: " + record.subject + " (Hsh=" + record.subjectSignature + ")");
		Log.info("School Year: " + record.schoolYear);

		Log.newLine();
	}

	public void parseStudents() {
		Log.fine("RECORD", "Populating student list...");
		RecordConfiguration config = record.getConfig();

		record.maleStudents = new ArrayList<>();
		record.femaleStudents = new ArrayList<>();

		int index = 1;
		for (Object o : config.getDataValues("maleStudents")) {
			record.maleStudents.add(new Student(record, Gender.MALE, (String) o, index));
			index++;
		}
		index = 1;
		for (Object o : config.getDataValues("femaleStudents")) {
			record.femaleStudents.add(new Student(record, Gender.FEMALE, (String) o, index));
			index++;
		}
		Log.info("RECORD", record.getStudentsCount() + " students (" + record.getMaleStudentsCount() + " male & "
				+ record.getFemaleStudentsCount() + " female) populated into list.");

		record.students = new MergedDataList<>(record.maleStudents, record.femaleStudents);
	}

	public void parseComponents(int sheetIndex) {
		Log.fine("RECORD", "Parsing grading components...");

		record.writtenWork = new GradingComponent(ComponentType.WRITTEN_WORK, record, sheetIndex);
		record.performanceTask = new GradingComponent(ComponentType.PERFORMANCE_TASK, record, sheetIndex);
		record.quarterlyAssessment = new GradingComponent(ComponentType.QUARTERLY_ASSESSMENT, record, sheetIndex);
		record.all = new GradingComponent(ComponentType.ALL, record, sheetIndex);
	}

	public void parseTrack() {
		Log.fine("RECORD", "Parsing track type...");
		String track = record.track;
		TrackType type = TrackType.getMatch(track);
		if (type != null)
			record.trackType = type;
	}

	public boolean isSummative() {
		return !(record.writtenWork.isEmpty() || record.performanceTask.isEmpty()
				|| record.quarterlyAssessment.isEmpty());
	}

}
