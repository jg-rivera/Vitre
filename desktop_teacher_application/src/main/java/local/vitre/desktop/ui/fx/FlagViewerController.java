package local.vitre.desktop.ui.fx;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import local.vitre.desktop.FlagHandler;
import local.vitre.desktop.FlagHandler.Flag;
import local.vitre.desktop.ui.FlagListCell;

public class FlagViewerController {

	@FXML public ListView<Flag> flagListView;
	@FXML public Label flagCount;

	public void create(FlagHandler flagHandler) {
		flagCount.setText(String.valueOf(flagHandler.size()));
		flagListView.setItems(FXCollections.observableArrayList(flagHandler.flags));
		flagListView.setCellFactory(l -> new FlagListCell());
	}

	@FXML
	void onCancel(ActionEvent e) {
		Stage stage = (Stage) flagListView.getScene().getWindow();
		stage.close();
	}
}
