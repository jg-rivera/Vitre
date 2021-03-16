package local.vitre.desktop.ui.fx;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import org.apache.commons.lang3.StringEscapeUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.stage.Stage;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.record.patch.ContextManager;
import local.vitre.desktop.record.patch.PatchContext;
import local.vitre.desktop.util.Utils;

public class PatchContextController implements Initializable {

	@FXML public Label noOfCharactersLeft, seed, hps, colName, componentName, rowCount, missingRowCount;
	@FXML public TextArea desc;

	protected PatchContext context;

	private boolean newContext;
	private int itemIndex, componentIndex;
	private final int MAX_LEN = 256;
	private ContextManager contextMan = Vitre.getActiveDocument().getContextManager();

	public void initialize(URL location, ResourceBundle resources) {
		desc.setTextFormatter(new TextFormatter<String>(new UnaryOperator<TextFormatter.Change>() {
			@Override
			public Change apply(Change change) {
				int len = change.getControlNewText().length();
				if (len <= MAX_LEN) {
					noOfCharactersLeft.setText(MAX_LEN - len + " characters left");
					return change;
				}
				return null;
			}
		}));
	}

	/**
	 * Register the patch context once if it has been confirmed.
	 * 
	 * @param e
	 */
	@SuppressWarnings("deprecation")
	@FXML
	void onSave(ActionEvent e) {
		String escDesc = StringEscapeUtils.escapeEcmaScript(desc.getText());
		
		if (newContext) {
			context.setDescription(escDesc);
			contextMan.addContext(context, true);
		} else {
			contextMan.updateContext(itemIndex, componentIndex, escDesc);
		}

		exit();
	}

	@FXML
	void onCancel(ActionEvent e) {
		exit();
	}

	private void exit() {
		Stage stage = (Stage) desc.getScene().getWindow();
		stage.close();
	}

	public void setNewContext(int quarter, int col, int itemIndex, int componentIndex, double cap, int occupiedCount,
			String columnName) {
		context = new PatchContext(quarter, true);
		context.setColumn(col);
		context.setItemIndex(itemIndex);
		context.setComponentIndex(componentIndex);
		context.setComponentName(columnName);
		context.setCap(cap);

		// Updating UI elements
		// componentName.setText(ComponentType.values()[componentIndex].name());
		hps.setText(Utils.noZeros("" + cap));
		rowCount.setText("" + occupiedCount);
		missingRowCount.setText(
				"(" + (Vitre.getActiveDocument().getClassRecord().getStudentsCount() - occupiedCount) + " missing)");
		colName.setText(columnName);
		seed.setText(context.getSeed());
		newContext = true;
	}

	public void setFilledContext(int occupiedCount, PatchContext context) {
		itemIndex = context.getItemIndex();
		componentIndex = context.getComponentIndex();

		// Updating UI elements
		// componentName.setText(ComponentType.values()[context.getComponentIndex()].name());
		hps.setText(Utils.noZeros("" + context.getCap()));
		rowCount.setText("" + occupiedCount);
		missingRowCount.setText(
				"(" + (Vitre.getActiveDocument().getClassRecord().getStudentsCount() - occupiedCount) + " missing)");
		colName.setText(context.getComponentName());
		desc.setText(context.getDescription());
		seed.setText(context.getSeed());
		context.setTimeStamp(Utils.time());
	}

}
