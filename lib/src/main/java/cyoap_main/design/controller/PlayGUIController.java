package cyoap_main.design.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import cyoap_main.design.ChoiceSet;
import javafx.fxml.Initializable;

public class PlayGUIController implements Initializable {

	public List<ChoiceSet> choiceSetList = new ArrayList<ChoiceSet>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		load();
	}

	private void load() {
	
		
	}	
}
