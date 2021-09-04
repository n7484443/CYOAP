package cyoap_main.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import cyoap_main.core.JavaFxMain;
import cyoap_main.design.ChoiceSet;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;

public class LoadUtil {
	public static LoadUtil instance;

	public LoadUtil() {
		instance = this;
	}

	public Pane loadFXML(String path) throws IOException {
		URL url = LoadUtil.class.getResource(path);
		Pane root = (Pane) FXMLLoader.load(url);
		return root;
	}

	public static File loadFolder() {
		DirectoryChooser chooser = new DirectoryChooser();
		return chooser.showDialog(JavaFxMain.instance.stage);
	}

	public static void setupChoiceSet(ChoiceSet choiceSet) {
		for (var c : choiceSet.choiceSet_child) {
			if (!c.choiceSet_child.isEmpty()) {
				setupChoiceSet(c);
			} else {
				c.guiComponent.setUp(c);
			}
		}
	}

	public static void loadChoiceSetParents(ChoiceSet choiceSet) {
		for (var c : choiceSet.choiceSet_child) {
			if (!c.choiceSet_child.isEmpty()) {
				loadChoiceSetParents(c);
			} else {
				c.choiceSet_parent = choiceSet;
				choiceSet.guiComponent.hbox.getChildren().add(c.getAnchorPane());
			}
		}
	}
}
