package local.vitre.desktop.record.patch.diff;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;

import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import local.vitre.desktop.Log;
import local.vitre.desktop.record.ComponentType;
import local.vitre.desktop.record.data.MergedDataList;
import local.vitre.desktop.record.patch.ContextManager;
import local.vitre.desktop.record.patch.diff.PatchInstructionSet.PatchOperation;
import local.vitre.desktop.record.patch.diff.PatchSpecification.PatchSpecType;
import local.vitre.desktop.ui.CellMetadata;
import local.vitre.desktop.ui.CellMetadata.DataType;
import local.vitre.desktop.ui.CellUpdate;
import local.vitre.desktop.ui.UIHandler;
import local.vitre.desktop.util.Utils;

/**
 * Class which generates and determines the difference between two patch files.
 * 
 * @author Gab
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PatchDifference {

	private String oldPatchData;
	private String newPatchData;

	private LinkedHashMap oldPatchMap;
	private LinkedHashMap newPatchMap;

	private ArrayList<PatchSpecification> differenceModel;
	private ArrayList<PatchSpecification> similarityModel;

	private ArrayList<CellUpdate> headerUpdates;
	private ArrayList<CellUpdate> studentUpdates;

	private MergedDataList<PatchSpecification> fullDifferenceModel;

	private int addOpCount;
	private int updateOpCount;
	private int removeOpCount;

	private int studentCount;
	private boolean invalid;

	/**
	 * Creates a patch difference between two patch data JSONs.
	 * 
	 * @param studentCount
	 * @param oldPatchData
	 * @param newPatchData
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public PatchDifference(int studentCount, String oldPatchData, String newPatchData)
			throws FileNotFoundException, IOException, ParseException {

		Log.info("PATCH", "Generating patch difference from [ " + oldPatchData.getBytes().length + " bytes vs. "
				+ newPatchData.getBytes().length + " bytes ]");

		this.studentCount = studentCount;
		this.oldPatchData = oldPatchData;
		this.newPatchData = newPatchData;
		oldPatchMap = Utils.createOrderedJSONMap(oldPatchData);
		newPatchMap = Utils.createOrderedJSONMap(newPatchData);

		headerUpdates = new ArrayList<>();
		studentUpdates = new ArrayList<>();

		differenceModel = new ArrayList<>();
		similarityModel = new ArrayList<>();

		Log.fine("PATCH", "Modeling and comparing component branches...");

		diffComponentBranch(ComponentType.WRITTEN_WORK, oldPatchMap, newPatchMap);
		diffComponentBranch(ComponentType.PERFORMANCE_TASK, oldPatchMap, newPatchMap);
		diffComponentBranch(ComponentType.QUARTERLY_ASSESSMENT, oldPatchMap, newPatchMap);

		fullDifferenceModel = new MergedDataList<>(differenceModel, similarityModel);

		Log.fine("PATCH", "Component branches modeled successfully.");
	}

	/**
	 * Consumes patch data to a patch difference where the cache tree is empty.
	 * 
	 * @param studentCount
	 * @param patchData
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public PatchDifference(int studentCount, String patchData)
			throws FileNotFoundException, IOException, ParseException {

		Log.info("PATCH", "Consuming patch difference [ " + patchData.getBytes().length + " bytes ]");

		this.studentCount = studentCount;
		this.newPatchData = patchData;
		newPatchMap = Utils.createOrderedJSONMap(newPatchData);

		headerUpdates = new ArrayList<>();
		studentUpdates = new ArrayList<>();

		differenceModel = new ArrayList<>();
		similarityModel = new ArrayList<>();

		Log.fine("PATCH", "Modeling and comparing component branches...");

		consumePatch(ComponentType.WRITTEN_WORK, newPatchMap);
		consumePatch(ComponentType.PERFORMANCE_TASK, newPatchMap);
		consumePatch(ComponentType.QUARTERLY_ASSESSMENT, newPatchMap);

		fullDifferenceModel = new MergedDataList<>(differenceModel, similarityModel);

		Log.fine("PATCH", "Component branches modeled successfully.");
	}

	/**
	 * Handle table cell rendering updates
	 */
	public void updateTableCells() {
		Log.fine("PATCH", "Firing table cell updates for " + fullDifferenceModel.size() + " patch specification(s).");

		// Update table headers with diffview
		for (CellUpdate update : headerUpdates) {
			SwingUtilities.invokeLater(() -> UIHandler.fireCellHeaderMetadataUpdate(update));
		}
		// Update student name cells with diffview
		for (CellUpdate update : studentUpdates)
			SwingUtilities.invokeLater(() -> UIHandler.fireCellMetadataUpdate(update));

		for (PatchSpecification spec : fullDifferenceModel) {
			// Update student grade data cells with diffview
			if (spec.type == PatchSpecType.STUDENT_DATA) {
				if (spec.itemName.equals("item") || spec.itemName.equals("aggregate")) {
					CellUpdate update = spec.cellUpdate();
					SwingUtilities.invokeLater(() -> UIHandler.fireCellUpdate(update));
				}
			}

			switch (spec.operation) {
			case ADD:
				addOpCount += 1;
				break;
			case SET:
				updateOpCount += 1;
				break;
			case REMOVE:
				removeOpCount += 1;
				break;
			default:
				break;
			}

		}

		Log.fine("PATCH", "Updated " + fullDifferenceModel.size() + " table cell(s) in current record view.");
	}

	private void consumePatch(ComponentType type, LinkedHashMap patch) {
		String tree = type.getTag();
		LinkedHashMap patchBranch = getComponentBranch(patch, tree);

		if (patchBranch == null) {
			Log.fine("PATCH", "No branch: " + type + ". Ignored.");
			return;
		}

		if (patchBranch.isEmpty()) {
			Log.fine("PATCH", "Empty branch: " + type + ". Ignored.");
			return;
		}

		ArrayList<PatchSpecification> item = new ArrayList<>();
		ArrayList<PatchSpecification> elo = new ArrayList<>();
		ArrayList<PatchSpecification> aggregate = new ArrayList<>();

		for (int i = 1; i <= studentCount; i++) {
			LinkedHashMap studentData = (LinkedHashMap) patchBranch.get(String.valueOf(i));

			item.addAll(consStudentGradeData(tree, "item", i, studentData));
			elo.addAll(consStudentGradeData(tree, "elo", i, studentData));
			aggregate.addAll(consStudentGradeData(tree, "aggregate", i, studentData));
		}

		if (type != ComponentType.QUARTERLY_ASSESSMENT) {
			headerUpdate(type, item, 0, 10);
			headerUpdate(type, aggregate, 10, 3);
		} else {
			headerUpdate(type, item, 0, 1);
			headerUpdate(type, aggregate, 1, 2);
		}

		int header = studentUpdate(type, item);
		CellUpdate update = new CellUpdate();
		update.column = 1;
		update.type = type;
		update.metadata = new CellMetadata(1, DataType.values()[header]);
		headerUpdates.add(update);

		differenceModel.addAll(item);
		differenceModel.addAll(elo);
		differenceModel.addAll(aggregate);

	}

	/**
	 * Patch difference modeling
	 * 
	 * @param type
	 * @param patch
	 * @param patchNew
	 */
	private void diffComponentBranch(ComponentType type, LinkedHashMap patch, LinkedHashMap patchNew) {
		// Cut off the modeling process if there are problems with other
		// component branches.

		if (invalid)
			return;

		String tree = type.getTag();
		LinkedHashMap patchOldBranch = getComponentBranch(patch, tree);
		LinkedHashMap patchNewBranch = getComponentBranch(patchNew, tree);

		int totalChanges = 0;

		boolean flagEmptyOld = patchOldBranch == null;
		boolean flagEmptyNew = patchNewBranch == null;
		boolean oneEmpty = flagEmptyOld || flagEmptyNew;

		LinkedHashMap patchBranch = null;

		// Proofread both branches if they are empty.
		if (flagEmptyOld)
			Log.fine("PATCH", "Old branch for " + type + " is empty.");
		else
			patchBranch = patchOldBranch;

		if (flagEmptyNew)
			Log.fine("PATCH", "New branch for " + type + " is empty.");
		else
			patchBranch = patchNewBranch;

		if (flagEmptyOld && flagEmptyNew) {
			Log.fine("PATCH", "Ignoring branch " + type);
			return;
		}

		if (!oneEmpty) {
			if (patchOldBranch.isEmpty() && patchNewBranch.isEmpty()) {
				Log.fine("PATCH", "Empty branch: " + type + ". Ignored.");
				return;
			}
		} else if (patchBranch.isEmpty())
			return;

		// Prepare modeling process
		// Create contrast arrays.
		ArrayList<PatchSpecification> item = new ArrayList<>();
		ArrayList<PatchSpecification> elo = new ArrayList<>();
		ArrayList<PatchSpecification> aggregate = new ArrayList<>();

		if (!oneEmpty) {
			// If there are both branches to compare...
			for (int i = 1; i <= studentCount; i++) {
				LinkedHashMap studentOldData = (LinkedHashMap) patchOldBranch.get(String.valueOf(i));
				LinkedHashMap studentNewData = (LinkedHashMap) patchNewBranch.get(String.valueOf(i));

				ArrayList<PatchSpecification> itemDiff;
				ArrayList<PatchSpecification> eloDiff;
				ArrayList<PatchSpecification> aggregateDiff;

				try {
					itemDiff = diffStudentGradeData(tree, "item", i, studentOldData, studentNewData);
					eloDiff = diffStudentGradeData(tree, "elo", i, studentOldData, studentNewData);
					aggregateDiff = diffStudentGradeData(tree, "aggregate", i, studentOldData, studentNewData);
				} catch (IndexOutOfBoundsException e) {
					Alert alert = UIHandler.alert(AlertType.ERROR, "Cannot synchronize this Excel document",
							"Is this the recent version?");
					alert.showAndWait();
					invalid = true;
					return;
				}
				item.addAll(itemDiff);
				elo.addAll(eloDiff);
				aggregate.addAll(aggregateDiff);
			}
		} else {
			// There are no branches to compare.
			// This is a plausibly a first-time upload.
			for (int i = 1; i <= studentCount; i++) {
				LinkedHashMap studentData = (LinkedHashMap) patchBranch.get(String.valueOf(i));

				item.addAll(consStudentGradeData(tree, "item", i, studentData));
				elo.addAll(consStudentGradeData(tree, "elo", i, studentData));
				aggregate.addAll(consStudentGradeData(tree, "aggregate", i, studentData));
			}
		}

		// Begin UI and visual updates.
		// Header Columns
		if (type != ComponentType.QUARTERLY_ASSESSMENT) {
			headerUpdate(type, item, 0, 10);
			headerUpdate(type, aggregate, 10, 3);
		} else {
			headerUpdate(type, item, 0, 1);
			headerUpdate(type, aggregate, 1, 2);
		}

		// Getting highest order patch status from student
		int header = studentUpdate(type, item);
		CellUpdate update = new CellUpdate();
		update.column = 1;
		update.type = type;
		update.metadata = new CellMetadata(update.column, DataType.values()[header]);
		headerUpdates.add(update);

		differenceModel.addAll(item);
		differenceModel.addAll(elo);
		differenceModel.addAll(aggregate);

		totalChanges += item.size() + elo.size() + aggregate.size();

		if (!oneEmpty)
			totalChanges += digestCaps(tree, patchOldBranch, patchNewBranch);
		else
			totalChanges += consCaps(tree, patchBranch);

		Log.info("PATCH", "Modeled " + type + " successfully.");
		Log.info("PATCH", totalChanges + " changes found for " + tree);
	}

	private void headerUpdate(ComponentType type, List<PatchSpecification> node, int startIndex, int length) {
		ArrayList<PatchSpecification> itemSorted = new ArrayList<>(node);
		sort(itemSorted);

		// These are item indices
		for (int i = 0; i < length; i++) {

			ArrayList<PatchSpecification> itemBranch = filterByItem(itemSorted, i);
			int typeIndex = DataType.BLANK.ordinal();
			for (PatchSpecification spec : itemBranch) {
				int newTypeIndex = spec.getDataType().ordinal();
				if (newTypeIndex < typeIndex)
					typeIndex = newTypeIndex;
			}

			int col = startIndex + i + 2;

			CellUpdate update = new CellUpdate();
			update.column = col;
			update.type = type;
			update.metadata = new CellMetadata(col, DataType.values()[typeIndex]);
			headerUpdates.add(update);
		}
	}

	private int studentUpdate(ComponentType type, List<PatchSpecification> node) {
		ArrayList<PatchSpecification> itemSorted = new ArrayList<>(node);
		sort(itemSorted);

		int headerTypeIndex = DataType.BLANK.ordinal();
		for (int i = 1; i <= studentCount; i++) {
			ArrayList<PatchSpecification> itemBranch = filterByStudent(itemSorted, i);
			int typeIndex = DataType.BLANK.ordinal();
			for (PatchSpecification spec : itemBranch) {
				int newTypeIndex = spec.getDataType().ordinal();
				if (newTypeIndex < typeIndex) {
					headerTypeIndex = newTypeIndex;
					typeIndex = newTypeIndex;
				}
			}

			CellUpdate update = new CellUpdate();
			update.column = 1;
			update.row = i;
			update.type = type;
			update.metadata = new CellMetadata(update.row, update.column, DataType.values()[typeIndex]);
			studentUpdates.add(update);
		}

		return headerTypeIndex;
	}

	private ArrayList<PatchSpecification> filterByItem(List<PatchSpecification> items, int index) {
		return items.stream().filter(i -> i.itemTargetIndex == index).collect(Collectors.toCollection(ArrayList::new));
	}

	private ArrayList<PatchSpecification> filterByStudent(List<PatchSpecification> items, int index) {
		return items.stream().filter(i -> i.studentTargetIndex == index)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private void sort(List<PatchSpecification> list) {
		Collections.sort(list, new Comparator<PatchSpecification>() {
			public int compare(PatchSpecification o1, PatchSpecification o2) {
				if (o1.itemTargetIndex == o2.itemTargetIndex)
					return 0;
				return o1.itemTargetIndex < o2.itemTargetIndex ? -1 : 1;
			}
		});
	}

	private ArrayList<PatchSpecification> diffStudentGradeData(String tree, String itemName, int studentIndex,
			LinkedHashMap studentOldData, LinkedHashMap studentNewData) throws IndexOutOfBoundsException {

		ArrayList<PatchSpecification> specs = new ArrayList<>();
		ArrayList<Object> oldData = (ArrayList<Object>) studentOldData.get(itemName);
		ArrayList<Object> newData = (ArrayList<Object>) studentNewData.get(itemName);

		int oldSize = oldData.size();
		int newSize = newData.size();
		int size = oldSize > newSize ? oldSize : newSize;

		for (int itemIndex = 0; itemIndex < size; itemIndex++) {
			String path = Utils.pseudoPath("data", tree, String.valueOf(studentIndex), itemName,
					String.valueOf(itemIndex));

			Object o2 = newData.get(itemIndex);

			if (itemIndex < oldSize) {
				// Do comparisons
				Object o1 = oldData.get(itemIndex);

				if (!o1.equals(o2)) {
					PatchSpecification newSpec = new PatchSpecification(PatchOperation.SET, path, o2);
					specs.add(newSpec);
				} else {
					PatchSpecification newSpec = new PatchSpecification(PatchOperation.NONE, path, o2);
					specs.add(newSpec);
					addSimilaritySpec(PatchOperation.NONE, path, o2);
				}
				continue;
			}
			PatchSpecification newSpec = new PatchSpecification(PatchOperation.ADD, path, o2);
			specs.add(newSpec);
		}
		return specs;
	}

	private ArrayList<PatchSpecification> consStudentGradeData(String tree, String itemName, int studentIndex,
			LinkedHashMap studentData) {
		ArrayList<PatchSpecification> specs = new ArrayList<>();

		ArrayList<Object> data = (ArrayList<Object>) studentData.get(itemName);

		for (int itemIndex = 0; itemIndex < data.size(); itemIndex++) {
			String path = Utils.pseudoPath("data", tree, String.valueOf(studentIndex), itemName,
					String.valueOf(itemIndex));
			Object o = data.get(itemIndex);
			PatchSpecification newSpec = new PatchSpecification(PatchOperation.ADD, path, o);
			specs.add(newSpec);
		}
		return specs;
	}

	private int consCaps(String tree, LinkedHashMap patchBranch) {
		int changes = 0;
		changes += consCap(tree, "itemCaps", patchBranch);
		changes += consCap(tree, "aggregateCaps", patchBranch);
		return changes;
	}

	private int consCap(String tree, String cap, LinkedHashMap patchBranch) {
		int changes = 0;
		ArrayList<PatchSpecification> specs = new ArrayList<>();

		ArrayList<Object> data = (ArrayList<Object>) patchBranch.get(cap);

		for (int itemIndex = 0; itemIndex < data.size(); itemIndex++) {
			String path = Utils.pseudoPath("data", tree, cap, String.valueOf(itemIndex));
			Object o = data.get(itemIndex);
			PatchSpecification newSpec = new PatchSpecification(PatchOperation.ADD, path, o);
			addDifferenceSpec(newSpec);
			specs.add(newSpec);
			changes++;
		}

		return changes;
	}

	private int digestCaps(String tree, LinkedHashMap patchOldBranch, LinkedHashMap patchNewBranch) {
		int changes = 0;
		changes += digestCap(tree, "itemCaps", patchOldBranch, patchNewBranch);
		changes += digestCap(tree, "aggregateCaps", patchOldBranch, patchNewBranch);
		return changes;
	}

	private int digestCap(String tree, String cap, LinkedHashMap patchOldBranch, LinkedHashMap patchNewBranch) {
		ArrayList<Object> oldCaps = (ArrayList<Object>) patchOldBranch.get(cap);
		ArrayList<Object> newCaps = (ArrayList<Object>) patchNewBranch.get(cap);

		int oldSize = oldCaps.size();
		int newSize = newCaps.size();
		int capSize = oldSize > newSize ? oldSize : newSize;
		int changes = 0;

		for (int itemIndex = 0; itemIndex < capSize; itemIndex++) {
			String path = Utils.pseudoPath("data", tree, cap, String.valueOf(itemIndex));
			Object o2 = newCaps.get(itemIndex);
			if (itemIndex < oldSize) {
				Object o1 = oldCaps.get(itemIndex);

				if (!o1.equals(o2)) {
					addDifferenceSpec(PatchOperation.SET, path, o2);
					changes++;
				} else
					addSimilaritySpec(PatchOperation.NONE, path, o2);
				continue;
			}
			addDifferenceSpec(PatchOperation.ADD, path, o2);
			changes++;
		}

		return changes;
	}

	private void addDifferenceSpec(PatchOperation operation, String path, Object value) {
		differenceModel.add(new PatchSpecification(operation, path, value));
	}

	private void addDifferenceSpec(PatchSpecification spec) {
		differenceModel.add(spec);
	}

	private void addSimilaritySpec(PatchOperation operation, String path, Object value) {
		similarityModel.add(new PatchSpecification(operation, path, value));
	}

	public String createJSONPatch() throws JsonProcessingException, ParseException, IOException {
		return Utils.createPartialPatch(oldPatchData, newPatchData);
	}

	public void addCacheContext(ContextManager contextMan) {
		ArrayList<PatchSpecification> sim = similarityModel;
		ArrayList<Integer> indices = new ArrayList<>();
		for (PatchSpecification spec : sim) {
			if (spec.itemName.equalsIgnoreCase("item")) {
				int itemIndex = spec.itemTargetIndex;
				if (!indices.contains(itemIndex)) {
					indices.add(itemIndex);
				}
			}
		}
	}

	public PatchInstructionSet createInstructionSet() {
		String inst = "";

		for (PatchSpecification spec : fullDifferenceModel) {
			if (spec.operation == PatchOperation.NONE)
				continue;
			if (spec.itemName.equalsIgnoreCase("elo")) // TODO Ignoring elo data
				continue;
			if (spec.itemName.equalsIgnoreCase("aggregate")) // TODO Ignoring
																// aggregate
																// data
				continue;
			inst += spec.getInstruction() + "\n";
		}

		return new PatchInstructionSet(inst);
	}

	public LinkedHashMap getComponentBranch(LinkedHashMap patch, String tree) {
		return (LinkedHashMap) getDataTree(patch).get(tree);
	}

	public LinkedHashMap getDataTree(LinkedHashMap patch) {
		return (LinkedHashMap) patch.get("data");
	}

	public int getAddOpCount() {
		return addOpCount;
	}

	public int getUpdateOpCount() {
		return updateOpCount;
	}

	public int getRemoveOpCount() {
		return removeOpCount;
	}

}
