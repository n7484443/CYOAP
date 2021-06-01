package design.controller;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import core.VarData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;

public class MainGUIController implements Initializable {
	public static MainGUIController instance;

	@FXML
	private Button buttonTest;
	@FXML
	public static SplitPane MainGuiPane;
	@FXML
	public TextArea TextInfo;
	@FXML
	public AnchorPane DescribePane;
	@FXML
	public ListView<String> var_field;
	@FXML
	public ImageView imageView;

	public List<File> dropped;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buttonTest.setOnMouseClicked(e -> {
			System.out.println("test");
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

	public MainGUIController() {
		MainGUIController.instance = this;
	}
	
	public Image image = null;
	public void update() {
		if (dropped != null && image == null) {
			image = new Image(dropped.get(0).toURI().toString());
			imageView.setImage(image);
		}
		if(VarData.instance.isUpdated) {
			VarData.instance.isUpdated = false;
			List<String> name_list = VarData.var_value.stream().map(t->t.name).collect(Collectors.toList());
			var_field.getItems().clear();
			var_field.getItems().addAll(name_list);
		}
	}
	
	public void render() {
		if(image != null) {
		}
	}

}
