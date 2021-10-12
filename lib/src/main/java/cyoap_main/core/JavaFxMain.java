package cyoap_main.core;

import java.io.File;

import cyoap_main.design.controller.IPlatformGuiController;
import cyoap_main.design.controller.createGui.CreateGuiController;
import cyoap_main.grammer.VarData;
import cyoap_main.grammer.VarData.ValueType;
import cyoap_main.util.FontLoader;
import cyoap_main.util.LoadUtil;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class JavaFxMain extends Application {
	public static String version = "1.1.2-alpha";

	public static JavaFxMain instance;
	public Stage stage;
	public Scene scene_create;
	public Scene scene_start;
	public Scene scene_play;
	public static IPlatformGuiController controller = null;

	public int window_width = (int) (1920 / 2 * 1.5f);// 1440
	public int window_height = (int) (1080 / 2 * 1.5f);// 810

	@Override
	public void start(Stage primaryStage){
		try {
			System.out.println("javafx.runtime.version: " + System.getProperties().get("javafx.runtime.version"));
			instance = this;
			new LoadUtil();
			new FontLoader();
			stage = primaryStage;
			scene_create = new Scene(LoadUtil.instance.loadFXML("/lib/design/Design_Create.fxml"), window_width,
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
			scene_start = new Scene(LoadUtil.instance.loadFXML("/lib/design/Design_Start.fxml"), window_width,
					window_height);
			scene_play = new Scene(LoadUtil.instance.loadFXML("/lib/design/Design_Play.fxml"), window_width,
					window_height);
			scene_create.getStylesheets().add(LoadUtil.instance.loadCss("/lib/css/style.css"));

			var v = LoadUtil.instance.loadFXML("/lib/design/Design_Create_Slider.fxml");
			v.getStylesheets().add(LoadUtil.instance.loadCss("/lib/css/style.css"));
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
			LoadUtil.loadLatestVersion();
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

	public File directory;

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
