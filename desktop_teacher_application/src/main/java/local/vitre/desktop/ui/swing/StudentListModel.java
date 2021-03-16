package local.vitre.desktop.ui.swing;

import javax.swing.AbstractListModel;

import local.vitre.desktop.record.Student;
import local.vitre.desktop.record.data.MergedDataList;

public class StudentListModel extends AbstractListModel<Student> {

	private MergedDataList<Student> students;

	public StudentListModel(MergedDataList<Student> students) {
		this.students = students;
	}

	@Override
	public Student getElementAt(int i) {
		return students.get(i);
	}

	@Override
	public int getSize() {
		return students.size();
	}

}
