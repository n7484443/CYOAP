package cyoap_main.design.controller;

import cyoap_main.design.platform.AbstractPlatform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public interface PlatformGuiController {
	public ImageView getBackgroundImageView();
	public Pane getPane();
	public AbstractPlatform getPlatform();
}
