package local.vitre.desktop.task;

import javafx.concurrent.Task;
import local.vitre.desktop.record.data.WorkbookDocument;

public abstract class WorkbookOperationTask<V> extends Task<V> {

	protected WorkbookDocument document;
	private String requestor;
	private int quarter;

	public WorkbookOperationTask(WorkbookDocument document) {
		this.document = document;
	}

	public void setQuarter(int quarter) {
		this.quarter = quarter;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	public String getRequestor() {
		return requestor;
	}

	public int getQuarter() {
		return quarter;
	}

}
