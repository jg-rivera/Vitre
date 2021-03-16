package local.vitre.desktop.ui.fx;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import local.vitre.desktop.record.ComponentType;
import local.vitre.desktop.record.patch.ContextManager;
import local.vitre.desktop.record.patch.PatchContext;
import local.vitre.desktop.ui.ContextListCell;

public class PatchContextListController {

	@FXML public ListView<PatchContext> writtenWorkList, perfTaskList, quarterlyAssessmentList;
	@FXML public Label contextsCount;

	public void create(ContextManager manager) {
		writtenWorkList.setItems(FXCollections.observableArrayList(manager.getContexts(ComponentType.WRITTEN_WORK)));
		writtenWorkList.setCellFactory(l -> new ContextListCell());
		writtenWorkList.setPlaceholder(new Label("No contexts found."));

		perfTaskList.setItems(FXCollections.observableArrayList(manager.getContexts(ComponentType.PERFORMANCE_TASK)));
		perfTaskList.setCellFactory(l -> new ContextListCell());
		perfTaskList.setPlaceholder(new Label("No contexts found."));

		quarterlyAssessmentList
				.setItems(FXCollections.observableArrayList(manager.getContexts(ComponentType.QUARTERLY_ASSESSMENT)));
		quarterlyAssessmentList.setCellFactory(l -> new ContextListCell());
		quarterlyAssessmentList.setPlaceholder(new Label("No contexts found."));

		contextsCount.setText(String.valueOf(manager.getContexts().size()));
	}

	@FXML
	void onClear(ActionEvent e) {
		Stage stage = (Stage) contextsCount.getScene().getWindow();
		stage.close();
	}

	@FXML
	void onCancel(ActionEvent e) {
		Stage stage = (Stage) contextsCount.getScene().getWindow();
		stage.close();
	}
}
