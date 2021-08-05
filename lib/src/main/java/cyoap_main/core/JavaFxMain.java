package cyoap_main.core;

import java.io.File;

import cyoap_main.design.controller.MakeGUIController;
import cyoap_main.grammer.VarData;
import cyoap_main.grammer.VarData.ValueType;
import cyoap_main.util.LoadUtil;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxMain extends Application {
	public static String version = "0.2.0";
	
	public static JavaFxMain instance;
	public Stage stage;
	public Scene scene_make;
	public Scene scene_start;
	public Scene scene_play;
	
	public int window_width = 1920/2;
	public int window_height = 1080/2;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			instance = this;
			new LoadUtil();
			stage = primaryStage;
			scene_make = new Scene(LoadUtil.instance.loadFXML("/lib/design/Design_Make.fxml"), window_width, window_height);
			scene_start = new Scene(LoadUtil.instance.loadFXML("/lib/design/Design_Start.fxml"), window_width, window_height);
			scene_play = new Scene(LoadUtil.instance.loadFXML("/lib/design/Design_Play.fxml"), window_width, window_height);
			stage.setTitle("CYOAP " + version);
			stage.setScene(scene_start);
			
			final long startNanoTime = System.nanoTime();
			
			new AnimationTimer() {
				@Override
				public void handle(long now_NanoTime) {
					double time = (now_NanoTime-startNanoTime) / 1000000000.0;
					update(time);
					render();
				}
			}.start();
			stage.setResizable(true);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(double time) {
		MakeGUIController.instance.update();
	}
	
	public void render() {
		MakeGUIController.instance.render();
	}
	public File directory;
	public void loadFiles(File directory) {
		if(directory == null)return;
		this.directory = directory;
		var hasConfig = false;
		for(var file : directory.listFiles()) {
			if(file.getName().equals("setting.json")) {
				hasConfig = true;
			}
		}
		if(!hasConfig) {
			
		}
	}

	public static void main(String[] args) {
		System.out.println("Version|" + version);
		launch(args);
	}
	@Override
	public void init() throws Exception {
		super.init();
		new VarData();
		var x = new ValueType(123f);
		var y = new ValueType(12);
		VarData.setValue("dataTest", x);
		VarData.setValue("dataTest2", y);
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
	}
}
