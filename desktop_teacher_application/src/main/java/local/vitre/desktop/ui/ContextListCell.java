package local.vitre.desktop.ui;

import javafx.scene.control.ListCell;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import local.vitre.desktop.record.patch.PatchContext;

public class ContextListCell extends ListCell<PatchContext> {

	private final TextFlow textFlow = new TextFlow();

	public ContextListCell() {
		textFlow.setPrefWidth(Region.USE_COMPUTED_SIZE);
	}

	@Override
	protected void updateItem(PatchContext item, boolean empty) {
		if (!empty) {
			setText(null);
			textFlow.getChildren().clear();

			Text component = new Text(item.getComponentName() + " ");
			component.setFill(Color.DARKBLUE);
			textFlow.getChildren().add(component);

			Text seed = new Text("(" + item.getSeed() + "): ");
			seed.setFill(Color.GREEN);
			textFlow.getChildren().add(seed);

			Text desc = new Text(item.getDescription());
			textFlow.getChildren().add(desc);

			setGraphic(textFlow);
		} else {
			setText(null);
			setGraphic(null);
		}
		super.updateItem(item, empty);
	}
}
