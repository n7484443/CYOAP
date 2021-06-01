package core;

import java.net.URL;
import java.nio.file.Paths;
import design.controller.MainGUIController;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class JavaFxMain extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			URL url = Paths.get("./src/main/resources/lib/design/MainDesign.fxml").toUri().toURL();
			Pane root = (Pane)FXMLLoader.load(url);
			Scene scene = new Scene(root, 960, 540);
			primaryStage.setTitle("CYOAP " + version);
			primaryStage.setScene(scene);
			
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
	public static String version = "0.0.9";
	
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
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
	}
	
	
}
