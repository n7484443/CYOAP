package cyoap_main.design.controller;

import java.net.URL;
import java.util.ResourceBundle;

import cyoap_main.design.node_extension.ImageCell;
import cyoap_main.design.platform.AbstractPlatform;
import cyoap_main.design.platform.PlayPlatform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class PlayGuiController implements IPlatformGuiController {
	public static PlayGuiController instance;

	public AbstractPlatform platform;

	@FXML
	public AnchorPane pane_play;

	public ImageCell imagecell_background = new ImageCell();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setUp();
		pane_play.setOnMousePressed(e -> {
			platform.start_mouse_x = e.getSceneX();
			platform.start_mouse_y = e.getSceneY();
		});

		pane_play.setOnMouseDragged(e -> {
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				double move_x = platform.sensitivity * (e.getSceneX() - platform.start_mouse_x);
				double move_y = platform.sensitivity * (e.getSceneY() - platform.start_mouse_y);
				updateMouseCoord(move_x, move_y, e.getSceneX(), e.getSceneY());
			}
		});
	}

	public PlayGuiController() {
		instance = this;
		platform = new PlayPlatform(instance);
	}

	@Override
	public ImageCell getBackgroundImageView() {
		return imagecell_background;
	}

	@Override
	public Pane getChoicePane() {
		return pane_play;
	}

	@Override
	public AbstractPlatform getPlatform() {
		return platform;
	}

	@Override
	public void update() {
		
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public Canvas getCanvas() {
		return null;
	}
}
