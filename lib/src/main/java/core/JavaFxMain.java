package core;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import core.VarData.ValueType;
import design.controller.MainGUIController;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class JavaFxMain extends Application {
	public Pane loadFXML(String path) throws IOException {
		URL url = Paths.get(path).toUri().toURL();
		Pane root = (Pane)FXMLLoader.load(url);
		return root;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Scene scene_main = new Scene(loadFXML("./src/main/resources/lib/design/MainDesign.fxml"), 960, 540);
			Scene scene_start = new Scene(loadFXML("./src/main/resources/lib/design/StartDesign.fxml"), 960, 540);
			primaryStage.setTitle("CYOAP " + version);
			primaryStage.setScene(scene_main);
			
			final long startNanoTime = System.nanoTime();
			
			new AnimationTimer() {
				@Override
				public void handle(long now_NanoTime) {
					double time = (now_NanoTime-startNanoTime) / 1000000000.0;
					update(time);
					render();
				}
			}.start();
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String version = "0.0.11";
	
	public void update(double time) {
		MainGUIController.instance.update();
	}
	
	public void render() {
		MainGUIController.instance.render();
	}

	public static void main(String[] args) {
		System.out.println("Version|" + version);
		launch(args);
	}
	@Override
	public void init() throws Exception {
		super.init();
		new VarData();
		var x = new ValueType(VarData.types.ints, "dataTest");
		x.setData(55);
		VarData.var_value.add(x);
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
	}
	
	
}
