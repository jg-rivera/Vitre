package local.vitre.desktop.record.patch.file;

import org.json.simple.JSONObject;

import local.vitre.desktop.http.Cypher;
import local.vitre.desktop.record.Student;
import local.vitre.desktop.record.data.MergedDataList;

/**
 * Serializes a classroom into a patch file.
 * 
 * @author Gab
 *
 */
@SuppressWarnings("unchecked")
public class ClassPatch extends PatchFile {

	public ClassPatch(String name) {
		super(name);
	}

	public void storeToStudentTree(int classID, String classSig, String classChunk, String className, MergedDataList<Student> students) {
		JSONObject studentTree = new JSONObject();

		JSONObject crownTree = new JSONObject();
		crownTree.put("id", classID);
		crownTree.put("sig", classSig);
		crownTree.put("name", className);
		crownTree.put("chunk", classChunk);
		crownTree.put("count", students.size());

		json.put("crown", crownTree);

		for (Student student : students) {
			JSONObject studentBranch = new JSONObject();
			int index = student.getIndex();
			studentBranch.put("fn", student.getFirstName());
			studentBranch.put("ln", student.getLastName());
			studentBranch.put("gender", "" + student.getGender().getRepresentation());
			studentBranch.put("username", student.getUsername());
			studentBranch.put("userkey", Cypher.sha256(student.getPassword()));
			studentBranch.put("sig", student.getSignature());
			studentTree.put(index, studentBranch);
		}

		json.put("data", studentTree);
	}
}
