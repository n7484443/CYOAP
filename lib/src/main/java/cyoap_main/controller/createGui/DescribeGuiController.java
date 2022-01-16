package cyoap_main.controller.createGui;

import cyoap_main.controller.IController;
import cyoap_main.core.JavaFxMain;
import cyoap_main.design.choice.ChoiceSet;
import cyoap_main.design.node_extension.ImageCell;
import cyoap_main.util.FlagUtil;
import cyoap_main.util.FontLoader;
import cyoap_main.util.LoadUtil;
import cyoap_main.util.LocalizationUtil;
import io.github.palexdev.materialfx.controls.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import org.fxmisc.richtext.InlineCssTextArea;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class DescribeGuiController implements IController {
    @FXML
    public GridPane gridpane_describe;
    @FXML
    public GridPane grid_setting;
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
    public MFXLabel label_imagecell;

    @FXML
    public MFXTextField text_round;
    @FXML
    public MFXSlider slider_round;

    @FXML
    public MFXComboBox<Label> combo_text_font;
    @FXML
    public MFXComboBox<String> combo_text_size;

    public InlineCssTextArea text_editor = new InlineCssTextArea();
    public InlineCssTextArea code_require_editor = new InlineCssTextArea();
    public InlineCssTextArea code_select_editor = new InlineCssTextArea();

    @FXML
    public MFXRadioButton button_outline;
    @FXML
    public MFXRadioButton button_horizon;
    @FXML
    public MFXRadioButton button_emptyimage;

    public List<MFXRadioButton> button_list = new ArrayList<>();

    public boolean isImageChanged = false;

    public AbstractMap.SimpleEntry<Image, String> image;

    public List<File> dropped;

    @Override
    public void nodeInit() {
        try {
            var editor_css = LoadUtil.getInstance().loadCss("/lib/css/text_editor.css");
            text_editor.setWrapText(true);
            text_editor.getStylesheets().add(editor_css);
            text_editor.getStyleClass().add("text-editor");
            text_editor.setStyle("-color-text: white ;");
            code_require_editor.setWrapText(true);
            code_require_editor.getStylesheets().add(editor_css);
            code_require_editor.getStyleClass().add("text-editor");
            code_require_editor.setStyle("-color-text: white ;");
            code_select_editor.setWrapText(true);
            code_select_editor.getStylesheets().add(editor_css);
            code_select_editor.getStyleClass().add("text-editor");
            code_select_editor.setStyle("-color-text: white ;");
        } catch (IOException e) {
            e.printStackTrace();
        }

        colorpicker.getStyleClass().add("button");

        var border_dash = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DASHED, new CornerRadii(2), new BorderWidths(2), null));
        BorderPane.setMargin(text_editor, new Insets(3f, 0, 0, 0));
        GridPane.setMargin(imagecell_describe, new Insets(12f));
        imagecell_describe.setBorder(border_dash);
        grid_setting.setBorder(border_dash);

        gridpane_describe.add(imagecell_describe, 0, 1, 2, 2);

        colorpicker_text_editor.getStyleClass().add("button");
        gridpane_describe.add(text_editor, 2, 2, 2, 1);
        gridpane_describe.add(code_require_editor, 2, 3);
        gridpane_describe.add(code_select_editor, 3, 3);
        GridPane.setMargin(code_require_editor, new Insets(3f, 3f, 3f, 0f));
        GridPane.setMargin(code_select_editor, new Insets(3f, 0f, 3f, 3f));

        button_list.add(button_outline);
        button_list.add(button_horizon);
        button_list.add(button_emptyimage);

        slider_round.setMin(0);
        slider_round.setMax(100);
        slider_round.setUnitIncrement(1);
        slider_round.setDecimalPrecision(0);
    }

    @Override
    public void localizationInit() {
        label_imagecell.setText(LocalizationUtil.getInstance().getLocalization("describeGui.label_imagecell"));
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
        imagecell_describe.setOnDragOver(e -> {
            if (e.getGestureSource() == null && e.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            e.consume();
        });
        imagecell_describe.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            var success = false;
            if (db.hasFiles()) {
                dropped = db.getFiles();
                isImageChanged = true;
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
        imagecell_describe.setOnMouseExited(e -> JavaFxMain.instance.scene_create.setCursor(Cursor.DEFAULT));
        text_round.textProperty().bindBidirectional(slider_round.valueProperty(), new StringConverter<>() {
            @Override
            public String toString(Number object) {
                return String.valueOf(object.intValue());
            }

            @Override
            public Integer fromString(String string) {
                try {
                    var round = Integer.getInteger(string);
                    if (round <= 0) {
                        return 0;
                    }
                    return round;
                } catch (Exception e) {
                    return 0;
                }
            }
        });
        slider_round.valueProperty().addListener((before, after, e) -> imagecell_describe.round.set(after.intValue()));

        button_save.setOnMouseClicked(e -> CreateGuiController.instance.save_describe_pane());
        button_next.setOnMouseClicked(e -> {
            CreateGuiController.instance.save_describe_pane();
            CreateGuiController.instance.next();
        });
    }

    public StringBuilder builder = new StringBuilder();
    public String getTextCss(IndexRange range, String css) {
        if (range.getStart() == range.getEnd()) return "error";

        String value = null;
        for (int i = range.getStart(); i < range.getEnd(); i++) {
            builder.setLength(0);
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
            builder.setLength(0);
            var cssCombined = text_editor.getStyleOfChar(i);
            if (cssCombined.contains(css)) {
                var pos = cssCombined.indexOf(css);
                var after_pos = cssCombined.indexOf(";", pos + 1);
                var beforeCssAttribute = cssCombined.substring(0, pos);
                var afterSemicolon = cssCombined.substring(after_pos + 1);
                builder.append(beforeCssAttribute).append(css).append(":").append(value).append(";").append(afterSemicolon);
            } else {
                builder.append(cssCombined).append("\n").append(css).append(":").append(value).append(";");
            }
            text_editor.setStyle(i, i + 1, builder.toString());
        }
    }

    public void removeTextCss(IndexRange range, String css) {
        if (range.getStart() == range.getEnd()) return;

        for (int i = range.getStart(); i < range.getEnd(); i++) {
            builder.setLength(0);
            var cssCombined = text_editor.getStyleOfChar(i);
            if (cssCombined.contains(css)) {
                var pos = cssCombined.indexOf(css);
                var after_pos = cssCombined.indexOf(";", pos + 1);
                var beforeCssAttribute = cssCombined.substring(0, pos);
                var afterSemicolon = cssCombined.substring(after_pos + 1);
                builder.append(beforeCssAttribute).append(afterSemicolon);
            } else {
                builder.append(cssCombined).append("\n");
            }
            text_editor.setStyle(i, i + 1, builder.toString());
        }
    }


    public void load(ChoiceSet dataSet) {
        text_title.setText(dataSet.string_title);

        LoadUtil.loadSegment(text_editor, dataSet.segmentList);
        if (dataSet.string_code_require != null) code_require_editor.appendText(dataSet.string_code_require);
        if (dataSet.string_code_select != null) code_select_editor.appendText(dataSet.string_code_select);
        colorpicker.setValue(dataSet.color);
        if (dataSet.string_image_name != null && !dataSet.string_image_name.isEmpty()) {
            image = LoadUtil.loadImage(dataSet.string_image_name);
            imagecell_describe.setImage(image.getKey());
            imagecell_describe.setCut(dataSet.round);
        }
        for (int i = 0; i < button_list.size(); i++) {
            button_list.get(i).setSelected(FlagUtil.getFlag(dataSet.flag, i));
        }
        dataSet.updateFlag();
    }

    public void clear() {
        text_editor.clear();
        code_require_editor.clear();
        code_select_editor.clear();
        imagecell_describe.setImage(null);
        colorpicker.setValue(ChoiceSet.baseColor);
        text_title.setText("Title");
        button_outline.setSelected(false);
        dropped = null;
        image = null;
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

    public void update() {
        if (isImageChanged) {
            isImageChanged = false;
            image = LoadUtil.loadImage(dropped.get(0));
            imagecell_describe.setImage(image.getKey());
        }
    }
}
