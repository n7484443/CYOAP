package design.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;

public class MainGUIController implements Initializable{
	@FXML
	private Button buttonTest;
	@FXML
	private SplitPane MainGuiPane;
	@FXML
	public TextField TextInfo;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buttonTest.setOnMouseClicked(e->{
			System.out.println("test");
		});
	}
	
}
