package local.vitre.desktop.util;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import local.vitre.desktop.Assets;
import local.vitre.desktop.ui.UIHandler;

public class IconBuilder {

	public MaterialDesignIconView check = buildIcon(MaterialDesignIcon.CHECK, 16, Color.DARKOLIVEGREEN);
	public MaterialDesignIconView outline = buildIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE_OUTLINE, 12,
			new Color(128 / 255, 128 / 255, 128 / 255, 1));
	public MaterialDesignIconView sync = buildIcon(MaterialDesignIcon.SYNC, 16, Color.DARKCYAN);
	public MaterialDesignIconView error = buildIcon(MaterialDesignIcon.ALERT, 16, Color.DARKRED);
	public MaterialDesignIconView cross = buildIcon(MaterialDesignIcon.WINDOW_CLOSE, 16, Color.DARKRED);

	public MaterialDesignIconView connected = buildIcon(MaterialDesignIcon.LAN_CONNECT, 16, Color.DARKGREEN);
	public MaterialDesignIconView disconnected = buildIcon(MaterialDesignIcon.LAN_DISCONNECT, 16, Color.DARKRED);
	
	public MaterialDesignIconView eye = buildIcon(MaterialDesignIcon.EYE, 20);
	public MaterialDesignIconView eyeOff = buildIcon(MaterialDesignIcon.EYE_OFF, 20);
	public MaterialDesignIconView fileImport = buildIcon(MaterialDesignIcon.LIBRARY_PLUS, 40, Color.DARKGRAY);
	public MaterialDesignIconView fileDocument = buildIcon(MaterialDesignIcon.FILE_DOCUMENT, 20, Color.WHITE);
	public MaterialDesignIconView excel = buildIcon(MaterialDesignIcon.FILE_EXCEL, 20, Color.WHITE);

	public ImageView vitre = buildImage("/dta.png", 64, 64);
	public ImageView patching = buildImage("/patching.gif", 84, 84);

	private static IconBuilder inst;

	public MaterialDesignIconView buildIcon(MaterialDesignIcon iconDesign, int size) {
		return buildIcon(iconDesign, size, Color.BLACK);
	}

	public MaterialDesignIconView buildIcon(MaterialDesignIcon iconDesign, int size, Color fill) {
		MaterialDesignIconView icon = new MaterialDesignIconView(iconDesign);
		icon.setGlyphSize(size);
		icon.setFill(fill);

		return icon;
	}

	public ImageView buildImage(String file, int width, int height) {
		Image image = new Image(Assets.getURLPath(file).toString());
		ImageView view = new ImageView(image);
		view.setPreserveRatio(true);
		view.setFitWidth(width);
		view.setFitHeight(height);
		view.setSmooth(true);
		return view;
	}

	public Node sync() {
		Node icon = IconBuilder.get().sync;
		UIHandler.addTooltip(icon, "Pending synchronization.");
		return icon;
	}

	public Node check() {
		Node icon = IconBuilder.get().check;
		UIHandler.addTooltip(icon, "Synchronized with the server.");
		return icon;
	}
	
	public Node cross() {
		Node icon = IconBuilder.get().cross;
		UIHandler.addTooltip(icon, "Error in retrieving state.");
		return icon;
	}
	
	// Network states
	public Node connected() {
		Node icon = IconBuilder.get().connected;
		UIHandler.addTooltip(icon, "Successfully communicating with the server!");
		return icon;
	}
	
	public Node disconnected() {
		Node icon = IconBuilder.get().disconnected;
		UIHandler.addTooltip(icon, "Unable to communicate with the server.");
		return icon;
	}


	public static IconBuilder get() {
		return inst != null ? inst : new IconBuilder();
	}
}
