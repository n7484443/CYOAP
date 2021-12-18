package cyoap_main.controller;

import java.net.URL;
import java.util.ResourceBundle;

import cyoap_main.core.JavaFxMain;
import cyoap_main.controller.createGui.CreateGuiController;
import cyoap_main.util.LoadUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

public class StartGUIController implements Initializable {
	@FXML
	public Pane pane_play;
	@FXML
	public Pane pane_create;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		pane_play.setOnMouseClicked(e->{
			var directory = LoadUtil.loadFolder();
			if (directory != null) {
				JavaFxMain.controller = PlayGuiController.instance;
				JavaFxMain.instance.loadFiles(directory);
				JavaFxMain.instance.stage.setScene(JavaFxMain.instance.scene_play);
				PlayGuiController.instance.load();
			}
		});
		
		pane_create.setOnMouseClicked(e-> {
			var directory = LoadUtil.loadFolder();
			if (directory != null) {
				JavaFxMain.controller = CreateGuiController.instance;
				JavaFxMain.instance.loadFiles(directory);
				JavaFxMain.instance.stage.setScene(JavaFxMain.instance.scene_create);
				CreateGuiController.instance.load_shortcut();
			}
		});
	}
	
}
