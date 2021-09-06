package cyoap_main.core;

import java.io.File;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import cyoap_main.design.controller.MakeGUIController;
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
	public static String version = "0.2.8";

	public static JavaFxMain instance;
	public Stage stage;
	public Scene scene_make;
	public Scene scene_start;
	public Scene scene_play;

	public int window_width = (int) (1920 / 2 * 1.25f);// 1200
	public int window_height = (int) (1080 / 2 * 1.25f);// 675

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			instance = this;
			new LoadUtil();
			new FontLoader();
			stage = primaryStage;
			scene_make = new Scene(LoadUtil.instance.loadFXML("/lib/design/Design_Make.fxml"), window_width,
					window_height);
			scene_make.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
				KeyCombination comb_save = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
				KeyCombination comb_load = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
				KeyCombination comb_undo = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
				KeyCombination comb_redo = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN,
						KeyCombination.SHIFT_DOWN);
				if (comb_save.match(e)) {
					System.out.println("Save ShortCut");
					MakeGUIController.instance.save_shortcut();
				} else if (comb_load.match(e)) {
					System.out.println("Load ShortCut");
					MakeGUIController.instance.load_shortcut();
				} else if (comb_undo.match(e)) {
					System.out.println("Undo ShortCut");
					MakeGUIController.instance.undo_shortcut();
				} else if (comb_redo.match(e)) {
					System.out.println("Redo ShortCut");
					MakeGUIController.instance.redo_shortcut();
				}
			});
			scene_start = new Scene(LoadUtil.instance.loadFXML("/lib/design/Design_Start.fxml"), window_width,
					window_height);
			scene_play = new Scene(LoadUtil.instance.loadFXML("/lib/design/Design_Play.fxml"), window_width,
					window_height);
			stage.setTitle("CYOAP " + version);
			stage.setScene(scene_start);

			final long startNanoTime = System.nanoTime();

			new AnimationTimer() {
				@Override
				public void handle(long now_NanoTime) {
					double time = (now_NanoTime - startNanoTime) / 1000000000.0;
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
		if (directory == null)
			return;
		this.directory = directory;
		var hasConfig = false;
		for (var file : directory.listFiles()) {
			if (file.getName().equals("setting.json")) {
				hasConfig = true;
			}
		}
		if (!hasConfig) {

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

		Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("JPEG");
		while (readers.hasNext()) {
			System.out.println("reader: " + readers.next());
		}
		readers = ImageIO.getImageReadersByFormatName("webp");
		while (readers.hasNext()) {
			System.out.println("reader: " + readers.next());
		}
		ImageIO.scanForPlugins();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
	}
}
