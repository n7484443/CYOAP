package cyoap_main.design.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;

import cyoap_main.core.JavaFxMain;
import cyoap_main.design.choice.ChoiceSet;
import cyoap_main.design.platform.AbstractPlatform;
import cyoap_main.util.LoadUtil;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public interface IPlatformGuiController extends Initializable{
	public ImageView getBackgroundImageView();
	public Pane getChoicePane();
	public AbstractPlatform getPlatform();
	public Canvas getCanvas();
	public void update();
	public boolean isEditable();
	public default void load() {
		getPlatform().clearNodeOnPanePosition();
		var path = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/choiceSet");
		if (!path.exists())
			return;
		var file_list = Stream.of(path.list()).filter(name -> name.endsWith(".json")).toList();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			for (var file : file_list) {
				InputStreamReader writer = new InputStreamReader(
						new FileInputStream(path.getAbsolutePath() + "/" + file), StandardCharsets.UTF_8);

				var data = objectMapper.readValue(writer, ChoiceSet.class);
				data.setUp(getChoicePane());
				data.update();
				getPlatform().choiceSetList.add(data);
				data.updateFlag();
				LoadUtil.loadSegment(data.guiComponent.area, data.segmentList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (var v : getPlatform().choiceSetList) {
			LoadUtil.setupChoiceSet(v);
		}
		for (var v : getPlatform().choiceSetList) {
			LoadUtil.loadChoiceSetParents(v);
		}
	}
}
