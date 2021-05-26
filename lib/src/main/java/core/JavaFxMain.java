package core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class JavaFxMain extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 400, 400);
			primaryStage.setTitle(version);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String version = "0.0.4";

	public static void main(String[] args) {
		System.out.println("Version|" + version);
		launch(args);
	}
	
}
