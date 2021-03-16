package local.vitre.desktop.record.patch.diff;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import local.vitre.desktop.record.ClassRecord;
import local.vitre.desktop.record.Student;
import local.vitre.desktop.record.data.MergedDataList;

public class RegistryDifference {

	ArrayList<String> localRegistry;
	ArrayList<String> serverRegistry;

	public RegistryDifference() {
		this.localRegistry = new ArrayList<>();
		this.serverRegistry = new ArrayList<>();
	}

	public void attachLocal(ClassRecord record) {
		MergedDataList<Student> students = record.getStudents();

		for (Student s : students) {
			localRegistry.add(s.getSignature());
		}

		StringUtils.join(localRegistry, ';');
	}
}
