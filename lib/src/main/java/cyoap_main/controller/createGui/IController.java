package cyoap_main.controller.createGui;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public interface IController extends Initializable {
    @Override
    default void initialize(URL location, ResourceBundle resources) {
        beforeInit();
        nodeInit();
        eventInit();
        localizationInit();
        afterInit();
    }

    default void afterInit() {

    }

    default void beforeInit() {

    }

    default void localizationInit() {

    }

    public void nodeInit();

    public void eventInit();
}
