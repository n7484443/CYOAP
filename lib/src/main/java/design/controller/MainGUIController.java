package design.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class MainGUIController implements Initializable{
	@FXML
	private Button buttonTest;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buttonTest.setOnMouseClicked(e->{
			System.out.println("test");
		});
	}
	
}
