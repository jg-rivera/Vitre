package local.vitre.desktop.task;

import java.io.File;

import javafx.concurrent.Task;

public abstract class FileOperationTask<V> extends Task<V> implements IUserData {

	protected File file;
	protected String userdata;

	public FileOperationTask(File file) {
		this.file = file;
	}

	public void setUserData(String data) {
		this.userdata = data;
	}

	public String getUserData() {
		return userdata;
	}
}
