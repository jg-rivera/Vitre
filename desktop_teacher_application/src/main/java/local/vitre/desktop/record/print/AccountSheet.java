package local.vitre.desktop.record.print;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import local.vitre.desktop.Log;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.record.Student;
import local.vitre.desktop.record.data.MergedDataList;
import local.vitre.desktop.util.Utils;

public class AccountSheet {

	private MergedDataList<Student> students;
	private XWPFDocument document;
	private XWPFTable table;
	private String sectionName;

	public AccountSheet(MergedDataList<Student> students, String sectionName) throws IOException {
		this.students = students;
		File template = new File(Vitre.assets.getEntryValue("ACCOUNT_TEMPLATE_DOC"));
		FileInputStream is = new FileInputStream(template);
		this.sectionName = sectionName;
		document = new XWPFDocument(is);
		table = document.getTables().get(0);
		create();
	}

	private void create() {
		int studentIndex = 0;

		for (int r = 0; r < 17; r++) {
			for (int c = 0; c < 4; c++) {
				if (studentIndex >= students.size())
					break;
				Student s = students.get(studentIndex);
				XWPFTableRow row = table.getRow(r);
				XWPFTableCell cell = row.getCell(c);
				cell.setText("Vitre Account Credentials");

				XWPFParagraph par = cell.addParagraph();
				par.setAlignment(ParagraphAlignment.CENTER);
				par.setVerticalAlignment(TextAlignment.CENTER);

				XWPFRun run = par.createRun();
				run.setText(s.getLastName() + ", " + s.getInitials());

				run.setBold(true);

				run.addBreak();

				XWPFRun run1 = par.createRun();
				run1.setText("user: " + s.getUsername());
				run1.setItalic(true);

				run1.addBreak();

				XWPFRun run2 = par.createRun();
				run2.setText("pass: " + s.getPassword());
				run2.setItalic(true);

				studentIndex++;
			}
		}
	}

	public void write(String outputFileName) throws IOException {
		Log.fine("ACC_SHEET", "Writing account sheet of " + sectionName + " to: " + outputFileName);
		FileOutputStream os = new FileOutputStream(outputFileName);
		document.write(os);
		os.close();
	}
}
