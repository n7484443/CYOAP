package cyoap_main.design.controller;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import cyoap_main.core.JavaFxMain;
import cyoap_main.design.ChoiceSet;
import cyoap_main.grammer.Analyser;
import cyoap_main.grammer.VarData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class MakeGUIController implements Initializable {
	public static MakeGUIController instance;
	@FXML
	public AnchorPane pane_position;
	@FXML
	public static SplitPane pane_mainGui;
	@FXML
	public AnchorPane pane_describe;
	@FXML
	public Button button_save;
	@FXML
	public Button button_next;
	@FXML
	public TextArea text_info;
	@FXML
	public TextField text_title;
	@FXML
	public ListView<String> var_field;
	@FXML
	public ListView<String> var_type;
	@FXML
	public ImageView imageView;
	@FXML
	public MenuItem menu_create;
	@FXML
	public MenuItem menu_delete;
	@FXML
	public MenuItem menu_connect;
	@FXML
	public ContextMenu menu_mouse;
	@FXML
	public TabPane tabpane_make;
	@FXML
	public Tab tab_describe;
	@FXML
	public Tab tab_position;

	public List<File> dropped;

	public List<ChoiceSet> choiceSetList = new ArrayList<ChoiceSet>();

	public double local_x = 0;
	public double local_y = 0;
	public double move_x = 0;
	public double move_y = 0;
	public double start_x = 0;
	public double start_y = 0;
	public int min_x = -500;
	public int min_y = -500;
	public int max_x = 500;
	public int max_y = 500;

	public float scale = 1.0f;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
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
			/*
			 * let the source know whether the string was successfully transferred and used
			 */
			e.setDropCompleted(success);
			e.consume();
		});
		var_field.setOnMouseClicked(e -> {
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				if (e.getClickCount() == 2) {
					var varable = var_field.getSelectionModel().getSelectedIndex();
					if (varable >= 0) {
						var text = addTextIntoString(text_info.getText(), text_info.getAnchor(),
								text_info.getCaretPosition(), "{" + VarData.var_map.keySet().toArray()[varable] + "}");
						text_info.setText(text);
					}
				}
			}
		});
		var_field.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		pane_position.setOnMousePressed(e -> {
			start_x = e.getSceneX();
			start_y = e.getSceneY();
		});
		pane_position.setOnMouseReleased(e -> {
			if (e.getButton().equals(MouseButton.SECONDARY)) {
				menu_mouse.show(pane_position, e.getScreenX(), e.getScreenY());
			} else {
				menu_mouse.hide();
			}
		});

		pane_position.setOnScroll(e -> {
			scale += (e.getDeltaY() / 40.0) / 2;
		});
		menu_create.setOnAction(e -> {
			Bounds boundsInScene = pane_describe.localToScene(pane_describe.getBoundsInLocal());
			makeNewComp(pane_position, local_x + start_x - boundsInScene.getMinX(),
					local_y + start_y - boundsInScene.getMinY(), -local_x, -local_y);
		});

		menu_delete.setOnAction(e -> {
			Bounds boundsInScene = pane_describe.localToScene(pane_describe.getBoundsInLocal());
			if (nowMouseInDataSet != null && nowMouseInDataSet.check_intersect(nowMouseInDataSet,
					local_x + start_x - boundsInScene.getMinX(), local_y + start_y - boundsInScene.getMinY())) {
				pane_position.getChildren().remove(nowMouseInDataSet.getAnchorPane());
				nowMouseInDataSet = null;
			}
		});

		pane_position.setOnMouseDragged(e -> {
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				double movex = sensitivity * (e.getSceneX() - start_x);
				double movey = sensitivity * (e.getSceneY() - start_y);
				local_x -= movex;
				local_y -= movey;
				start_x = e.getSceneX();
				start_y = e.getSceneY();
				if (local_x >= max_x)
					local_x = max_x;
				if (local_y >= max_y)
					local_y = max_y;
				if (local_x <= min_x)
					local_x = min_x;
				if (local_y <= min_y)
					local_y = min_y;

				choiceSetList.forEach(d -> d.updatePos(-local_x, -local_y));
			}
		});

		var_type.getItems().addAll("&b | boolean", " \"\" | string", "floor | 내림", "ceil | 올림", "round | 반올림");
		var_type.setOnMouseClicked(e -> {
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				if (e.getClickCount() == 2) {
					var index = var_type.getSelectionModel().getSelectedIndex();
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
					text_info.setText(text);
				}
			}
		});
	}

	public void makeNewComp(Pane pane, double posx, double posy, double updatex, double updatey) {
		ChoiceSet dataSet = new ChoiceSet(posx, posy);
		dataSet.setUp(pane);
		choiceSetList.add(dataSet);
		dataSet.updatePos(updatex, updatey);
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

	public void save_shortcut() {
		save_describe_pane();
		save_position_pane();
	}

	public void load_shortcut() {
		loadToDataSet();
	}

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
		}
	}

	public void save_position_pane() {
		ObjectMapper objectMapper = new ObjectMapper();
		File dir = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/choiceSet");
		if (!dir.exists()) {
			dir.mkdir();
		}
		try {
			for (var choiceSet : choiceSetList) {
				OutputStreamWriter writer = new OutputStreamWriter(
						new FileOutputStream(dir.getAbsolutePath() + "/" + text_title.getText() + ".json"),
						StandardCharsets.UTF_8);
				objectMapper.writeValue(writer, choiceSet);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void clearNodeOnPanePosition() {
		this.choiceSetList.clear();
		this.pane_position.getChildren().clear();
	}

	public void loadToDataSet() {
		clearNodeOnPanePosition();
		var path = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/choiceSet");
		var file_list = Stream.of(path.list()).filter(name -> name.endsWith(".json")).toList();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			for (var file : file_list) {
				System.out.println(file.toString());

				InputStreamReader writer = new InputStreamReader(
						new FileInputStream(path.getAbsolutePath() + "/" + file), StandardCharsets.UTF_8);

				var data = objectMapper.readValue(writer, ChoiceSet.class);
				data.setUp(this.pane_position);
				this.choiceSetList.add(data);
				this.text_info.setText(data.string_title);
				this.text_title.setText(data.string_describe);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void next() {
		MakeGUIController.instance.changeTab(MakeGUIController.instance.tab_position);
		this.text_info.setText(null);
		this.text_title.setText(null);
		this.imageView.setImage(null);
		this.image = null;
		this.dropped = null;
	}

	public MakeGUIController() {
		MakeGUIController.instance = this;
	}

	public Image image = null;

	public void update() {
		if (dropped != null && image == null) {
			image = new Image(dropped.get(0).toURI().toString());
			imageView.setImage(image);
		}
		if (VarData.isUpdated) {
			VarData.isUpdated = false;
			List<String> name_list = new ArrayList<String>();
			for (var key : VarData.var_map.keySet()) {
				var value = VarData.var_map.get(key);
				name_list.add(key + "  |  " + value.data + "  |  " + value.type.toString());
			}
			var_field.getItems().clear();
			var_field.getItems().setAll(name_list);
		}
	}

	public void render() {
		if (image != null) {
		}
	}

	public void loadFromDataSet(ChoiceSet dataSet) {
		this.text_title.setText(dataSet.string_title);
		this.text_info.setText(dataSet.string_describe);
		if (dataSet.string_image_name != null && !dataSet.string_image_name.isEmpty())
			this.image = new Image(dataSet.string_image_name);
	}

	public ChoiceSet nowEditDataSet;
	public ChoiceSet nowMouseInDataSet;

	public void changeTab(Tab tab) {
		tabpane_make.getSelectionModel().select(tab);
	}

}
