package util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import core.JavaFxMain;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;

public class LoadUtil {
	public static Pane loadFXML(String path) throws IOException {
		URL url = Paths.get(path).toUri().toURL();
		Pane root = (Pane)FXMLLoader.load(url);
		return root;
	}
	
	public static File loadFolder() {
		DirectoryChooser chooser = new DirectoryChooser();
		return chooser.showDialog(JavaFxMain.instance.stage);
	}
}
