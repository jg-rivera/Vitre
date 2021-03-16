package local.vitre.desktop.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import local.vitre.desktop.Assets;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.record.ClassRecord;
import local.vitre.desktop.record.ComponentType;
import local.vitre.desktop.record.GradingComponent;
import local.vitre.desktop.record.Student;
import local.vitre.desktop.record.data.MergedDataList;
import local.vitre.desktop.record.patch.ContextManager;
import local.vitre.desktop.record.patch.PatchContext;
import local.vitre.desktop.ui.fx.FlagViewerController;
import local.vitre.desktop.ui.fx.MainController;
import local.vitre.desktop.ui.fx.PatchContextController;
import local.vitre.desktop.ui.fx.PatchContextListController;
import local.vitre.desktop.ui.fx.PatchHistoryController;
import local.vitre.desktop.ui.fx.RegistryController;
import local.vitre.desktop.ui.swing.GradeTableCellRenderer;
import local.vitre.desktop.ui.swing.GradeTableHeaderRenderer;
import local.vitre.desktop.ui.swing.GradeTableKeyAdapter;
import local.vitre.desktop.ui.swing.GradeTableModel;
import local.vitre.desktop.ui.swing.GradeTableMouseAdapter;
import local.vitre.desktop.ui.swing.StudentListCellRenderer;
import local.vitre.desktop.ui.swing.StudentListModel;
import local.vitre.desktop.util.IconBuilder;

public class UIHandler {

	public static void repaint(SwingNode node) {
		repaint_logic(node);

	}

	public static void repaintAll(SwingNode... nodes) {
		Platform.runLater(() -> {
			for (SwingNode node : nodes)
				repaint_logic(node);
		});
	}

	private static void repaint_logic(SwingNode node) {
		if (node.getContent() != null)
			SwingUtilities.invokeLater(() -> node.getContent().repaint());
	}

	public static void repaintStudentList() {
		repaint(Vitre.controller.studentsListNode);
	}

	public static void showNewPatchContextCreateFX(int quarter, int col, int itemIndex, int componentIndex, double cap,
			int occupiedCount, String columnName) throws IOException {
		FXMLLoader loader = new FXMLLoader(MainController.class.getResource("PatchContextNew.fxml"));
		Parent root = loader.load();
		PatchContextController controller = loader.getController();

		Scene scene = new Scene(root);
		scene.getStylesheets().add(MainController.class.getResource("sys_font.css").toExternalForm());

		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Create new patch context...");
		stage.getIcons().add(new Image(Assets.getURLPath("/icon.png").toString()));

		controller.setNewContext(quarter, col, itemIndex, componentIndex, cap, occupiedCount, columnName);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}

	public static void showFilledPatchContextCreateFX(int occupiedCount, PatchContext context) throws IOException {
		FXMLLoader loader = new FXMLLoader(MainController.class.getResource("PatchContextNew.fxml"));
		Parent root = loader.load();
		PatchContextController controller = loader.getController();

		Scene scene = new Scene(root);
		scene.getStylesheets().add(MainController.class.getResource("sys_font.css").toExternalForm());

		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Update patch context...");
		stage.getIcons().add(new Image(Assets.getURLPath("/icon.png").toString()));

		stage.initModality(Modality.APPLICATION_MODAL);
		controller.setFilledContext(occupiedCount, context);
		stage.showAndWait();
	}

	public static void showPatchHistoryFX() throws IOException {
		FXMLLoader loader = new FXMLLoader(MainController.class.getResource("PatchHistory.fxml"));
		Parent root = loader.load();
		PatchHistoryController controller = loader.getController();
		controller.create();

		Scene scene = new Scene(root);
		scene.getStylesheets().add(MainController.class.getResource("style.css").toExternalForm());
		scene.getStylesheets().add(MainController.class.getResource("sys_font.css").toExternalForm());

		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("List of all published patches");
		stage.getIcons().add(new Image(Assets.getURLPath("/icon.png").toString()));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}

	public static void showPatchContextListFX(ContextManager manager) throws IOException {
		FXMLLoader loader = new FXMLLoader(MainController.class.getResource("PatchContextList.fxml"));
		Parent root = loader.load();
		PatchContextListController controller = loader.getController();
		controller.create(manager);

		Scene scene = new Scene(root);
		scene.getStylesheets().add(MainController.class.getResource("style.css").toExternalForm());
		scene.getStylesheets().add(MainController.class.getResource("sys_font.css").toExternalForm());

		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("List of created patch contexts for this record");
		stage.getIcons().add(new Image(Assets.getURLPath("/icon.png").toString()));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}

	public static void showFlagViewerFX() throws IOException {
		FXMLLoader loader = new FXMLLoader(MainController.class.getResource("FlagViewer.fxml"));
		Parent root = loader.load();
		FlagViewerController controller = loader.getController();
		controller.create(Vitre.flagger);

		Scene scene = new Scene(root);
		scene.getStylesheets().add(MainController.class.getResource("sys_font.css").toExternalForm());

		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Application flags and warnings");
		stage.getIcons().add(new Image(Assets.getURLPath("/icon.png").toString()));
		stage.showAndWait();
	}

	public static void showRegisterFX(ClassRecord record) throws IOException {
		FXMLLoader loader = new FXMLLoader(MainController.class.getResource("Register.fxml"));
		Parent root = loader.load();
		RegistryController controller = loader.getController();

		Scene scene = new Scene(root);
		Stage stage = new Stage();
		controller.create(stage, record);
		scene.getStylesheets().add(MainController.class.getResource("registry.css").toExternalForm());
		stage.setScene(scene);
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Register this class...");
		stage.getIcons().add(new Image(Assets.getURLPath("/icon.png").toString()));

		stage.showAndWait();
	}

	public static void fireCellUpdate(CellUpdate update) {
		SwingNode node = getNode(update.type);
		setCellValueAt(node, update.value, update.row, update.column);
		setCellMetadataAt(node, update.metadata);
	}

	public static void fireCellMetadataUpdate(CellUpdate update) {
		SwingNode node = getNode(update.type);
		setCellMetadataAt(node, update.metadata);
	}

	public static void fireCellHeaderMetadataUpdate(CellUpdate update) {
		SwingNode node = getNode(update.type);
		setCellHeaderMetadata(node, update.metadata);
	}

	public static SwingNode getNode(ComponentType type) {
		switch (type) {
		case WRITTEN_WORK:
			return Vitre.controller.writtenWorkNode;
		case PERFORMANCE_TASK:
			return Vitre.controller.performanceTasksNode;
		case QUARTERLY_ASSESSMENT:
			return Vitre.controller.quarterlyAssessmentNode;
		default:
			return null;
		}
	}

	public static void setCellValueAt(SwingNode node, Object value, int row, int column) {
		if (node.getContent() != null)
			getModel(node).setValueAt(value, row, column);
	}

	public static void setCellMetadataAt(SwingNode node, CellMetadata metadata) {
		if (node.getContent() != null)
			getModel(node).setMetadata(metadata);
	}

	public static void setCellHeaderMetadata(SwingNode node, CellMetadata metadata) {
		if (node.getContent() != null)
			getModel(node).setHeaderMetadata(getTable(node), metadata);
	}

	public static JTable getTable(SwingNode node) {
		JScrollPane pane = (JScrollPane) node.getContent();
		JTable table = (JTable) pane.getViewport().getComponent(0);
		return table;
	}

	public static GradeTableModel getModel(SwingNode node) {
		GradeTableModel model = (GradeTableModel) getTable(node).getModel();
		return model;
	}

	public static GradeTableModel getModel(JTable table) {
		GradeTableModel model = (GradeTableModel) table.getModel();
		return model;
	}

	public static GradeTableHeaderRenderer getHeaderRenderer(SwingNode node) {
		return (GradeTableHeaderRenderer) getTable(node).getTableHeader().getDefaultRenderer();
	}

	public static void focus(SwingNode node) {
		Platform.runLater(() -> node.requestFocus());
	}

	/**
	 * Updates the flag counter; reflecting on the current flag list size.
	 */
	public static void refreshCounter() {
		Platform.runLater(() -> Vitre.controller.flagCount.setText(String.valueOf(Vitre.flagger.size())));
	}

	public static void addTooltip(Node node, String text) {
		Tooltip t = new Tooltip(text);
		t.setFont(javafx.scene.text.Font.font(10));
		Tooltip.install(node, t);
	}

	/**
	 * Toggles the label's current sync icon.
	 */
	public static void toggleCheck(Label label, boolean bool) {
		if (bool) {
			label.setGraphic(IconBuilder.get().check);
		} else
			label.setGraphic(IconBuilder.get().outline);
	}

	/**
	 * Resets the main steps labels on Class Record Input Data window.
	 */
	public static void resetToggleCheck() {
		MainController c = Vitre.controller;
		toggleCheck(c.checkLabelParsed, false);
		toggleCheck(c.checkLabelVerified, false);
		toggleCheck(c.checkLabelSync, false);
	}

	/**
	 * Creates a default alert dialog.
	 */
	public static Alert alert(AlertType type, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle("Alert!");
		alert.setContentText(content);
		alert.setHeaderText(header);
		alert.initModality(Modality.WINDOW_MODAL);
		alert.initOwner(Vitre.getMainStage());
		return alert;
	}

	/**
	 * File chooser object for excel documents.
	 */
	public static FileChooser recordChooser() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Import class record(s)...");
		chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel Workbook", "*.xlsx"),
				new FileChooser.ExtensionFilter("Excel 92-2003 Workbook", "*.xls"),
				new FileChooser.ExtensionFilter("All Files", "*.*"));
		return chooser;
	}

	/**
	 * File chooser object for word documents
	 */
	public static DirectoryChooser sheetPathChooser(String defaultPath) {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Set output folder for account sheet...");
		File defaultDirectory = new File(defaultPath);
		chooser.setInitialDirectory(defaultDirectory);
		return chooser;
	}

	public static Node attachIcon(String file, double size) {
		Image image = new Image(Assets.getURLPath(file).toString());
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(size);
		imageView.setFitHeight(size);
		return imageView;
	}

	/**
	 * Displays a pop over on the class record view pane.
	 */
	public static void showTaskPopOverOnCRV(TaskPopOver pop) {
		TitledPane pane = Vitre.controller.recordViewTitlePane;

		double x = pane.getLayoutX() + pane.getWidth() / 2;
		double y = pane.getLayoutY() + pane.getHeight() / 2 - 50;
		Point2D p = pane.localToScreen(x, y);

		pop.show(pane, p.getX(), p.getY());
	}

	/**
	 * Displays a pop over on the class record view pane.
	 */
	public static void showTaskPopOverOnIV(TaskPopOver pop) {
		HBox pane = Vitre.controller.fileChooserPane;

		double x = pane.getLayoutX() + pane.getWidth() / 2;
		double y = pane.getLayoutY() - 30;
		Point2D p = pane.localToScreen(x, y);

		pop.show(pane, p.getX(), p.getY());
	}

	public static Color blend(Color c0, Color c1) {
		double totalAlpha = c0.getAlpha() + c1.getAlpha();
		double weight0 = c0.getAlpha() / totalAlpha;
		double weight1 = c1.getAlpha() / totalAlpha;

		double r = weight0 * c0.getRed() + weight1 * c1.getRed();
		double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
		double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
		double a = Math.max(c0.getAlpha(), c1.getAlpha());

		return new Color((int) r, (int) g, (int) b, (int) a);
	}

	public static void updateColumnInfo(String rowColValue, String studentValue, String itemValue, String contextValue,
			String stateValue) {
		Vitre.controller.selectedRowColValue.setText(rowColValue);
		Vitre.controller.selectedStudentValue.setText(studentValue);
		Vitre.controller.selectedItemValue.setText(itemValue);
		Vitre.controller.selectedContextValue.setText(contextValue);
		Vitre.controller.selectedStateValue.setText(stateValue);
	}

	public static void attachStudentList(MergedDataList<Student> studentList, SwingNode node) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}

			JList<Student> students = new JList<>(new StudentListModel(studentList));
			students.setSelectionForeground(Color.black);
			students.setCellRenderer(new StudentListCellRenderer());
			students.setVisibleRowCount(13);

			JScrollPane pane = new JScrollPane(students);
			node.setContent(pane);
		});
	}

	public static void handleSelection(int row, int col, GradeTableModel model) {
		String rowColValue = "(" + row + ", " + col + ")";
		String studentValue = "" + model.getStudentOwner(row, col);
		String itemValue = model.getFullColumnName(col);
		ContextManager conMan = Vitre.getActiveContextor();

		PatchContext con = conMan.getContext(GradeTableModel.getItemIndex(col), model.getComponentIndex());

		if (con != null)
			Platform.runLater(() -> updateColumnInfo(rowColValue, studentValue, itemValue, con.getSeed(),
					model.getHeaderMetadata(col).type.name()));
		else
			Platform.runLater(() -> updateColumnInfo(rowColValue, studentValue, itemValue, "n/a",
					model.getHeaderMetadata(col).type.name()));
	}

	/**
	 * Constructs table.
	 * 
	 * @param component
	 * @param node
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	public static void attachGradeTables(GradingComponent component, SwingNode node)
			throws InvocationTargetException, InterruptedException {

		SwingUtilities.invokeLater(() -> {
			try {
				// Set Swing theme to native.
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Create table with data
			JTable table = new JTable(new GradeTableModel(component)) {
				@Override
				public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {

					Component c = super.prepareRenderer(renderer, row, column);
					if (isRowSelected(row) && isColumnSelected(column)) {
						((JComponent) c).setForeground(Color.BLACK);
						((JComponent) c).setBackground(CellMetadata.selection);
					}
					return c;
				}
			};

			TableColumnModel colModel = table.getColumnModel();
			colModel.getColumn(0).setMinWidth(30);
			colModel.getColumn(0).setMaxWidth(30);
			colModel.getColumn(1).setMinWidth(130);

			ComponentType type = component.getType();

			if (type != ComponentType.QUARTERLY_ASSESSMENT) {
				for (int c = 2; c <= 11; c++) {
					colModel.getColumn(c).setMaxWidth(100);
				}
			}

			JTableHeader tableHeader = table.getTableHeader();
			tableHeader.setFont(new Font("Arial", Font.PLAIN, 14));
			tableHeader.setFocusable(true);
			tableHeader.setRequestFocusEnabled(true);
			tableHeader.setReorderingAllowed(false);
			tableHeader.setDefaultRenderer(new GradeTableHeaderRenderer());

			((DefaultTableCellRenderer) tableHeader.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

			// Patch Context right-click handling
			table.addMouseListener(new GradeTableMouseAdapter(table));

			// Key traversal handling for information.
			table.addKeyListener(new GradeTableKeyAdapter(table));

			table.setDefaultRenderer(Object.class, new GradeTableCellRenderer());
			table.setRowSelectionAllowed(true);
			table.setCellSelectionEnabled(true);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

			table.setShowGrid(false);
			table.setIntercellSpacing(new Dimension(0, 0));
			table.setFont(new Font("Arial", Font.PLAIN, 13));

			JScrollPane pane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

			// Feed scrollpane containing table to Swing Node
			node.setContent(pane);
		});
	}
}
