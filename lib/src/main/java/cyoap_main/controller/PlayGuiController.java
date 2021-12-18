package cyoap_main.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import cyoap_main.core.JavaFxMain;
import cyoap_main.design.node_extension.ImageCell;
import cyoap_main.design.node_extension.ResizableCanvas;
import cyoap_main.platform.AbstractPlatform;
import cyoap_main.platform.PlayPlatform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class PlayGuiController implements IGuiController {
	public static PlayGuiController instance;

	public AbstractPlatform platform;

	public ResizableCanvas canvas = new ResizableCanvas();
	@FXML
	public AnchorPane pane_play;
	@FXML
	public AnchorPane pane_play_parent;

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
	public void load() {
		loadPlatform();
	}

	@Override
	public List<ImageCell> getBackgroundImageCellList() {
		var list = new ArrayList<ImageCell>();
		list.add(imagecell_background);
		return list;
	}

	@Override
	public Pane getChoicePane() {
		return pane_play;
	}

	@Override
	public Pane getChoicePaneParent() {
		return pane_play_parent;
	}

	@Override
	public void setPlatform(AbstractPlatform abstractPlatform) {
		this.platform = abstractPlatform;
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
		return canvas;
	}
}
