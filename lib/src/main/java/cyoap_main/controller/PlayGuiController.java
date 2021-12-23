package cyoap_main.controller;

import cyoap_main.design.node_extension.ImageCell;
import cyoap_main.design.node_extension.ResizableCanvas;
import cyoap_main.platform.AbstractPlatform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class PlayGuiController implements IGuiController {
	public static PlayGuiController instance;

	public AbstractPlatform platform;

	public ResizableCanvas canvas = new ResizableCanvas();
	@FXML
	public AnchorPane pane_position_play;
	@FXML
	public AnchorPane pane_position_play_parent;
	@FXML
	public GridPane gridpane_base;
	@FXML
	public GridPane gridpane_play_side;

	public ImageCell imagecell_background = new ImageCell();

    @Override
    public void nodeInit() {
        setUp();

        pane_position_play_parent.getChildren().add(canvas);
        pane_position_play.getChildren().add(imagecell_background);
        canvas.setMouseTransparent(true);
        canvas.toFront();
    }

	public PlayGuiController() {
		instance = this;
	}

	@Override
	public void load() {
		loadPlatform();
		IGuiController.super.load();
	}

	@Override
	public List<ImageCell> getBackgroundImageCellList() {
		var list = new ArrayList<ImageCell>();
		list.add(imagecell_background);
		return list;
	}

	@Override
	public Pane getChoicePane() {
		return pane_position_play;
	}

	@Override
	public Pane getChoicePaneParent() {
		return pane_position_play_parent;
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
		platform.update();
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public Canvas getCanvas() {
		return canvas;
	}

    @Override
    public double getChoicePaneRealWidth() {
        return gridpane_base.getWidth() - gridpane_play_side.getWidth();
    }

    @Override
    public double getChoicePaneRealHeight() {
        return gridpane_base.getHeight();
    }

    @Override
    public void eventInit() {
        pane_position_play.setOnMousePressed(e -> {
            platform.start_mouse_x = e.getSceneX();
            platform.start_mouse_y = e.getSceneY();
        });

        pane_position_play.setOnMouseDragged(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                double move_x = platform.sensitivity * (e.getSceneX() - platform.start_mouse_x);
                double move_y = platform.sensitivity * (e.getSceneY() - platform.start_mouse_y);
                updateMouseCoord(move_x, move_y, e.getSceneX(), e.getSceneY());
            }
        });
    }
}
