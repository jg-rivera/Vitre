package local.vitre.desktop.record;

import java.util.ArrayList;

import local.vitre.desktop.Log;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.record.data.Flushable;
import local.vitre.desktop.record.data.MergedDataList;
import local.vitre.desktop.record.data.RecordConfiguration;
import local.vitre.desktop.record.patch.ContextManager;
import local.vitre.desktop.record.patch.Patcher;
import local.vitre.desktop.ui.SignaturePopOver;
import local.vitre.desktop.util.Utils;

/**
 * Object representation of an Excel class record sheet.
 * 
 * @author Gab
 *
 */
public class ClassRecord implements Flushable {

	private RecordConfiguration config;

	// Name fields
	public String teacher;
	public String gradeSection;
	public String subject;
	public String track;
	public String schoolYear;
	public String semester;

	// Chunk fields
	protected String teacherChunk;
	protected String classChunk;
	protected String subjectChunk;

	// Signature fields
	protected String patchSignature;
	protected String teacherSignature;
	protected String classSignature;
	protected String subjectSignature;

	// Components
	public GradingComponent writtenWork;
	public GradingComponent performanceTask;
	public GradingComponent quarterlyAssessment;
	public GradingComponent all;
	public TrackType trackType;

	private Patcher patcher;
	private ContextManager contextMan;

	protected ArrayList<Student> maleStudents;
	protected ArrayList<Student> femaleStudents;
	protected MergedDataList<Student> students;

	private RecordParser parser;

	private String name;

	private String contextFileName;

	private SignaturePopOver pop;

	private int currentQuarter;

	protected int classID;
	protected int subjectID;
	protected int teacherID;
	protected boolean isAuth;

	public ClassRecord(String name, String contextFileName, RecordConfiguration config, int quarter) {
		this.name = name;
		this.currentQuarter = quarter;
		this.contextFileName = contextFileName;
		this.config = config;

		Log.info("RECORD", "Created new class record object: [" + name + "]");
		parser = new RecordParser(this);
		patcher = new Patcher(this);
	}

	public void parse() {
		int quarter = getQuarterSheetIndex(currentQuarter);
		Log.fine("RECORD",
				"Parsing ClassRecord object with configuration '" + config.getName() + "' Quarter=" + quarter);

		flush(); // Empties the class record object.
		parser.parseInputSheet();
		parser.parseTrack();
		parser.parseStudents();
		parser.parseComponents(quarter);

		// Initialize the context manager
		contextMan = new ContextManager(this, contextFileName);
		contextMan.read();
	}

	@Override
	public void flush() {
		students = null;
		maleStudents = null;
		femaleStudents = null;
		writtenWork = null;
		performanceTask = null;
		quarterlyAssessment = null;
		all = null;
		pop = null;
	}

	public void updateSignatureUI(String patchSignature) {
		this.patchSignature = patchSignature;

		ClassRecordDescriptor descriptor = createDescriptor();
		pop.attachDescriptor(descriptor);

		Vitre.controller.buildAuthData(pop);
	}

	public void setNetworkIDs(int classID, int teacherID, int subjectID) {
		this.classID = classID;
		this.teacherID = teacherID;
		this.subjectID = subjectID;
		isAuth = true;
	}

	public void colorizeAuthData(boolean hasClass, boolean hasSubject, boolean hasTeacher) {
		pop = new SignaturePopOver();
		pop.idValueFill(hasClass, hasSubject, hasTeacher);
		pop.sigValueFill(hasClass, hasSubject, hasTeacher);
	}

	protected ClassRecordDescriptor createDescriptor() {
		return new ClassRecordDescriptor(this);
	}

	public int getClassNetworkID() {
		if (!isAuth)
			throw new IllegalStateException("Tried to retrieve network ID from an inauthentic record.");
		return classID;
	}

	public int getTeacherNetworkID() {
		if (!isAuth)
			throw new IllegalStateException("Tried to retrieve network ID from an inauthentic record.");
		return teacherID;
	}

	public int getSubjectNetworkID() {
		if (!isAuth)
			throw new IllegalStateException("Tried to retrieve network ID from an inauthentic record.");
		return subjectID;
	}

	public Patcher getPatcher() {
		return patcher;
	}

	public boolean isAuthenticated() {
		return isAuth;
	}

	public String getPatchSignature() {
		return patchSignature;
	}

	public int getQuarterSheetIndex(int quarter) {
		return config.getData("sheetQuarter" + quarter).getIntegerValue();
	}

	public int getCurrentQuarter() {
		return currentQuarter;
	}

	public ContextManager getContextManager() {
		return contextMan;
	}

	public String getClassChunk() {
		return classChunk;
	}

	public String getClassSignature() {
		return classSignature;
	}

	public String getSubjectChunk() {
		return subjectChunk;
	}

	public String getSubjectSignature() {
		return subjectSignature;
	}

	public String getTeacherChunk() {
		return teacherChunk;
	}

	public String getTeacherSignature() {
		return teacherSignature;
	}

	public int getMaleStudentsCount() {
		return maleStudents.size();
	}

	public int getFemaleStudentsCount() {
		return femaleStudents.size();
	}

	public int getStudentsCount() {
		return getMaleStudentsCount() + getFemaleStudentsCount();
	}

	/**
	 * Is summative grade columns computable?
	 * 
	 * @return
	 */
	public boolean isSummative() {
		return parser.isSummative();
	}

	public boolean isSynchronized() {
		return patcher.isSynchronized();
	}

	public MergedDataList<Student> getStudents() {
		return students;
	}

	public ArrayList<String> getStudentNames() {
		ArrayList<String> temp = new ArrayList<>();
		for (Student s : students)
			temp.add(Utils.namify(s.toString()));
		return temp;
	}

	public int getAllItemCount() {
		return writtenWork.getItemCount() + performanceTask.getItemCount() + quarterlyAssessment.getItemCount();
	}

	public String getName() {
		return name;
	}

	public RecordConfiguration getConfig() {
		return config;
	}
}
