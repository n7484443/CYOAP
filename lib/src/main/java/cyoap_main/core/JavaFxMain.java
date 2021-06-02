package cyoap_main.core;

import java.io.File;

import cyoap_main.core.VarData.ValueType;
import cyoap_main.design.controller.MakeGUIController;
import cyoap_main.util.LoadUtil;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxMain extends Application {
	public static JavaFxMain instance;
	public Stage stage;
	public Scene scene_make;
	public Scene scene_start;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			instance = this;
			new LoadUtil();
			stage = primaryStage;
			scene_make = new Scene(LoadUtil.instance.loadFXML("/lib/design/Design_Make.fxml"), 960, 540);
			scene_start = new Scene(LoadUtil.instance.loadFXML("/lib/design/Design_Start.fxml"), 960, 540);
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
			stage.setResizable(false);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String version = "0.1.2";
	
	public void update(double time) {
		MakeGUIController.instance.update();
	}
	
	public void render() {
		MakeGUIController.instance.render();
	}
	public File directory;
	public void loadFiles(File directory) {
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
