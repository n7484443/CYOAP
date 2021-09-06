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
import cyoap_main.design.platform.AbstractPlatform;
import cyoap_main.design.platform.PlayPlatform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class PlayGUIController implements Initializable, PlatformGuiController {
	public static PlayGUIController instance;

	public AbstractPlatform platform;
	
	@FXML
	public AnchorPane pane_play;
	@FXML
	public ImageView imageview_background;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	public void load() {
		platform.clearNodeOnPanePosition();
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
		platform = new PlayPlatform(instance);
	}

	@Override
	public ImageView getBackgroundImageView() {
		return imageview_background;
	}

	@Override
	public Pane getPane() {
		return pane_play;
	}

	@Override
	public AbstractPlatform getPlatform() {
		return platform;
	}
}
