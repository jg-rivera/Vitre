package local.vitre.desktop.ui;

import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import local.vitre.desktop.record.patch.queue.PatchTracker;

public class QueueListCell extends ListCell<PatchTracker> {

	private final TextFlow textFlow = new TextFlow();

	@Override
	protected void updateItem(PatchTracker item, boolean empty) {
		if (!empty) {
			setText(null);

			textFlow.getChildren().clear();

			Text index = new Text("[Patch " + (item.index + 1) + "] ");
			index.setFill(Color.DARKBLUE);
			textFlow.getChildren().add(index);

			Text owner = new Text(item.teacher + ", " + " " + item.subject + ", " + item.gradeSection);
			textFlow.getChildren().add(owner);

			setGraphic(textFlow);
		} else {
			setText(null);
			setGraphic(null);
		}
		super.updateItem(item, empty);
	}
}
