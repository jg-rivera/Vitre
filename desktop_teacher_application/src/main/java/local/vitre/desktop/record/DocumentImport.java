package local.vitre.desktop.record;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.json.simple.JSONObject;

import com.sun.javafx.scene.control.skin.TitledPaneSkin;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.record.data.Flushable;
import local.vitre.desktop.record.data.RecordConfiguration;
import local.vitre.desktop.record.data.WorkbookDocument;
import local.vitre.desktop.ui.CellMetadata;
import local.vitre.desktop.ui.FileChooserPaneHandler;
import local.vitre.desktop.util.IconBuilder;
import local.vitre.desktop.util.Utils;

@SuppressWarnings("restriction")
public class DocumentImport implements Flushable, Comparable<DocumentImport> {

	private int sortIndex;
	private String path;
	private String schema;
	private String name;
	private String contextFilePath;

	private String sectionValue;
	private String subjectValue;

	private Hyperlink activateLink;

	private TitledPane pane;
	private ImportManager manager;
	private WorkbookDocument workDoc;
	private RecordConfiguration config;

	private boolean created;
	private boolean cached;
	private boolean active;
	private boolean dragged;
	private boolean hasUI;

	public DocumentImport(String name, String path) {
		this.name = name;
		this.path = path;
		this.manager = Vitre.importer;
		this.contextFilePath = Vitre.assets.getEntryValue("CONTEXT_STORE_DIR") + Utils.noExtension(name);
	}

	public boolean create() {
		workDoc = new WorkbookDocument(path);
		created = workDoc.prepare(this);
		return created;
	}

	public void flush() {
		if (created && workDoc != null)
			workDoc.flush();
	}

	public void createUI(FileChooserPaneHandler fileChooser, HBox fileChooserPane) {

		pane = new TitledPane();
		pane.setGraphic(IconBuilder.get().excel);
		pane.setCollapsible(false);
		pane.setMinWidth(200);
		pane.setMinHeight(fileChooserPane.getMinHeight());
		pane.setAnimated(false);

		VBox content = new VBox();

		this.config = workDoc.getConfig();

		// Define attributes.
		if (!hasUI) {
			sectionValue = (String) config.getSuspendedData("gradeSection").getValue();
			subjectValue = (String) config.getSuspendedData("subject").getValue();
		}

		Label section = new Label("Section: " + sectionValue);
		Label subject = new Label("Subject: " + Utils.namify(subjectValue));

		String date[] = config.getTool().getLastModifiedDate().split(";");
		Label lastModifiedDate = new Label("Date: " + date[0]);
		Label lastModifiedTime = new Label("Time: " + date[1]);

		config.flush();

		HBox actions = new HBox(1.25D);

		Hyperlink manageLink = new Hyperlink("More...");
		Hyperlink removeLink = new Hyperlink("Remove");

		removeLink.setTextFill(Color.DARKRED);

		removeLink.setOnAction(event -> {
			manager.destroyImport(this);
			manager.removeImport(this);
		});

		activateLink = new Hyperlink();
		setActivatedStyling(true);

		activateLink.setOnAction(event -> {
			colorizeUI(CellMetadata.TP_ACTIVE_CSS);
			manager.setActiveImport(name);
		});

		// Actions
		actions.getChildren().addAll(activateLink, manageLink, removeLink);
		content.getChildren().addAll(section, subject, lastModifiedDate, lastModifiedTime, actions);

		Insets insets = new Insets(0.0, 3.0, 0.0, 5.0);
		HBox.setMargin(pane, insets);
		pane.setContent(content);
		pane.setText(name);

		/*
		 * Handle dragging
		 */

		// Dragging effect definitions
		InnerShadow dragEffect = new InnerShadow();
		dragEffect.setChoke(0.1);
		dragEffect.setColor(Color.CADETBLUE);

		// Vbox definitions
		Label dragText = new Label("Drag to rearrange documents");
		VBox vbox = new VBox(dragText);
		vbox.setAlignment(Pos.CENTER);
		vbox.setPrefWidth(170);
		vbox.setPrefHeight(5);

		// Popover definitions
		PopOver pop = new PopOver(vbox);
		pop.setDetachable(true);
		pop.setTitle("Sorting...");
		pop.setCloseButtonEnabled(false);

		ObjectProperty<Point2D> mousePosition = new SimpleObjectProperty<>();

		// Mouse press event
		pane.setOnMousePressed(e -> {
			mousePosition.set(new Point2D(e.getSceneX(), e.getSceneY()));

			if (!dragged && e.isPrimaryButtonDown()) {
				int index = sortIndex - 1;
				fileChooser.styleTransparent(index);
				pane.setEffect(dragEffect);

				double x = fileChooserPane.getLayoutX() + fileChooserPane.getWidth() / 2;
				double y = fileChooserPane.getLayoutY() - 30;
				Point2D p = fileChooserPane.localToScreen(x, y);

				pop.setDetached(true);
				pop.setArrowLocation(ArrowLocation.TOP_CENTER);
				pop.show(fileChooserPane, p.getX(), p.getY());
				dragged = true;
			}
		});

		// Mouse release event
		pane.setOnMouseReleased(e -> {
			if (dragged) {
				mousePosition.set(new Point2D(e.getSceneX(), e.getSceneY()));

				Point2D mouse = pane.localToParent(mousePosition.get());
				int index = sortIndex - 1;

				Platform.runLater(() -> {
					fileChooser.snap(index, pane.sceneToLocal(mouse), fileChooserPane);
				});

				pane.setEffect(null);
				fileChooser.styleSolid(index);
				pop.setDetached(true);
				pop.hide();
				dragged = false;
			}
		});

		// Mouse drag event
		pane.setOnMouseDragged(e -> {
			if (dragged && e.isPrimaryButtonDown()) {
				int index = sortIndex - 1;
				double dx = mousePosition.get().getX();
				double tx = dx - (pane.getWidth() * 2) - pane.getLayoutX() - (insets.getLeft());

				double width = fileChooserPane.getWidth() - (pane.getWidth() * index);
				double minWidth = fileChooserPane.getMinWidth() - (pane.getWidth() * index);

				// TODO Investigate behavior on scrollbarred window
				if (tx <= width && tx >= minWidth) {
					pane.setTranslateX(tx);
				}
			}
			mousePosition.set(new Point2D(e.getSceneX(), e.getSceneY()));
		});

		if (!hasUI)
			hasUI = true;
	}

	/**
	 * Create a json representation of an imported Excel document; for reusing
	 * in next read.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject createJSON() {
		JSONObject values = new JSONObject();
		values.put("path", path);
		values.put("context", contextFilePath);
		values.put("index", sortIndex);
		values.put("schema", workDoc.getSchema().getName());
		return values;
	}

	public void colorizeUI(String color) {
		TitledPaneSkin skin = (TitledPaneSkin) pane.getSkin();
		Pane titleRegion = (Pane) skin.getChildren().get(1);
		titleRegion.setStyle("-fx-background-color: " + color);
	}

	public void setActivatedStyling(boolean disabled) {
		if (disabled) {
			activateLink.setText("Activate");
			activateLink.setTextFill(Color.DARKGREEN);
		} else {
			activateLink.setText("Deactivate");
			activateLink.setTextFill(Color.DARKVIOLET);
		}
	}

	@Override
	public int compareTo(DocumentImport o) {
		if (sortIndex == o.sortIndex)
			return 0;
		return sortIndex < o.sortIndex ? -1 : 1;
	}

	public String getContextFilePath() {
		return contextFilePath;
	}

	@Override
	public boolean equals(Object obj) {
		return ((DocumentImport) obj).getName().equals(name);
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setCached(boolean cached) {
		this.cached = cached;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isCached() {
		return cached;
	}
	
	public boolean hasUI() {
		return hasUI;
	}

	public String getSchemaName() {
		return schema;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public String getName() {
		return name;
	}

	public TitledPane getTitledPane() {
		return pane;
	}

	public String getPath() {
		return path;
	}

	public RecordConfiguration getConfig() {
		return config;
	}

	public WorkbookDocument getWorkbookDocument() {
		return workDoc;
	}

	public ClassRecord getRecord() {
		return workDoc.getClassRecord();
	}

	public boolean isActive() {
		return active;
	}

}
