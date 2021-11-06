package cyoap_main.design.controller.createGui;

import java.net.URL;
import java.util.ResourceBundle;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    public MFXSlider slider_pixelScale;
    @FXML
    public MFXButton button_pixelScale;
    @FXML
    public AnchorPane anchorPane_slider;
    @FXML
    public MFXComboBox<String> combo_pixelScale;
    public ObservableList<String> observableList = FXCollections.observableArrayList("png");
    public float range_start = 1.0f;
    public float range_end = 4.0f;

    public PixelScaleGuiController() {
        instance = this;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchorPane_slider.setOpacity(1);
        anchorPane_slider.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(2), null)));
        anchorPane_slider.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2), BorderWidths.DEFAULT)));
        anchorPane_slider.toFront();
        slider_pixelScale.setMin(range_start);
        slider_pixelScale.setMax(range_end);
        slider_pixelScale.setDecimalPrecision(1);
        slider_pixelScale.setValue(2.5f);

        combo_pixelScale.setItems(observableList);
        combo_pixelScale.getSelectionModel().selectItem("png");

        button_pixelScale.setOnMouseClicked(e -> {
            CreateGuiController.instance.capture((float) slider_pixelScale.getValue(), combo_pixelScale.getSelectedValue());
            CreateGuiController.instance.anchorpane_create.getChildren().remove(PixelScaleGuiController.instance.anchorPane_slider);
        });
    }
}
