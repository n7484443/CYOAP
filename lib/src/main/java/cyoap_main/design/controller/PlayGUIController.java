package cyoap_main.design.controller;

import java.net.URL;
import java.util.ResourceBundle;

import cyoap_main.design.platform.AbstractPlatform;
import cyoap_main.design.platform.PlayPlatform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class PlayGUIController implements IPlatformGuiController {
	public static PlayGUIController instance;

	public AbstractPlatform platform;

	@FXML
	public AnchorPane pane_play;
	@FXML
	public ImageView imageview_background;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		pane_play.setOnMousePressed(e -> {
			platform.start_mouse_x = e.getSceneX();
			platform.start_mouse_y = e.getSceneY();
		});

		pane_play.setOnScroll(e -> {
			platform.scale += (e.getDeltaY() / 40.0) / 8;
			if (platform.scale <= platform.minimize)
				platform.scale = platform.minimize;
			if (platform.scale >= platform.maximize)
				platform.scale = platform.maximize;
		});
		pane_play.setOnMouseDragged(e -> {
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				double movex = platform.sensitivity * (e.getSceneX() - platform.start_mouse_x);
				double movey = platform.sensitivity * (e.getSceneY() - platform.start_mouse_y);
				platform.local_x -= movex;
				platform.local_y -= movey;
				platform.start_mouse_x = e.getSceneX();
				platform.start_mouse_y = e.getSceneY();
				if (platform.local_x + this.getChoicePane().getWidth() >= platform.max_x)
					platform.local_x = platform.max_x - this.getChoicePane().getWidth();
				if (platform.local_y + this.getChoicePane().getHeight() >= platform.max_y)
					platform.local_y = platform.max_y - this.getChoicePane().getHeight();
				if (platform.local_x <= platform.min_x)
					platform.local_x = platform.min_x;
				if (platform.local_y <= platform.min_y)
					platform.local_y = platform.min_y;
				platform.updateMouseCoordinate();
			}
		});
	}

	public PlayGUIController() {
		instance = this;
		platform = new PlayPlatform(instance);
	}

	@Override
	public ImageView getBackgroundImageView() {
		return imageview_background;
	}

	@Override
	public Pane getChoicePane() {
		return pane_play;
	}

	@Override
	public AbstractPlatform getPlatform() {
		return platform;
	}
}
