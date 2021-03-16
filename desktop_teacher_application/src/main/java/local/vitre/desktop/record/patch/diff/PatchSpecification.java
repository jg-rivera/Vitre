package local.vitre.desktop.record.patch.diff;

import static local.vitre.desktop.record.patch.diff.PatchInstructionSet.BREAK;
import static local.vitre.desktop.record.patch.diff.PatchInstructionSet.DELIMETER;

import local.vitre.desktop.record.ComponentType;
import local.vitre.desktop.record.patch.diff.PatchInstructionSet.PatchOperation;
import local.vitre.desktop.ui.CellMetadata;
import local.vitre.desktop.ui.CellMetadata.DataType;
import local.vitre.desktop.ui.CellUpdate;
import local.vitre.desktop.util.Utils;

public class PatchSpecification {

	public static final String ITEM_ELEMENT = "item";
	public static final String ELO_ELEMENT = "elo";
	public static final String AGGR_ELEMENT = "aggregate";

	public enum PatchSpecType {
		STUDENT_DATA, COMPONENT_DATA;
	}

	public PatchOperation operation;
	public PatchSpecType type;
	public ComponentType componentType;

	public String path;
	public String componentName;
	public String itemName;

	public int studentTargetIndex;
	public int itemTargetIndex;
	public int cellOffset = 2;

	public Object value;

	public PatchSpecification(PatchOperation operation, String path, Object value) {
		this.operation = operation;
		this.path = path;
		this.value = value;
		this.componentName = split(1);
		this.componentType = ComponentType.getType(componentName);

		try {
			this.studentTargetIndex = Integer.parseInt(split(2));
			this.itemName = split(3);
			this.itemTargetIndex = Integer.parseInt(split(4));
			if (itemName.equals("aggregate")) {
				if (componentType != ComponentType.QUARTERLY_ASSESSMENT)
					cellOffset += 10;
			}
			this.type = PatchSpecType.STUDENT_DATA;
		} catch (NumberFormatException e) {
			this.studentTargetIndex = -1;
			this.itemName = split(2);
			this.itemTargetIndex = Integer.parseInt(split(3));
			this.type = PatchSpecType.COMPONENT_DATA;
		}

	}

	public CellUpdate cellUpdate() {
		CellUpdate update = new CellUpdate();
		update.row = studentTargetIndex;
		update.column = cellOffset + itemTargetIndex;
		update.value = Utils.noZeros(value.toString());
		update.type = componentType;
		update.metadata = new CellMetadata(update.row, update.column, update.value, operation.getDataType());
		return update;
	}

	public DataType getDataType() {
		return operation.getDataType();
	}

	private String split(int index) {
		return path.split("/")[index];
	}

	public String getInstruction() {
		return operation.getOperationTag() + DELIMETER + componentType.ordinal() + DELIMETER + itemName + DELIMETER
				+ studentTargetIndex + DELIMETER + itemTargetIndex + DELIMETER + value + BREAK;
	}

	@Override
	public String toString() {
		return operation.name() + " -> " + value + " on " + componentName + " path: " + path;
	}
}
