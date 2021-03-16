package local.vitre.desktop.ui;

import javafx.scene.control.ListCell;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import local.vitre.desktop.FlagHandler.Flag;

public class FlagListCell extends ListCell<Flag> {

	private final TextFlow textFlow = new TextFlow();

	public FlagListCell() {
		textFlow.setPrefWidth(Region.USE_COMPUTED_SIZE);
	}

	@Override
	protected void updateItem(Flag flag, boolean empty) {
		if (!empty) {
			setText(null);
			textFlow.getChildren().clear();

			Text component = new Text(flag.name() + " ");
			component.setFill(Color.DARKRED);
			textFlow.getChildren().add(component);

			Text desc = new Text(flag.getDescription());
			textFlow.getChildren().add(desc);

			setGraphic(textFlow);
		} else {
			setText(null);
			setGraphic(null);
		}
		super.updateItem(flag, empty);
	}
}
