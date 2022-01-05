package cyoap_main.core;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import cyoap_main.controller.IGuiController;
import cyoap_main.controller.createGui.CreateGuiController;
import cyoap_main.grammer.VariableDataBase;
import cyoap_main.grammer.ValueType;
import cyoap_main.util.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.concurrent.*;

public class JavaFxMain extends Application {
	public static String version;

	public static JavaFxMain instance;
	public Stage stage;
	public Scene scene_create;
	public Scene scene_start;
	public Scene scene_play;

	public static IGuiController controller = null;
	public File directory;

	public int window_width = (int) (1920 / 2 * 1.5f);// 1440
	public int window_height = (int) (1080 / 2 * 1.5f);// 810

	@Override
	public void start(Stage primaryStage) {
		try {
			instance = this;
			Properties property_version = new Properties();
			var loader = JavaFxMain.class.getClassLoader().getResourceAsStream("lib/version.properties");
			property_version.load(loader);
			version = property_version.getProperty("VERSION_NAME");

			System.out.println("Version|" + version);
			System.out.println("javafx.runtime.version: " + System.getProperties().get("javafx.runtime.version"));

			new FontLoader();

			LocalizationUtil.getInstance().loadLocalization();

			stage = primaryStage;
			scene_create = new Scene(LoadUtil.getInstance().loadFXML("/lib/design/Design_Create.fxml"), window_width,
					window_height);
			scene_create.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
				KeyCombination comb_save = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
				KeyCombination comb_load = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
				KeyCombination comb_undo = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
				KeyCombination comb_redo = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN,
						KeyCombination.SHIFT_DOWN);
				KeyCombination comb_reset = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
				if (comb_save.match(e)) {
					System.out.println("Save ShortCut");
					CreateGuiController.instance.save_shortcut();
				} else if (comb_load.match(e)) {
					System.out.println("Load ShortCut");
					CreateGuiController.instance.load_shortcut();
				} else if (comb_undo.match(e)) {
					System.out.println("Undo ShortCut");
					CreateGuiController.instance.undo_shortcut();
				} else if (comb_redo.match(e)) {
					System.out.println("Redo ShortCut");
					CreateGuiController.instance.redo_shortcut();
				} else if(comb_reset.match(e)){
					System.out.println("Reset ShortCut");
					CreateGuiController.instance.getPlatform().scale = 1.f;
				}
			});
			scene_start = new Scene(LoadUtil.getInstance().loadFXML("/lib/design/Design_Start.fxml"), window_width,
					window_height);
			scene_play = new Scene(LoadUtil.getInstance().loadFXML("/lib/design/Design_Play.fxml"), window_width,
					window_height);
			scene_create.getStylesheets().add(LoadUtil.getInstance().loadCss("/lib/css/style.css"));

			var v = LoadUtil.getInstance().loadFXML("/lib/design/Design_Create_Slider.fxml");
			v.getStylesheets().add(LoadUtil.getInstance().loadCss("/lib/css/text.css"));
			stage.setTitle("CYOAP " + version);
			stage.setScene(scene_start);

			final long startNanoTime = System.nanoTime();

			new AnimationTimer() {
				@Override
				public void handle(long now_NanoTime) {
					double time = (now_NanoTime - startNanoTime) / 1000000000.0;
					update(time);
					render(time);
				}
			}.start();
			stage.setResizable(true);
			stage.show();
			var future = CompletableFuture.supplyAsync(LoadUtil::loadLatestVersion);
			future.thenAccept(result -> {
				if (result == 1) {
					System.out.println("update complete! Please restart program.");
					stage.setTitle("CYOAP " + version + " | " + LocalizationUtil.getInstance().getLocalization("system.update_complete"));
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(double time) {
		if(controller != null)controller.update();
	}

	public void render(double time) {
		if (controller != null && controller.getCanvas() != null) {
			var gc = controller.getCanvas().getGraphicsContext2D();
			gc.clearRect(0, 0, controller.getCanvas().getWidth(), controller.getCanvas().getHeight());
			controller.getPlatform().render(gc, time);
		}
	}

	public void loadFiles(File directory) {
		this.directory = directory;
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init() throws Exception {
		var x = new ValueType(123f);
		var y = new ValueType(12);
		VariableDataBase.getInstance().setValue("dataTest", x);
		VariableDataBase.getInstance().setValue("dataTest2", y);
	}

	@Override
	public void stop() throws Exception {
	}
}
