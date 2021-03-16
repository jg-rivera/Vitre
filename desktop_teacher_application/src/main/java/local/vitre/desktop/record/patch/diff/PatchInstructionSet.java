package local.vitre.desktop.record.patch.diff;

import local.vitre.desktop.ui.CellMetadata.DataType;

public class PatchInstructionSet {

	public static final String MASTER = "!";
	public static final String DELIMETER = ",";
	public static final String BREAK = ";";

	public enum PatchOperation {
		ADD("ADD", DataType.NEW), SET("SET", DataType.REQ_UPDATE), REMOVE("REM", DataType.UNKNOWN), NONE("NONE",
				DataType.CACHED);

		private String op;
		private DataType dataType;

		PatchOperation(String op, DataType dataType) {
			this.op = op;
			this.dataType = dataType;
		}

		public String getOperationTag() {
			return op;
		}

		public DataType getDataType() {
			return dataType;
		}
	}

	public enum PatchMasterOperation {
		REMOVE_ALL("REM_ALL");

		private String op;

		private PatchMasterOperation(String op) {
			this.op = op;
		}

		public String getOperationTag() {
			return op;
		}
	}

	private String inst;

	public PatchInstructionSet(String inst) {
		this.inst = inst;
	}

	public void addRemoveAllInstruction(String hash) {
		StringBuilder b = new StringBuilder(inst);
		b.insert(0, MASTER + PatchMasterOperation.REMOVE_ALL);
	}

	public String getInstructions() {
		return inst;
	}
}
