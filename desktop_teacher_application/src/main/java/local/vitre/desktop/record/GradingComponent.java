package local.vitre.desktop.record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.CellReference;
import org.json.simple.JSONArray;

import local.vitre.desktop.Log;
import local.vitre.desktop.record.data.LoadedDataPoint;
import local.vitre.desktop.record.data.MergedDataList;
import local.vitre.desktop.record.data.RecordConfiguration;
import local.vitre.desktop.record.patch.file.GradePatch;
import local.vitre.desktop.record.patch.file.PatchFile;
import local.vitre.desktop.ui.swing.GradeTableModel;
import local.vitre.desktop.util.Utils;

public class GradingComponent {

	/**
	 * Grades map (fetch index, grade data)
	 */
	private HashMap<Integer, LoadedDataPoint> grades;

	/**
	 * Collative grade data per column in a grading component
	 */
	private ArrayList<MergedDataList<Object>> mergedGrades;

	/**
	 * 
	 */
	private HashMap<Integer, ArrayList<Double>> elo;
	/**
	 * 
	 */
	private ArrayList<MergedDataList<Double>> mergedElo;

	/**
	 * Starting reference row for male students
	 */
	private int rowMale;
	/**
	 * Starting reference row for female students
	 */
	private int rowFemale;

	private String configName;

	private String[] columnNames;
	private String[] fullColumnNames;
	private int columnNameIndex;

	private int[] bounds;

	private ArrayList<Integer> occupiedItemColumns;
	private int maxItemCount;
	private int scoreColumnCount;

	private boolean noScoreColumns;
	private boolean empty;
	private List<Object> caps;

	private GradeTableModel model;
	private RecordConfiguration config;
	private ClassRecord record;
	private GradePatch patch;

	private int sheetIndex;
	private int rowPopLimit;
	private int scoreColumnStart;
	private int railLength;

	public String representativeName;

	private ComponentType type;

	public GradingComponent(ComponentType type, ClassRecord record, int sheetIndex) {
		this.type = type;
		this.record = record;
		this.sheetIndex = sheetIndex;
		this.configName = type.getTag();
		this.grades = new HashMap<>();
		this.elo = new HashMap<>();
		this.config = record.getConfig();
		this.caps = new ArrayList<>();
		this.mergedGrades = new ArrayList<>();
		this.mergedElo = new ArrayList<>();
		this.bounds = new int[2];

		if (type != ComponentType.ALL) {
			representativeName = Utils.keepTextOnlyNamify((String) config.getDataValue(configName)).trim();
			if (representativeName.endsWith("s"))
				representativeName = representativeName.substring(0, representativeName.length() - 1);
		} else
			representativeName = "All";

		parse();
	}

	private void parse() {
		Log.info("RECORD", "Parsing Class Grade Component: " + representativeName + "...");

		String inputCol = (String) config.getDataValue("col" + StringUtils.capitalize(configName));
		String[] raw = inputCol.split(">");

		// Calculating bounds
		for (int i = 0; i < bounds.length; i++)
			bounds[i] = CellReference.convertColStringToIndex(raw[i]);

		// Calculating rail length
		railLength = (bounds[1] - bounds[0]) + 1;

		columnNames = new String[railLength];
		fullColumnNames = new String[railLength];

		Log.fine("RECORD", "Rail length: " + railLength);

		rowMale = config.getData("rowMale").getIntegerValue();
		rowFemale = config.getData("rowFemale").getIntegerValue();
		rowPopLimit = config.getData("rowAtleastBeforeBlank").getIntegerValue();
		scoreColumnCount = config.getData("scoreColCount" + StringUtils.capitalize(configName)).getIntegerValue();

		if (scoreColumnCount <= 0) {
			noScoreColumns = true;
			Log.warn("Component '" + configName + "' has no score columns!");
		}

		// 13 - 3 = 10
		// 0 1 2 3 4 5 6 7 8 9 (10 11 12)
		maxItemCount = railLength - scoreColumnCount;
		scoreColumnStart = maxItemCount;

		// Begin excel parsing

		if (!noScoreColumns) {
			// Calculating highest possible score caps
			LoadedDataPoint p = config.newData("caps", "F11]AH", sheetIndex, true);

			int offset = config.getData("colOffset").getIntegerValue();
			caps = p.getValues().subList(bounds[0] - offset, bounds[1] - (offset - 1));

			for (int i = 0; i < caps.size(); i++)
				if (caps.get(i).equals(PatchFile.MISSING_DATA_VALUE))
					caps.set(i, PatchFile.NIL_DATA_VALUE);

			Double d = (Double) caps.get(caps.size() - 1);
			caps.set(caps.size() - 1, d * 100);
		}

		ArrayList<Integer> maleData = readData(rowMale, record.getMaleStudentsCount());
		ArrayList<Integer> femaleData = readData(rowFemale, record.getFemaleStudentsCount());

		if (!maleData.equals(femaleData))
			Log.warn("Discrepancy found between male and female student list. Retained only common items.");

		maleData.retainAll(femaleData);
		occupiedItemColumns = new ArrayList<>();
		occupiedItemColumns.addAll(maleData);
		if (occupiedItemColumns.isEmpty())
			empty = true;
		Log.info("RECORD", "Occupied Columns -> " + occupiedItemColumns);
		Log.info("RECORD", "Total items for this component: " + getItemCount() + " / " + maxItemCount);

		mergeGrade();
		mergeElo();

		if (!noScoreColumns)
			Log.info("ELO",
					"Highest elo for this component: " + Elo.highestPossibleElo(this) + " / " + Elo.maxElo(this));

		Log.info("RECORD", "Successfully parsed '" + representativeName + "' grade component! \n");
	}

	/**
	 * 
	 * @return integer array list of currently occupied columns
	 */
	private ArrayList<Integer> readData(int row, int studentsCount) {
		if (row == rowMale)
			Log.fine("RECORD", "Processing MALE students grade data...");
		else
			Log.fine("RECORD", "Processing FEMALE students grade data...");

		int first = bounds[0];
		int last = bounds[1];
		int dist = last - first;

		ArrayList<Integer> items = new ArrayList<>();

		for (int column = first; column <= last; column++) {
			int itemNumber = column - bounds[0] + 1;
			String dataTag = configName + "Item" + itemNumber;
			String fullTag = representativeName + " " + itemNumber;
			String shortTag = "" + itemNumber;

			if (itemNumber >= dist - 1) {
				int i = itemNumber - dist;
				if (i == -1) {
					if (!configName.equalsIgnoreCase("quarterlyAssessment")) {
						dataTag = configName + "SumRaw";
						fullTag = "Raw Sum";
						shortTag = "Total";
					}
				} else if (i == 0) {
					dataTag = configName + "SumPercentage";
					fullTag = "Percentage Sum";
					shortTag = "PS";
				} else if (i == 1) {
					dataTag = configName + "SumWeighted";
					fullTag = "Weighted Sum";
					shortTag = "WS";
				}
			}

			// Store as column name for table view
			storeName(fullTag, shortTag);

			String coordinate = CellReference.convertNumToColString(column) + row + "[" + (row + studentsCount - 1);
			LoadedDataPoint data = config.newData(dataTag, coordinate, sheetIndex, true);

			boolean blankColumn = false;

			if (data.hasEmptyValues()) {
				int occupied = studentsCount - data.getEmptyCount();

				if (occupied < rowPopLimit) {
					blankColumn = true;
					data.setAll(PatchFile.NIL_DATA_VALUE);
				}
				// Column is functional yet has missing nodes
				else {
					Log.fine("RECORD", "Found only " + (studentsCount - data.getEmptyCount()) + " occupied cell(s) in '"
							+ dataTag + "' @ " + coordinate);
				}
			}

			if (!blankColumn) {
				// If an i
				if (shortTag.matches("-?\\d+"))
					elo.put(elo.size() + 1, getElo(itemNumber, data.getValues()));
				if (itemNumber <= maxItemCount)
					items.add(itemNumber);
			}

			grades.put(grades.size() + 1, data);
		}

		return items;
	}

	private ArrayList<Double> getElo(int itemNumber, ArrayList<Object> data) {
		ArrayList<Double> eloArray = new ArrayList<>();
		for (int i = 0; i < data.size(); i++) {
			Object o = data.get(i);
			if (o instanceof Number) {
				Number score = (Number) o;
				Number cap = (Number) getScoreCap(itemNumber - 1);
				Number weight = getWeight();

				Double elo = Elo.calculate(score.doubleValue(), cap.doubleValue(), weight.doubleValue() / 100);
				eloArray.add(elo);
			}
		}
		return eloArray;
	}

	private void storeName(String fullTag, String shortTag) {
		if (columnNameIndex + 1 <= railLength) {
			fullColumnNames[columnNameIndex] = fullTag;
			columnNames[columnNameIndex] = shortTag;
			columnNameIndex++;
		}
	}

	private void mergeGrade() {
		Log.fine("RECORD", "Merging grade data lists...");

		for (int i = 1; i <= grades.size(); i++) {

			// Prevent double merging of data
			if (i > railLength)
				break;

			LoadedDataPoint dataTarget = grades.get(i);
			MergedDataList<Object> mList = dataTarget.merge(grades.get(railLength + i));
			mList.setName(dataTarget.getTag());
			mList.setId(i);

			Log.fine("RECORD", "G" + mList.getId() + "(N" + mList.size() + ") = " + mList.toString());
			mergedGrades.add(mList);
		}

		Log.fine("RECORD", "Grade data lists merged.");
		grades.clear();
	}

	private void mergeElo() {
		for (int i = 1; i <= elo.size(); i++) {
			int shift = elo.size() / 2;
			if (i > shift)
				break;

			ArrayList<Double> source = elo.get(i);
			ArrayList<Double> target = elo.get(shift + i);
			MergedDataList<Double> mList = new MergedDataList<>(source, target);
			mList.setId(i);
			Log.fine("RECORD", "E" + mList.getId() + "(N" + mList.size() + ") = " + mList.toString());
			mergedElo.add(mList);
		}
		Log.fine("RECORD", "Elo data lists merged.");
		elo.clear();

	}

	@SuppressWarnings("unchecked")
	public void createContent(GradePatch patch) {
		Log.info("RECORD", "Patching " + configName + " grading component into JSON...");

		if (empty) {
			Log.warn("Unable to create JSON patch for empty '" + configName + "'.");
			return;
		}

		// Exclude HPS processing for ALL component
		if (type != ComponentType.ALL) {
			// Store HPS array once
			int[] occupied = occupiedItemColumns.stream().mapToInt(i -> i).toArray();
			int max = Arrays.stream(occupied).max().getAsInt();
			patch.setMaxItemCapacity(max);

			JSONArray itemCaps = new JSONArray();
			itemCaps.addAll(caps.subList(0, max));
			patch.storeCaps(type, "itemCaps", itemCaps);

			JSONArray aggregateCaps = new JSONArray();
			aggregateCaps.addAll(caps.subList(maxItemCount, railLength));
			patch.storeCaps(type, "aggregateCaps", aggregateCaps);
		}

		// Sew grades to respective students...
		for (int mIndex = 0; mIndex < mergedGrades.size(); mIndex++) {
			for (int sIndex = 0; sIndex < record.getStudentsCount(); sIndex++) {

				MergedDataList<Object> mList = mergedGrades.get(mIndex);
				Object data = mList.get(sIndex);

				if (type != ComponentType.ALL)
					handleComponents(patch, mIndex, sIndex, mList, data);
				else {
					if (record.isSummative())
						patch.storeAsAll(sIndex + 1, data);
				}
			}
		}

		patch.getDataTree().put(type.getTag(), patch.getComponentBranch(type));

		// mergedGrades.clear(); TODO Check drawbacks.

		Log.info("RECORD", "Patched " + configName + " to JSON.");
	}

	public void handleComponents(GradePatch patch, int mIndex, int sIndex, MergedDataList<Object> mList, Object data) {
		// if currently at item indices
		if (mIndex < maxItemCount) {
			if (occupiedItemColumns.contains(mList.getId())) {
				Double eloData = mergedElo.get(mIndex).get(sIndex);
				patch.storeItemCount(type, getItemCount());
				patch.storeAsItem(type, mList.getId() - 1, sIndex + 1, data, eloData);
			}
		}
		// now at aggregate columns
		else {
			patch.storeAsAggregate(type, sIndex + 1, data);
		}
	}

	public void setModel(GradeTableModel model) {
		this.model = model;
	}

	public boolean isEmpty() {
		return empty;
	}

	public boolean hasModel() {
		return model != null;
	}

	public int getPercentage() {
		return Integer.parseInt(configName.replaceAll("[^0-9]", ""));
	}

	public String getName() {
		return configName;
	}

	public int getIndex() {
		return type.ordinal();
	}

	public int getMaxItemCount() {
		return maxItemCount;
	}

	public int getItemCount() {
		return occupiedItemColumns.size();
	}

	public ArrayList<Integer> getItemColumns() {
		return occupiedItemColumns;
	}

	public Number getWeight() {
		return (Number) caps.get(railLength - 1);
	}

	public Object getScoreCap(int index) {
		return caps.get(index);
	}

	public List<Object> getScoreCaps() {
		return caps;
	}

	public ClassRecord getRecord() {
		return record;
	}

	public GradePatch getPatch() {
		return patch;
	}

	public ComponentType getType() {
		return type;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public String[] getFullColumnNames() {
		return fullColumnNames;
	}

	public int getRailLength() {
		return railLength;
	}

	public int getScoreColumnStart() {
		return scoreColumnStart;
	}

	public ArrayList<MergedDataList<Object>> getMergedLists() {
		return mergedGrades;
	}
}