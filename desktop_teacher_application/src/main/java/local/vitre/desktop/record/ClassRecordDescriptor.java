package local.vitre.desktop.record;

import local.vitre.desktop.util.Utils;

public class ClassRecordDescriptor {

	public String teacherChunk;
	public String classChunk;
	public String subjectChunk;

	public String patchSignature;
	public String teacherSignature;
	public String classSignature;
	public String subjectSignature;

	public int classID;
	public int subjectID;
	public int teacherID;

	public String document;
	public String teacher;
	public String gradeSection;
	public String subject;
	public String track;
	public String schoolYear;
	public String semester;

	public ClassRecordDescriptor(ClassRecord record) {
		document = record.getName();
		teacherChunk = record.teacherChunk;
		classChunk = record.classChunk;
		subjectChunk = record.subjectChunk;
		patchSignature = record.patchSignature;
		teacherSignature = record.teacherSignature;
		classSignature = record.classSignature;
		subjectSignature = record.subjectSignature;
		classID = record.classID;
		subjectID = record.subjectID;
		teacherID = record.teacherID;
		teacher = Utils.namify(record.teacher);
		gradeSection = Utils.namify(record.gradeSection);
		subject = Utils.namify(record.subject);
		track = record.track;
		schoolYear = record.schoolYear;
		semester = record.semester;
	}
}
