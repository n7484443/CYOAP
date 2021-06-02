package design.controller;

import java.net.URL;
import java.util.ResourceBundle;

import core.JavaFxMain;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import util.LoadUtil;

public class StartGUIController implements Initializable {
	@FXML
	public Pane pane_play;
	@FXML
	public Pane pane_make;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		pane_play.setOnMouseClicked(e->{
		});
		
		pane_make.setOnMouseClicked(e->{
			var directory = LoadUtil.loadFolder();
			JavaFxMain.instance.loadFiles(directory);
			JavaFxMain.instance.stage.setScene(JavaFxMain.instance.scene_make);
		});
	}
	
}
