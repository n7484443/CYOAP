package cyoap_main.design.controller.createGui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.fxmisc.richtext.StyleClassedTextArea;

import com.fasterxml.jackson.databind.ObjectMapper;

import cyoap_main.command.AbstractCommand;
import cyoap_main.command.CreateCommand;
import cyoap_main.command.DeleteCommand;
import cyoap_main.core.JavaFxMain;
import cyoap_main.design.ChoiceSet;
import cyoap_main.design.controller.PlatformGuiController;
import cyoap_main.design.platform.AbstractPlatform;
import cyoap_main.design.platform.MakePlatform;
import cyoap_main.grammer.Analyser;
import cyoap_main.grammer.VarData;
import cyoap_main.util.FlagUtil;
import cyoap_main.util.LoadUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;

public class CreateGuiController implements Initializable, PlatformGuiController {
	public static CreateGuiController instance;
	@FXML
	public AnchorPane pane_position;
	@FXML
	public SplitPane pane_mainGui;
	@FXML
	public AnchorPane pane_describe;
	@FXML
	public Button button_save;
	@FXML
	public Button button_next;
	
	public StyleClassedTextArea text_info;
	@FXML
	public TextField text_title;
	@FXML
	public TextField text_color;
	@FXML
	public ListView<String> view_var_field;
	@FXML
	public ListView<String> view_var_type;
	@FXML
	public ListView<String> view_command_timeline;
	@FXML
	public ImageView imageview_describe;
	@FXML
	public MenuItem menu_create;
	@FXML
	public MenuItem menu_delete;
	@FXML
	public MenuItem menu_connect;
	@FXML
	public MenuItem menu_saveAsImage;
	@FXML
	public ContextMenu menu_mouse;
	@FXML
	public TabPane tabpane_make;
	@FXML
	public Tab tab_describe;
	@FXML
	public Tab tab_position;
	@FXML
	public ImageView imageview_background;
	@FXML
	public RadioButton button_darkmode;
	@FXML
	public RadioButton button_outline;

	public List<File> dropped;

	public boolean isCommandListUpdated = false;
	public List<AbstractCommand> commandList = new ArrayList<AbstractCommand>();
	public static AbstractPlatform platform;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		text_info = new StyleClassedTextArea();
		pane_describe.getChildren().add(1,text_info);
		text_info.setPrefWidth(1012);
		text_info.setPrefHeight(285);
		text_info.setLayoutX(6);
		text_info.setLayoutY(327);
		
		button_save.setOnMouseClicked(e -> {
			save_describe_pane();
		});
		button_next.setOnMouseClicked(e -> {
			save_describe_pane();
			next();
		});
		pane_describe.setOnDragOver(e -> {
			if (e.getGestureSource() == null && e.getDragboard().hasFiles()) {
				/* allow for both copying and moving, whatever user chooses */
				e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
			e.consume();
		});
		pane_describe.setOnDragDropped(e -> {
			Dragboard db = e.getDragboard();
			var success = false;
			if (db.hasFiles()) {
				dropped = db.getFiles();
				success = true;
			}
			e.setDropCompleted(success);
			e.consume();
		});
		view_var_field.setOnMouseClicked(e -> {
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				if (e.getClickCount() == 2) {
					var varable = view_var_field.getSelectionModel().getSelectedIndex();
					if (varable >= 0) {
						var text = addTextIntoString(text_info.getText(), text_info.getAnchor(),
								text_info.getCaretPosition(), "{" + VarData.var_map.keySet().toArray()[varable] + "}");
						text_info.clear();
						text_info.appendText(text);
					}
				}
			}
		});
		view_var_field.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		pane_position.setOnDragOver(e -> {
			if (e.getGestureSource() == null && e.getDragboard().hasFiles()) {
				/* allow for both copying and moving, whatever user chooses */
				e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
			e.consume();
		});
		pane_position.setOnDragDropped(e -> {
			Dragboard db = e.getDragboard();
			var success = false;
			if (db.hasFiles()) {
				platform.image_file = db.getFiles().get(0);
				success = true;
			}
			e.setDropCompleted(success);
			e.consume();
		});
		pane_position.setOnMousePressed(e -> {
			platform.start_x = e.getSceneX();
			platform.start_y = e.getSceneY();
		});
		pane_position.setOnMouseReleased(e -> {
			if (e.getButton().equals(MouseButton.SECONDARY)) {
				menu_mouse.show(pane_position, e.getScreenX(), e.getScreenY());
			} else {
				menu_mouse.hide();
			}
		});

		pane_position.setOnScroll(e -> {
			platform.scale += (e.getDeltaY() / 40.0) / 2;
		});

		menu_mouse.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
			if (e.getButton() != MouseButton.PRIMARY) {
				e.consume();
			}
		});
		menu_mouse.setOnAction(e -> {
			var menu = (MenuItem) e.getTarget();
			Bounds boundsInScene = pane_describe.localToScene(pane_describe.getBoundsInLocal());
			var posx = (float) (platform.local_x + platform.start_x - boundsInScene.getMinX());
			var posy = (float) (platform.local_y + platform.start_y - boundsInScene.getMinY());
			if (menu == menu_create) {
				excuteCommand(new CreateCommand(posx, posy));
			} else if (menu == menu_delete) {
				if (nowMouseInDataSet != null && nowMouseInDataSet.check_intersect(nowMouseInDataSet, posx, posy)) {
					excuteCommand(new DeleteCommand(nowMouseInDataSet, platform.local_x, platform.local_x));
				}
			} else if (menu == menu_saveAsImage) {
				var v = PixelScaleGuiController.instance.anchorPane_slider;
				this.getPane().getChildren().add(v);
				v.setLayoutX(this.getPane().getWidth()/2f - boundsInScene.getMinX() + v.getWidth()/2f);
				v.setLayoutY(this.getPane().getHeight()/2f - boundsInScene.getMinY() + v.getHeight()/2f);
			}
		});

		pane_position.setOnMouseDragged(e -> {
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				double movex = sensitivity * (e.getSceneX() - platform.start_x);
				double movey = sensitivity * (e.getSceneY() - platform.start_y);
				platform.local_x -= movex;
				platform.local_y -= movey;
				platform.start_x = e.getSceneX();
				platform.start_y = e.getSceneY();
				if (platform.local_x + this.getPane().getWidth() >= platform.max_x)
					platform.local_x = platform.max_x - this.getPane().getWidth();
				if (platform.local_y + this.getPane().getHeight() >= platform.max_y)
					platform.local_y = platform.max_y - this.getPane().getHeight();
				if (platform.local_x <= platform.min_x)
					platform.local_x = platform.min_x;
				if (platform.local_y <= platform.min_y)
					platform.local_y = platform.min_y;
				platform.updateMouseCoordinate();
			}
		});

		view_var_type.getItems().addAll("&b | boolean", " \"\" | string", "floor | 내림", "ceil | 올림", "round | 반올림");
		view_var_type.setOnMouseClicked(e -> {
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				if (e.getClickCount() == 2) {
					var index = view_var_type.getSelectionModel().getSelectedIndex();
					var anchor = text_info.getAnchor();
					var caret = text_info.getCaretPosition();
					String text = switch (index) {
					case 0 -> addTextIntoString(text_info.getText(), anchor, caret, "&b");
					case 1 -> addTextIntoString(text_info.getText(), anchor, caret, "\" \"");
					case 2 -> addTextIntoString(text_info.getText(), anchor, caret, "floor( )");
					case 3 -> addTextIntoString(text_info.getText(), anchor, caret, "ceil( )");
					case 4 -> addTextIntoString(text_info.getText(), anchor, caret, "round( )");
					default -> "";
					};
					text_info.clear();
					text_info.appendText(text);
				}
			}
		});
	}

	public void capture(float pixelScale) {
		this.getPane().getChildren().remove(PixelScaleGuiController.instance.anchorPane_slider);
		
		var width_before = this.getPane().getWidth();
		var height_before = this.getPane().getHeight();
		var width_after = (platform.max_x - platform.min_x);
		var height_after = (platform.max_y - platform.min_y);

		this.getPane().resize(width_after, height_after);
		System.out.println(this.getPane().getWidth() + ":" + this.getPane().getHeight());

		platform.updatePositionAll(platform.local_x, platform.local_y);

		var spa = new SnapshotParameters();
		spa.setTransform(Transform.scale(pixelScale, pixelScale));
		spa.setViewport(new Rectangle2D(platform.min_x * pixelScale, platform.min_y * pixelScale,
				platform.max_x - platform.min_x, platform.max_y - platform.min_y));
		var writeableImage = this.getPane().snapshot(spa, new WritableImage(
				(int) (this.getPane().getWidth() * pixelScale), (int) (this.getPane().getHeight() * pixelScale)));

		BufferedImage tempImg = SwingFXUtils.fromFXImage(writeableImage, null);
		String imageType = "png";
		File f = new File(JavaFxMain.instance.directory.getAbsolutePath() + File.separator + "file." + imageType);
		try {
			ImageIO.write(tempImg, imageType, f);
		} catch (IOException e) {
			e.printStackTrace();
		}

		platform.updatePositionAll(-platform.local_x, -platform.local_y);
		this.getPane().resize(width_before, height_before);
	}

	public String addTextIntoString(String str, int anchor, int caret, String add) {
		if (str == null) {
			str = new String();
		}
		var before = str.substring(0, Math.min(anchor, caret));
		var after = str.substring(Math.max(anchor, caret));
		return before + add + after;
	}

	public double sensitivity = 1f;

	public int command_now = 0;

	public void save_describe_pane() {
		VarData.isUpdated = true;
		var text = Analyser.parser(text_info.getText());
		StringBuilder builder = new StringBuilder();
		if (text != null)
			text.stream().forEach(t -> builder.append(t));
		if (nowEditDataSet != null) {
			nowEditDataSet.string_title = text_title.getText();
			nowEditDataSet.string_describe = text_info.getText();
			if (image != null)
				nowEditDataSet.string_image_name = image.getUrl();
			nowEditDataSet.update();
			int t = 0x0067A3;
			try {
				t = Integer.valueOf(text_color.getText(), 16);
			} catch (NumberFormatException e) {
			}
			if (nowEditDataSet.color != t) {
				nowEditDataSet.updateColor(t);
			}
			if (FlagUtil.getFlag(nowEditDataSet.flag, ChoiceSet.flagPosition_selectable) != this.button_outline
					.isSelected()) {
				nowEditDataSet.flag = FlagUtil.setFlag(nowEditDataSet.flag, ChoiceSet.flagPosition_selectable,
						this.button_outline.isSelected());
				nowEditDataSet.updateFlag();
			}
		}
	}

	public void save_position_pane() {
		ObjectMapper objectMapper = new ObjectMapper();
		File dir = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/choiceSet");
		if (dir.exists()) {
			for (var f : dir.listFiles()) {
				f.delete();
			}
		} else {
			dir.mkdir();
		}
		try {
			for (var choiceSet : platform.choiceSetList) {
				OutputStreamWriter writer = new OutputStreamWriter(
						new FileOutputStream(dir.getAbsolutePath() + "/" + choiceSet.string_title + ".json"),
						StandardCharsets.UTF_8);
				objectMapper.writeValue(writer, choiceSet);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadToDataSet() {
		platform.clearNodeOnPanePosition();
		var path = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/choiceSet");
		if (!path.exists())
			return;
		var file_list = Stream.of(path.list()).filter(name -> name.endsWith(".json")).toList();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			for (var file : file_list) {
				System.out.println(file.toString());

				InputStreamReader writer = new InputStreamReader(
						new FileInputStream(path.getAbsolutePath() + "/" + file), StandardCharsets.UTF_8);

				var data = objectMapper.readValue(writer, ChoiceSet.class);
				data.setUp(this.pane_position);
				data.update();
				platform.choiceSetList.add(data);
				data.updateFlag();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (var v : platform.choiceSetList) {
			LoadUtil.setupChoiceSet(v);
		}
		for (var v : platform.choiceSetList) {
			LoadUtil.loadChoiceSetParents(v);
		}
	}

	public void next() {
		CreateGuiController.instance.changeTab(CreateGuiController.instance.tab_position);
		this.text_info.clear();
		this.text_title.setText("Title");
		this.text_color.setText("Color");
		this.button_outline.setSelected(false);
		;
		this.imageview_describe.setImage(null);
		this.image = null;
		this.dropped = null;
		this.nowEditDataSet = null;
	}

	public CreateGuiController() {
		CreateGuiController.instance = this;
		platform = new MakePlatform(instance);
	}

	public Image image = null;

	public void update() {
		if (dropped != null && image == null) {
			image = new Image(dropped.get(0).toURI().toString());
			imageview_describe.setImage(image);
		}

		if (VarData.isUpdated) {
			VarData.isUpdated = false;
			List<String> name_list = new ArrayList<String>();
			for (var key : VarData.var_map.keySet()) {
				var value = VarData.var_map.get(key);
				name_list.add(key + "  |  " + value.data + "  |  " + value.type.toString());
			}
			view_var_field.getItems().clear();
			view_var_field.getItems().setAll(name_list);
		}
		if (isCommandListUpdated) {
			isCommandListUpdated = false;
			List<String> name_list = new ArrayList<String>();
			for (var command : commandList) {
				name_list.add(command.getName());
			}
			view_command_timeline.getItems().clear();
			view_command_timeline.getItems().setAll(name_list);
			view_command_timeline.getSelectionModel().select(command_now);
		}
		platform.update();
	}

	public void render() {
		if (image != null) {
		}
	}

	public void loadFromDataSet(ChoiceSet dataSet) {
		text_title.setText(dataSet.string_title);
		text_info.clear();
		text_info.appendText(dataSet.string_describe);
		text_color.setText(Integer.toHexString(dataSet.color));
		if (dataSet.string_image_name != null && !dataSet.string_image_name.isEmpty()) {
			this.image = new Image(dataSet.string_image_name);
			imageview_describe.setImage(image);
		}
		button_outline.setSelected(FlagUtil.getFlag(dataSet.flag, ChoiceSet.flagPosition_selectable));
		dataSet.updateFlag();
	}

	public ChoiceSet nowEditDataSet;
	public ChoiceSet nowMouseInDataSet;

	public void changeTab(Tab tab) {
		tabpane_make.getSelectionModel().select(tab);
	}

	public void excuteCommand(AbstractCommand command) {
		command.excute();
		addCommand(command);
	}

	public void addCommand(AbstractCommand command) {
		for (int i = command_now + 1; i < commandList.size(); i++) {
			commandList.remove(i);
		}
		commandList.add(command);
		command_now = commandList.size() - 1;
		isCommandListUpdated = true;
	}

	public void undoCommand() {
		if (command_now >= 0) {
			var command = commandList.get(command_now);
			command.undo();
			command_now -= 1;
			isCommandListUpdated = true;
		}
	}

	public void redoCommand() {
		if (command_now < commandList.size() - 1) {
			command_now += 1;
			var command = commandList.get(command_now);
			command.excute();
			isCommandListUpdated = true;
		}
	}

	public void save_shortcut() {
		save_describe_pane();
		save_position_pane();
	}

	public void load_shortcut() {
		loadToDataSet();
	}

	public void undo_shortcut() {
		undoCommand();
	}

	public void redo_shortcut() {
		redoCommand();
	}

	@Override
	public ImageView getBackgroundImageView() {
		return imageview_background;
	}

	@Override
	public Pane getPane() {
		return pane_position;
	}

	@Override
	public AbstractPlatform getPlatform() {
		return platform;
	}
}
