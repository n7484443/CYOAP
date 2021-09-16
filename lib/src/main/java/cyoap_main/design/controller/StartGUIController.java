package cyoap_main.design.controller;

import java.net.URL;
import java.util.ResourceBundle;

import cyoap_main.core.JavaFxMain;
import cyoap_main.design.controller.createGui.CreateGuiController;
import cyoap_main.util.LoadUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

public class StartGUIController implements Initializable {
	@FXML
	public Pane pane_play;
	@FXML
	public Pane pane_make;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		pane_play.setOnMouseClicked(e->{
			var directory = LoadUtil.loadFolder();
			JavaFxMain.instance.loadFiles(directory);
			JavaFxMain.instance.stage.setScene(JavaFxMain.instance.scene_play);
			PlayGUIController.instance.load();
		});
		
		pane_make.setOnMouseClicked(e->{
			var directory = LoadUtil.loadFolder();
			JavaFxMain.instance.loadFiles(directory);
			JavaFxMain.instance.stage.setScene(JavaFxMain.instance.scene_make);
			CreateGuiController.instance.load_shortcut();
		});
	}
	
}
