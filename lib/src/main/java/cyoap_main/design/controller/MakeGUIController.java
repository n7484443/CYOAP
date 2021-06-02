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
import com.fasterxml.jackson.databind.ObjectMapper;

import cyoap_main.core.JavaFxMain;
import cyoap_main.core.VarData;
import cyoap_main.design.DataSet;
import cyoap_main.util.Analyser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;

public class MakeGUIController implements Initializable {
	public static MakeGUIController instance;

	@FXML
	public Button button_save;
	@FXML
	public Button button_next;
	@FXML
	public static SplitPane MainGuiPane;
	@FXML
	public TextArea text_info;
	@FXML
	public TextField text_title;
	@FXML
	public AnchorPane DescribePane;
	@FXML
	public ListView<String> var_field;
	@FXML
	public ImageView imageView;

	public List<File> dropped;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		button_save.setOnMouseClicked(e -> {
			save();
		});
		button_next.setOnMouseClicked(e -> {
			save();
			next();
		});
		DescribePane.setOnDragOver(e -> {
			if (e.getGestureSource() == null && e.getDragboard().hasFiles()) {
				/* allow for both copying and moving, whatever user chooses */
				e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
			e.consume();
		});
		DescribePane.setOnDragDropped(e -> {
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
		var_field.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

	}
	public void save() {
		DataSet data = new DataSet(text_title.getText(), text_info.getText(), this.image);
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(JavaFxMain.instance.directory.getAbsolutePath() + "/" + text_title.getText() +".json"), StandardCharsets.UTF_8);
			objectMapper.writeValue(writer, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Analyser.analyse(data.describe);
	}
	
	public void load() {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			InputStreamReader writer = new InputStreamReader(new FileInputStream(JavaFxMain.instance.directory.getAbsolutePath() + "/" + text_title.getText() +".json"), StandardCharsets.UTF_8);
			var data = objectMapper.readValue(writer, DataSet.class);
			this.text_info.setText(data.title);
			this.text_title.setText(data.describe);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void next() {
		
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
		if(VarData.isUpdated) {
			VarData.isUpdated = false;
			List<String> name_list = new ArrayList<String>();
			for(var key : VarData.var_map.keySet()) {
				var value = VarData.var_map.get(key);
				name_list.add(key + "  |  " + value.data + "  |  " + value.type.toString());
			}
			var_field.getItems().clear();
			var_field.getItems().setAll(name_list);
		}
	}
	
	public void render() {
		if(image != null) {
		}
	}

}
