package local.vitre.desktop.ui.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import local.vitre.desktop.Log;
import local.vitre.desktop.record.ComponentType;
import local.vitre.desktop.record.GradingComponent;
import local.vitre.desktop.record.Student;
import local.vitre.desktop.record.data.MergedDataList;
import local.vitre.desktop.ui.CellMetadata;
import local.vitre.desktop.ui.CellMetadata.DataType;
import local.vitre.desktop.util.Utils;

public class GradeTableModel extends AbstractTableModel {

	private GradingComponent component;

	private String[] columnNames;
	private String[] fullColumnNames;

	private Object[][] data;
	private CellMetadata[][] metadata;
	private CellMetadata[] headerMetadata;

	private ArrayList<MergedDataList<Object>> mergedLists;
	private ArrayList<Integer> itemColumns;

	private MergedDataList<Student> students;
	private String[] studentNames;

	private int[] studentIndices;
	private int studentOffset;
	private int railLength;
	private int maxItemCount;

	private List<Object> caps;
	private List<Integer> occupiedColumns;

	public GradeTableModel(GradingComponent component) {
		this.component = component;
		this.mergedLists = component.getMergedLists();
		this.itemColumns = component.getItemColumns();
		this.caps = component.getScoreCaps();
		this.railLength = component.getRailLength();
		this.students = component.getRecord().getStudents();
		this.studentOffset = component.getRecord().getMaleStudentsCount();
		this.maxItemCount = component.getMaxItemCount();
		this.occupiedColumns = component.getItemColumns();

		prepare();
		Log.fine("Item columns rendered for " + component.getName() + " -> " + itemColumns.toString());
		Log.fine("TABLE", "Acquired merged list: col. size = " + mergedLists.size());
		Log.fine("TABLE", "Prepared data array: (" + data.length + ", " + data[0].length + ")");

		generate();
		component.setModel(this);
	}

	private void prepare() {
		int row = students.size();
		int col = railLength;
		col += 2; // student index, students offset=2
		row += 1; // HPS offset=1

		data = new Object[row][col];
		metadata = new CellMetadata[row][col];
		headerMetadata = new CellMetadata[col];

		studentNames = new String[students.size()];
		for (int i = 0; i < students.size(); i++) {
			Student student = students.get(i);
			studentNames[i] = " " + student.getLastName().toUpperCase() + ", " + student.getInitials();
		}

		columnNames = new String[col];
		columnNames[0] = "N";
		columnNames[1] = "Students";

		for (int i = 2; i < columnNames.length; i++)
			columnNames[i] = component.getColumnNames()[i - 2];

		fullColumnNames = new String[col];
		fullColumnNames[0] = "..."; // N
		fullColumnNames[1] = "..."; // Students

		for (int i = 2; i < fullColumnNames.length; i++)
			fullColumnNames[i] = component.getFullColumnNames()[i - 2];

		studentIndices = new int[students.size()];
		for (int i = 0; i < studentIndices.length; i++)
			studentIndices[i] = students.get(i).getIndex();
	}

	private void generate() {
		data[0][0] = "";
		data[0][1] = "H.P. Score";

		// Highest possible scores population
		for (int i = 2; i < data[0].length; i++) { // first row
			Object o = caps.get(i - 2);
			if (o instanceof Double && i <= maxItemCount + 1) {
				data[0][i] = Utils.noZeros(o.toString());
				continue;
			}
			data[0][i] = o;
		}

		for (int i = 1; i < data.length; i++) {
			data[i][0] = studentIndices[i - 1];
			data[i][1] = studentNames[i - 1];
		}

		// Grade data population
		for (int i = 1; i < data.length; i++) { // row 38
			for (int j = 2; j < data[i].length; j++) { // col 15
				MergedDataList<Object> array = mergedLists.get(j - 2);
				Object o = array.get(i - 1);

				if (component.getType() != ComponentType.QUARTERLY_ASSESSMENT) {
					if (o instanceof Double && j <= maxItemCount + 1) {
						data[i][j] = Utils.noZeros(o.toString());
						continue;
					}
				} else {
					if (o instanceof Double && j == maxItemCount + 1) {
						data[i][j] = Utils.noZeros(o.toString());
						continue;
					}
				}
				data[i][j] = o;
			}
		}

		Log.fine("TABLE", "Data size of table view: " + data.length);

		updateMetadata();
		updateHeaderMetadata();

		Log.info("TABLE", "Generated TableView for " + component.getName());

	}

	private void updateMetadata() {
		for (int row = 0; row < data.length; row++) { // row
			for (int col = 0; col < data[row].length; col++) { // col
				CellMetadata meta = new CellMetadata(row, col, data[row][col]);

				if (row == 0) {
					meta.setBackgroundColor(CellMetadata.BLANK_COLOR);
				}

				if (col == 0 && row > 0) {
					meta.setBackgroundColor(CellMetadata.male);
					if (row > studentOffset)
						meta.setBackgroundColor(CellMetadata.female);
				}

				data[row][col] = meta.getData();
				metadata[row][col] = meta;
			}
		}
	}

	private void updateHeaderMetadata() {
		for (int col = 0; col < headerMetadata.length; col++) {
			CellMetadata metadata = new CellMetadata(col);
			metadata.setType(DataType.BLANK);

			if (col == 0)
				metadata.setBackgroundColor(CellMetadata.numberIndex);

			headerMetadata[col] = metadata;
		}
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	public CellMetadata getMetadataAt(int row, int col) {
		return metadata[row][col];
	}

	public CellMetadata getHeaderMetadataAt(int col) {
		return headerMetadata[col];
	}

	public void setMetadata(CellMetadata newMeta) {
		int row = newMeta.row;
		int column = newMeta.col;
		metadata[row][column] = newMeta;
		fireTableCellUpdated(row, column);
	}

	public void setMetadataAt(CellMetadata value, int row, int column) {
		metadata[row][column] = value;
		fireTableCellUpdated(row, column);
	}

	public void setHeaderMetadata(JTable table, CellMetadata newMeta) {
		int column = newMeta.col;
		headerMetadata[column] = newMeta;
		table.getTableHeader().repaint();
	}

	public CellMetadata getHeaderMetadata(int col) {
		return headerMetadata[col];
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public String getFullColumnName(int col) {
		return fullColumnNames[col];
	}

	public boolean isItemColumnOccupied(int col) {
		int offset = col - 1;
		return occupiedColumns.contains(offset);
	}

	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	public String getStudentOwner(int row, int col) {
		if (row >= 1 && col >= 2)
			return String.valueOf(data[row][1]);
		else
			return "-";
	}

	public int getRailLength() {
		return railLength;
	}

	public double getCap(int col) {
		Number n = (Number) caps.get(col - 2);
		return n.doubleValue();
	}

	public int getOccupiedRowCount(int col) {
		int count = data.length;
		for (int i = 0; i < data.length; i++) {
			Object o = data[i][col];
			if (o.equals("MISS"))
				count--;
		}
		return count - 1;
	}

	public static int getItemIndex(int col) {
		return getItemColumnIndexFor(col) - 1;
	}

	public int getNumberFromColumn(int col) {
		return Integer.parseInt(columnNames[col].replaceAll("[^\\d-]", ""));
	}

	public static int getItemColumnIndexFor(int col) {
		if (col >= 2) {
			return col - 1;
		}
		return -1;
	}

	public static boolean isItemColumn(int col) {
		return col >= 2;
	}

	public int getComponentIndex() {
		return component.getIndex();
	}

	public int getMaxItemCount() {
		return maxItemCount;
	}

	public GradingComponent getComponent() {
		return component;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void setValueAt(Object value, int row, int column) {
		data[row][column] = value;
		fireTableCellUpdated(row, column);
	}

}
