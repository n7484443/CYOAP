package cyoap_main.design.controller.createGui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.converter.NumberStringConverter;

public class PixelScaleGuiController implements Initializable {
	public static PixelScaleGuiController instance;

	@FXML
	public Slider slider_pixelScale;
	@FXML
	public Button button_pixelScale;
	@FXML
	public TextField textField_pixelScale;
	@FXML
	public AnchorPane anchorPane_slider;

	public PixelScaleGuiController() {
		instance = this;
	}

	public float range_start = 1.0f;
	public float range_end = 4.0f;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		anchorPane_slider.setOpacity(1);
		anchorPane_slider.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(2), null)));
		anchorPane_slider.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2), BorderWidths.DEFAULT)));
		anchorPane_slider.toFront();
		slider_pixelScale.setMin(range_start);
		slider_pixelScale.setMax(range_end);
		slider_pixelScale.setValue(2.5f);

		textField_pixelScale.setText(Float.toString(4.0f));
		
		textField_pixelScale.textProperty().bindBidirectional(slider_pixelScale.valueProperty(), new NumberStringConverter());

		button_pixelScale.setOnMouseClicked(e ->
			CreateGuiController.instance.capture((float) slider_pixelScale.getValue()));
	}

}
