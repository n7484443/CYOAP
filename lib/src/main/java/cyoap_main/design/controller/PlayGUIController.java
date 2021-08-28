package cyoap_main.design.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import com.fasterxml.jackson.databind.ObjectMapper;
import cyoap_main.core.JavaFxMain;
import cyoap_main.design.ChoiceSet;
import cyoap_main.unit.AbstractPlatform;
import cyoap_main.unit.PlayPlatform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class PlayGUIController implements Initializable {
	public static PlayGUIController instance;

	public AbstractPlatform platform = new PlayPlatform();
	
	@FXML
	public AnchorPane pane_play;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	private void load() {
		platform.clearNodeOnPanePosition(this.pane_play);
		var path = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/choiceSet");
		var file_list = Stream.of(path.list()).filter(name -> name.endsWith(".json")).toList();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			for (var file : file_list) {
				System.out.println(file.toString());

				InputStreamReader writer = new InputStreamReader(
						new FileInputStream(path.getAbsolutePath() + "/" + file), StandardCharsets.UTF_8);

				var data = objectMapper.readValue(writer, ChoiceSet.class);
				data.setUp(this.pane_play);
				data.update();
				this.platform.choiceSetList.add(data);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}	
	public PlayGUIController() {
		instance = this;
	}
}
