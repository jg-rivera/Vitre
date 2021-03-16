package local.vitre.desktop.ui.fx;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.record.patch.queue.PatchQueueManager;
import local.vitre.desktop.record.patch.queue.PatchTracker;
import local.vitre.desktop.ui.QueueListCell;

public class PatchHistoryController {

	@FXML public ListView<PatchTracker> patchesListView;

	@FXML public Label date, document, className, subjectName, teacherName, added, removed, updated;

	public void create() {
		PatchQueueManager manager = Vitre.getQueueManager();
		patchesListView.getItems().addAll(manager.getTrackers());
		patchesListView.setCellFactory(l -> new QueueListCell());

		Comparator<PatchTracker> comparator = Comparator.comparingInt(PatchTracker::getIndex);
		patchesListView.getItems().sort(comparator.reversed());

		DateFormat simple = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");

		patchesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PatchTracker>() {

			@Override
			public void changed(ObservableValue<? extends PatchTracker> observable, PatchTracker oldValue,
					PatchTracker t) {

				date.setText(simple.format(new Date(t.timestamp)));
				document.setText(t.document);
				className.setText(t.gradeSection);
				subjectName.setText(t.subject);
				teacherName.setText(t.teacher);
				added.setText(String.valueOf(t.addOpCount));
				removed.setText(String.valueOf(t.removeOpCount));
				updated.setText(String.valueOf(t.updateOpCount));
			}
		});
	}

	@FXML
	public void onCancel(ActionEvent e) {
		Stage stage = (Stage) date.getScene().getWindow();
		stage.close();
	}
}
