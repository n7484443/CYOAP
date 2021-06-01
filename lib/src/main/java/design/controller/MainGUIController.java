package design.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;

public class MainGUIController implements Initializable{
	@FXML
	private Button buttonTest;
	@FXML
	public static SplitPane MainGuiPane;
	@FXML
	public TextField TextInfo;
	@FXML
	public AnchorPane DescribePane;
	@FXML
	public ListView<String> var_field;
	
	public String dropped;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buttonTest.setOnMouseClicked(e->{
			System.out.println("test");
		});
		DescribePane.setOnDragOver(e->{
			if (e.getGestureSource() == null && e.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            e.consume();
		});
		DescribePane.setOnDragDropped(e->{
			Dragboard db = e.getDragboard();
			var success = false;
            if (db.hasFiles()) {
                dropped = (db.getFiles().toString());
                success = true;
            }
            /* let the source know whether the string was successfully 
             * transferred and used */
            e.setDropCompleted(success);
            e.consume();
		});
		var_field.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		for(int i = 0; i < 20; i++)var_field.getItems().addAll("var_1", "어떤 변수");
		
	}
}
