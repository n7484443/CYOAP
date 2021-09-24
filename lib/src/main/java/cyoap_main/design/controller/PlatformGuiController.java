package cyoap_main.design.controller;

import cyoap_main.design.platform.AbstractPlatform;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public interface PlatformGuiController extends Initializable{
	public ImageView getBackgroundImageView();
	public Pane getChoicePane();
	public AbstractPlatform getPlatform();
}
