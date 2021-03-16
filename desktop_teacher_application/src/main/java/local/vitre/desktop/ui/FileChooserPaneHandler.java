package local.vitre.desktop.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import local.vitre.desktop.record.DocumentImport;
import local.vitre.desktop.record.ImportManager;
import local.vitre.desktop.ui.fx.MainController;
import local.vitre.desktop.util.IconBuilder;

public class FileChooserPaneHandler {

	private ImportManager manager;
	private FileChooser chooser;

	InnerShadow snapEffect = new InnerShadow();

	public FileChooserPaneHandler(ImportManager manager) {
		this.manager = manager;
		this.chooser = UIHandler.recordChooser();
		snapEffect.setChoke(0.1);
		snapEffect.setColor(Color.PALEVIOLETRED);
	}

	public void buildUI(HBox fileChooserPane) {

		InnerShadow dragEffect = new InnerShadow();
		dragEffect.setChoke(0.29);
		dragEffect.setColor(Color.BLUEVIOLET);

		fileChooserPane.getStylesheets().add(MainController.class.getResource("file.css").toExternalForm());
		fileChooserPane.getStyleClass().add("directory");
		fileChooserPane.getStyleClass().clear();

		// Drag over
		fileChooserPane.setOnDragOver(event -> {
			Dragboard db = event.getDragboard();

			if (db.hasFiles()) {
				for (File file : db.getFiles()) {
					if (!file.getName().split("\\.")[1].equalsIgnoreCase("xlsx")) {
						event.acceptTransferModes(TransferMode.NONE);
						event.consume();
						return;
					}
				}

				event.acceptTransferModes(TransferMode.LINK);

				fileChooserPane.setEffect(dragEffect);
			} else {
				fileChooserPane.setEffect(null);
				event.consume();
			}
		});

		// Drag exit
		fileChooserPane.setOnDragExited(event -> fileChooserPane.setEffect(null));

		// Drag n drop
		fileChooserPane.setOnDragDropped(event -> {

			Dragboard db = event.getDragboard();

			boolean success = false;
			if (db.hasFiles())
				success = manager.importAll(db.getFiles());

			fileChooserPane.setEffect(null);
			event.setDropCompleted(success);
			event.consume();

		});

		attachHelper(fileChooserPane);
	}

	private void attachHelper(HBox fileChooserPane) {
		InnerShadow buttonEffect = new InnerShadow();
		buttonEffect.setChoke(0.05);
		buttonEffect.setColor(Color.DARKBLUE);

		Label helper = new Label("Import class records from the File > Import Menu");
		helper.setStyle("-fx-text-fill: #696969;");
		helper.setUserData("emptyState");
		helper.setFont(new Font(13));
		helper.setContentDisplay(ContentDisplay.TOP);
		MaterialDesignIconView icon = IconBuilder.get().fileImport;

		icon.setOnMouseEntered(event -> {
			icon.setEffect(buttonEffect);
			icon.setCursor(Cursor.HAND);
		});

		icon.setOnMouseExited(event -> {
			icon.setEffect(null);
		});

		icon.setOnMouseClicked(event -> {
			List<File> files = chooser.showOpenMultipleDialog(fileChooserPane.getScene().getWindow());
			manager.importAll(files);
		});

		helper.setGraphic(icon);

		fileChooserPane.setAlignment(Pos.CENTER);
		fileChooserPane.getChildren().add(helper);
	}

	public void redraw(HBox fileChooserPane) {
		if (fileChooserPane.getChildren().get(0).getUserData() == "emptyState") {
			fileChooserPane.getChildren().remove(0);
			fileChooserPane.setAlignment(Pos.CENTER_LEFT);
		}

		ArrayList<DocumentImport> imports = manager.getImports();

		if (!imports.isEmpty()) {

			ObservableList<Node> children = fileChooserPane.getChildren();

			// Sort document imports according to their indices for reinsertion.
			Collections.sort(imports);

			// Redraw
			if (!children.isEmpty()) {
				children.clear();

				for (int i = 0; i < imports.size(); i++) {
					DocumentImport documentImp = imports.get(i);

					// Create UI if imported when there are already imported.
					if (!documentImp.hasUI())
						documentImp.createUI(this, fileChooserPane);

					// Reset offsets from dragging
					TitledPane pane = documentImp.getTitledPane();
					pane.setTranslateX(0);
					fileChooserPane.getChildren().add(pane);
				}
			} else {
				// First draw
				for (int i = 0; i < imports.size(); i++) {
					DocumentImport documentImp = imports.get(i);
					documentImp.createUI(this, fileChooserPane);
					fileChooserPane.getChildren().add(documentImp.getTitledPane());
				}
			}
		}
	}

	public void styleTransparent(int originIndex) {
		int size = manager.getImportSize();
		for (int o = 0; o < size; o++) {
			if (o == originIndex)
				continue;
			DocumentImport v = manager.getImports().get(o);
			TitledPane imp = v.getTitledPane();
			imp.setStyle("-fx-opacity: 0.25");
		}
	}

	public void styleSolid(int originIndex) {
		int size = manager.getImportSize();
		for (int o = 0; o < size; o++) {
			if (o == originIndex)
				continue;
			DocumentImport v = manager.getImports().get(o);
			TitledPane imp = v.getTitledPane();
			imp.setStyle("-fx-opacity: 1");
		}
	}

	public void snap(int originIndex, Point2D mouse, HBox fileChooserPane) {
		if (!manager.getImports().isEmpty()) {
			DocumentImport originImp = manager.getImports().get(originIndex);
			int size = manager.getImportSize();

			for (int vectorIndex = 0; vectorIndex < size; vectorIndex++) {
				if (vectorIndex == originIndex)
					continue;

				DocumentImport vectorImp = manager.getImports().get(vectorIndex);
				TitledPane pane = vectorImp.getTitledPane();
				Bounds b = pane.getBoundsInParent();

				double w = pane.getWidth() / 3;
				double h = pane.getHeight();
				double x = mouse.getX() - (w / 2);
				double y = mouse.getY() - (h / 2);

				boolean collision = b.intersects(x, y, w, h);

				// Handle collision
				if (collision) {
					int originSortIndex = originImp.getSortIndex();
					int vectorSortIndex = vectorImp.getSortIndex();

					originImp.setSortIndex(vectorSortIndex);
					vectorImp.setSortIndex(originSortIndex);

					sort(originSortIndex, vectorSortIndex, size);
					redraw(fileChooserPane);
					return;
				}
			}

			// Handle offsnaps
			ObservableList<Node> children = fileChooserPane.getChildren();
			int frontIndex = children.size() - 1;

			if (originIndex != frontIndex) {
				DocumentImport frontImp = manager.getImports().get(frontIndex);

				// If exceed last import bounds
				if (mouse.getX() >= frontImp.getTitledPane().getLayoutX()) {
					int originSortIndex = originImp.getSortIndex();
					int frontSortIndex = frontImp.getSortIndex();

					originImp.setSortIndex(frontSortIndex);
					frontImp.setSortIndex(originSortIndex);

					sort(originSortIndex, frontSortIndex, size);
				}
			}

		}
		redraw(fileChooserPane);
	}

	private void sort(int originSortIndex, int vectorSortIndex, int size) {

		for (int sortIndex = 1; sortIndex <= size; sortIndex++) {
			int index = sortIndex - 1;
			if (sortIndex == originSortIndex || sortIndex == vectorSortIndex) {
				continue;
			}

			DocumentImport currImport = manager.getImports().get(index);
			currImport.setSortIndex(sortIndex);
		}
	}

	public void remove(String name, HBox fileChooserPane) {
		ObservableList<Node> nodes = fileChooserPane.getChildren();
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			if (node instanceof TitledPane) {
				TitledPane pane = (TitledPane) node;
				if (pane.getText().equals(name))
					nodes.remove(i);
			}
		}
		if (nodes.isEmpty())
			attachHelper(fileChooserPane);
	}
}
