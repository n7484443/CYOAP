package cyoap_main.controller.createGui;

import cyoap_main.core.JavaFxMain;
import cyoap_main.design.choice.ChoiceSet;
import cyoap_main.design.node_extension.ImageCell;
import cyoap_main.util.FontLoader;
import cyoap_main.util.LoadUtil;
import cyoap_main.util.SizeUtil;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import org.fxmisc.richtext.InlineCssTextArea;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DescribeGuiController implements IController {
    @FXML
    public GridPane gridpane_describe;
    @FXML
    public BorderPane pane_text_editor;
    @FXML
    public HBox hbox_setting;
    @FXML
    public MFXTextField text_title;

    @FXML
    public ColorPicker colorpicker;
    @FXML
    public ImageView image_text_bold;
    @FXML
    public ImageView image_text_italic;
    @FXML
    public ImageView image_text_underline;
    @FXML
    public ColorPicker colorpicker_text_editor;

    @FXML
    public Button button_save;
    @FXML
    public Button button_next;

    public ImageCell imagecell_describe = new ImageCell();

    @FXML
    public MFXTextField text_round;

    @FXML
    public MFXComboBox<Label> combo_text_font;
    @FXML
    public MFXComboBox<String> combo_text_size;
    public InlineCssTextArea text_editor = new InlineCssTextArea();

    @FXML
    public MFXRadioButton button_outline;
    @FXML
    public MFXRadioButton button_horizon;
    @FXML
    public MFXRadioButton button_emptyimage;

    public List<MFXRadioButton> button_list = new ArrayList<>();

    @Override
    public void nodeInit() {
        try {
            text_editor.setWrapText(true);
            text_editor.getStylesheets().add(LoadUtil.getInstance().loadCss("/lib/css/text_editor.css"));
            text_editor.getStyleClass().add("text-editor");
            text_editor.setStyle("-color-text: white ;");
        } catch (IOException e) {
            e.printStackTrace();
        }

        colorpicker.getStyleClass().add("button");

        BorderPane.setMargin(text_editor, new Insets(2.5f, 0, 0, 0));
        GridPane.setMargin(imagecell_describe, new Insets(5));
        hbox_setting.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DASHED, new CornerRadii(2), new BorderWidths(2), null)));

        gridpane_describe.add(imagecell_describe, 0, 1, 2, 1);

        colorpicker_text_editor.getStyleClass().add("button");
        pane_text_editor.setCenter(text_editor);

        button_list.add(button_outline);
        button_list.add(button_horizon);
        button_list.add(button_emptyimage);
    }

    public void eventInit() {
        image_text_bold.setOnMouseClicked(e -> {
            var range = text_editor.getSelection();
            var v = getTextCss(range, "-fx-font-weight");
            if (v == null || v.equals("error")) {
                editTextCss(range, "-fx-font-weight", "bold");
            } else {
                removeTextCss(range, "-fx-font-weight");
            }
        });
        image_text_italic.setOnMouseClicked(e -> {
            var range = text_editor.getSelection();
            var v = getTextCss(range, "-fx-font-style");
            if (v == null || v.equals("error")) {
                editTextCss(range, "-fx-font-style", "italic");
            } else {
                removeTextCss(range, "-fx-font-style");
            }
        });
        image_text_underline.setOnMouseClicked(e -> {
            var range = text_editor.getSelection();
            var v = getTextCss(range, "-fx-underline");
            if (v == null || v.equals("error")) {
                editTextCss(range, "-fx-underline", "true");
            } else {
                removeTextCss(range, "-fx-underline");
            }
        });
        colorpicker_text_editor.valueProperty().addListener(e -> {
            var range = text_editor.getSelection();
            editTextCss(range, "-color-text", colorpicker_text_editor.getValue().toString().replace("0x", "#"));
        });
        gridpane_describe.setOnDragOver(e -> {
            if (e.getGestureSource() == null && e.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            e.consume();
        });
        gridpane_describe.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            var success = false;
            if (db.hasFiles()) {
                CreateGuiController.instance.dropped = db.getFiles();
                CreateGuiController.instance.isImageChanged = true;
                success = true;
            }
            e.setDropCompleted(success);
            e.consume();
        });
        combo_text_font.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Label> observ,
                                                                                Label oldVal, Label newVal) -> {
            if (newVal != null) {
                var range = text_editor.getSelection();
                editTextCss(range, "-fx-font-family", newVal.getText());
            }
        });
        combo_text_size.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observ,
                                                                                String oldVal, String newVal) -> {
            if (newVal != null) {
                var range = text_editor.getSelection();
                editTextCss(range, "-fx-font-size", newVal + "pt");
            }
        });
        imagecell_describe.setOnMouseMoved(e -> {
            var base_width = (imagecell_describe.getWidth() - imagecell_describe.getRealWidth()) / 2f;
            var base_height = (imagecell_describe.getHeight() - imagecell_describe.getRealHeight()) / 2f;
            SizeUtil.setCursorRound(e.getX() - base_width, e.getY() - base_height, imagecell_describe.getRealWidth(), imagecell_describe.getRealHeight(), 15, imagecell_describe.round.get());
        });
        imagecell_describe.setOnMouseExited(e -> {
            JavaFxMain.instance.scene_create.setCursor(Cursor.DEFAULT);
        });
        text_round.textProperty().bindBidirectional(imagecell_describe.round, new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object.toString();
            }

            @Override
            public Integer fromString(String string) {
                try {
                    var round = Integer.valueOf(string);
                    if (round <= 0) {
                        return 0;
                    }
                    return round;
                } catch (Exception e) {
                    return 0;
                }
            }
        });

        button_save.setOnMouseClicked(e -> CreateGuiController.instance.save_describe_pane());
        button_next.setOnMouseClicked(e -> {
            CreateGuiController.instance.save_describe_pane();
            CreateGuiController.instance.next();
        });
    }

    public String getTextCss(IndexRange range, String css) {
        if (range.getStart() == range.getEnd()) return "error";

        String value = null;
        for (int i = range.getStart(); i < range.getEnd(); i++) {
            StringBuilder builder = new StringBuilder();
            var cssCombined = text_editor.getStyleOfChar(i);
            if (cssCombined.contains(css)) {
                var pos = cssCombined.indexOf(css);
                var after_pos = cssCombined.indexOf(";", pos + 1);

                var data = cssCombined.substring(pos + 1, after_pos);
                if (value == null) value = data;
                else if (!data.contains(value)) {
                    return "error";
                }
            }
        }
        return value;
    }

    public void editTextCss(IndexRange range, String css, String value) {
        if (range.getStart() == range.getEnd()) return;

        for (int i = range.getStart(); i < range.getEnd(); i++) {
            StringBuilder builder = new StringBuilder();
            var cssCombined = text_editor.getStyleOfChar(i);
            if (cssCombined.contains(css)) {
                var pos = cssCombined.indexOf(css);
                var after_pos = cssCombined.indexOf(";", pos + 1);
                var beforeCssAttribute = cssCombined.substring(0, pos);
                var afterSemicolon = cssCombined.substring(after_pos + 1);
                builder.append(beforeCssAttribute);
                builder.append(css);
                builder.append(":");
                builder.append(value);
                builder.append(";");
                builder.append(afterSemicolon);
            } else {
                builder.append(cssCombined);
                builder.append("\n");
                builder.append(css);
                builder.append(":");
                builder.append(value);
                builder.append(";");
            }
            text_editor.setStyle(i, i + 1, builder.toString());
        }
    }

    public void removeTextCss(IndexRange range, String css) {
        if (range.getStart() == range.getEnd()) return;

        for (int i = range.getStart(); i < range.getEnd(); i++) {
            StringBuilder builder = new StringBuilder();
            var cssCombined = text_editor.getStyleOfChar(i);
            if (cssCombined.contains(css)) {
                var pos = cssCombined.indexOf(css);
                var after_pos = cssCombined.indexOf(";", pos + 1);
                var beforeCssAttribute = cssCombined.substring(0, pos);
                var afterSemicolon = cssCombined.substring(after_pos + 1);
                builder.append(beforeCssAttribute);
                builder.append(afterSemicolon);
            } else {
                builder.append(cssCombined);
                builder.append("\n");
            }
            text_editor.setStyle(i, i + 1, builder.toString());
        }
    }

    public void clear() {
        text_editor.clear();
        imagecell_describe.setImage(null);
        colorpicker.setValue(ChoiceSet.baseColor);
        text_title.setText("Title");
        button_outline.setSelected(false);
    }

    public void afterInit() {
        List<Label> list_label = new ArrayList<>();
        Label l = null;
        for (int i = 0; i < Font.getFamilies().size(); i++) {
            var name = Font.getFamilies().get(i);
            var label = new Label(name);
            label.setStyle("-fx-font-family: " + name + ";");
            list_label.add(label);
            if (name.equals("NanumGothicOTF")) {
                l = label;
            }
        }
        combo_text_font.setItems(FXCollections.observableList(list_label));
        combo_text_font.getSelectionModel().selectItem(l);

        for (var i : FontLoader.size) {
            combo_text_size.getItems().add(String.valueOf(i));
        }
        combo_text_size.getSelectionModel().selectItem("12");
    }
}
