package local.vitre.desktop.task;

import javafx.concurrent.Task;
import local.vitre.desktop.record.ClassRecord;

public abstract class RecordOperationTask<V> extends Task<V> {

	protected ClassRecord record;
	protected int quarter;

	public RecordOperationTask(ClassRecord record) {
		this.record = record;
	}

	public void setQuarter(int quarter) {
		this.quarter = quarter;
	}

}
