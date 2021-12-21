package cyoap_main.controller.createGui;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.imageio.*;

import cyoap_main.command.*;
import cyoap_main.design.node_extension.ImageCell;
import cyoap_main.design.node_extension.ResizableCanvas;
import cyoap_main.unit.Bound2f;
import cyoap_main.unit.Vector2f;
import cyoap_main.util.*;
import io.github.palexdev.materialfx.controls.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import org.fxmisc.richtext.InlineCssTextArea;

import com.fasterxml.jackson.databind.ObjectMapper;

import cyoap_main.core.JavaFxMain;
import cyoap_main.design.choice.ChoiceSet;
import cyoap_main.controller.IGuiController;
import cyoap_main.platform.AbstractPlatform;
import cyoap_main.platform.CreatePlatform;
import cyoap_main.grammer.Analyser;
import cyoap_main.grammer.VarData;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;

import static cyoap_main.util.LocalizationUtil.getLocalization;

public class CreateGuiController implements IGuiController {
    public static CreateGuiController instance;
    @FXML
    public AnchorPane anchorpane_create;
    @FXML
    public AnchorPane pane_position;
    @FXML
    public AnchorPane pane_position_parent;
    @FXML
    public GridPane gridpane_mainGui;
    @FXML
    public GridPane gridpane_describe;
    @FXML
    public GridPane gridpane_general;
    @FXML
    public Button button_save;
    @FXML
    public Button button_next;
    @FXML
    public MFXTextField text_title;
    @FXML
    public ColorPicker colorpicker;
    @FXML
    public MFXListView<String> view_var_field;
    @FXML
    public MFXListView<String> view_var_type;
    @FXML
    public MFXListView<String> view_command_timeline;

    public ImageCell imagecell_describe = new ImageCell();
    @FXML
    public MenuItem menu_create;
    @FXML
    public MenuItem menu_delete;
    @FXML
    public MenuItem menu_copySize;
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
    public Tab tab_generalSetting;

    public ImageCell imagecell_background = new ImageCell();
    @FXML
    public MFXRadioButton button_darkmode;
    @FXML
    public MFXRadioButton button_background_preserve_ratio;


    @FXML
    public MFXRadioButton button_outline;
    @FXML
    public MFXRadioButton button_horizon;
    @FXML
    public MFXRadioButton button_emptyimage;

    public List<MFXRadioButton> button_list = new ArrayList<>();
    @FXML
    public MFXButton button_border;
    @FXML
    public MFXButton button_borderless;

    @FXML
    public BorderPane pane_text_editor;
    @FXML
    public HBox hbox_setting;

    public InlineCssTextArea text_editor = new InlineCssTextArea();

    @FXML
    public ImageView image_text_bold;
    @FXML
    public ImageView image_text_italic;
    @FXML
    public ImageView image_text_underline;

    @FXML
    public ColorPicker colorpicker_text_editor;
    @FXML
    public ColorPicker colorpicker_background;
    @FXML
    public MFXComboBox<Label> combo_text_font;
    @FXML
    public MFXComboBox<String> combo_text_size;

    @FXML
    public MFXTextField text_round;

    @FXML
    public VBox vbox_background_order;
    @FXML
    public MFXScrollPane scrollpane_background_order;

    public ResizableCanvas canvas = new ResizableCanvas();

    public List<File> dropped;
    public boolean isImageChanged = false;

    public CommandTimeline commandTimeline = new CommandTimeline();
    public static AbstractPlatform platform;

    public SimpleEntry<ChoiceSet, SizeChangeCommand> nowSizeChange;

    public ChoiceSet nowEditDataSet;
    public ChoiceSet nowMouseInDataSet;

    public Bound2f copyBound;

    public CreateGuiController() {
        CreateGuiController.instance = this;
        platform = new CreatePlatform(instance);
        platform.setUp(this);
    }

    public void capture(float pixelScale, String imageType) {
        var width_before = this.getChoicePane().getWidth();
        var before_height = this.getChoicePane().getHeight();
        var width_after = (platform.max_x - platform.min_x);
        var height_after = (platform.max_y - platform.min_y);

        this.getChoicePane().resize(width_after, height_after);

        platform.updateTranslationAll(0, 0);

        var spa = new SnapshotParameters();
        spa.setTransform(Transform.scale(pixelScale, pixelScale));
        spa.setViewport(new Rectangle2D(platform.min_x * pixelScale, platform.min_y * pixelScale,
                platform.max_x - platform.min_x, platform.max_y - platform.min_y));
        var writeableImage = this.getChoicePane().snapshot(spa,
                new WritableImage((int) (this.getChoicePane().getWidth() * pixelScale),
                        (int) (this.getChoicePane().getHeight() * pixelScale)));

        BufferedImage tempImg = SwingFXUtils.fromFXImage(writeableImage, null);
        File f = new File(JavaFxMain.instance.directory.getAbsolutePath() + File.separator + "file." + imageType);
        try {
            var imageOutputStream = ImageIO.createImageOutputStream(f);
            if (!ImageIO.write(tempImg, imageType, imageOutputStream)) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        platform.updateTranslationAll(-platform.local_x, -platform.local_y);
        this.getChoicePane().resize(width_before, before_height);
    }

    public String addTextIntoString(String str, int anchor, int caret, String add) {
        if (str == null) {
            str = "";
        }
        var before = str.substring(0, Math.min(anchor, caret));
        var after = str.substring(Math.max(anchor, caret));
        return before + add + after;
    }

    public void save_describe_pane() {
        VarData.isUpdated = true;

        var text = Analyser.parser(text_editor.getText());
        StringBuilder builder = new StringBuilder();
        if (text != null)
            text.forEach(builder::append);
        if (nowEditDataSet != null) {
            var command = new TextChangeCommand(nowEditDataSet);
            if (!text_title.getText().equals(nowEditDataSet.string_title)) {
                var number_of = (int) this.getPlatform().choiceSetList.stream().filter(t -> t != nowEditDataSet && t.string_title.equals(text_title.getText())).count();
                if (number_of == 0) {
                    nowEditDataSet.string_title = text_title.getText();
                } else {
                    System.err.println("duplicated title! " + nowEditDataSet.string_title);
                }
            }
            if (image != null)
                nowEditDataSet.string_image_name = image.getValue();
            nowEditDataSet.color = colorpicker.getValue();
            nowEditDataSet.round = imagecell_describe.round.get();

            var v = button_list.stream().map(ToggleButton::isSelected).collect(Collectors.toList());
            nowEditDataSet.flag = FlagUtil.createFlag(v);

            LoadUtil.paragraphToSegment(text_editor.getDocument().getParagraphs(), nowEditDataSet.segmentList);

            nowEditDataSet.update();
            command.setText(nowEditDataSet);
            commandTimeline.addCommand(command);
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

    public void loadFromDataSet(ChoiceSet dataSet) {
        text_title.setText(dataSet.string_title);
        LoadUtil.loadSegment(text_editor, dataSet.segmentList);
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

    public void next() {
        CreateGuiController.instance.changeTab(CreateGuiController.instance.tab_position);
        this.text_editor.clear();
        this.text_title.setText("Title");
        this.colorpicker.setValue(ChoiceSet.baseColor);
        this.button_outline.setSelected(false);
        this.imagecell_describe.setImage(null);
        this.image = null;
        this.dropped = null;
        this.nowEditDataSet = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUp();
        ImageCell imagecell_tutorialImage = new ImageCell();
        anchorpane_create.getChildren().add(imagecell_tutorialImage);
        try {
            var image_source = "/lib/image/";
            Image image = new Image(LoadUtil.instance.loadInternalImage(image_source + "tutorial_image.png"));
            imagecell_tutorialImage.setImage(image);
            imagecell_tutorialImage.setOnMouseClicked(t ->
                    anchorpane_create.getChildren().remove(imagecell_tutorialImage)
            );
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        scrollpane_background_order.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        pane_position_parent.getChildren().add(canvas);
        pane_position.getChildren().add(imagecell_background);

        gridpane_describe.add(imagecell_describe, 0, 1, 2, 1);
        GridPane.setMargin(imagecell_describe, new Insets(5));

        hbox_setting.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DASHED, new CornerRadii(2), new BorderWidths(2), null)));
        colorpicker_text_editor.getStyleClass().add("button");
        colorpicker_background.getStyleClass().add("button");

        pane_text_editor.setCenter(text_editor);
        BorderPane.setMargin(text_editor, new Insets(2.5f, 0, 0, 0));

        canvas.setMouseTransparent(true);
        canvas.toFront();

        button_list.add(button_outline);
        button_list.add(button_horizon);
        button_list.add(button_emptyimage);


        try {
            text_editor.setWrapText(true);
            text_editor.getStylesheets().add(LoadUtil.instance.loadCss("/lib/css/text_editor.css"));
            text_editor.getStyleClass().add("text-editor");
            text_editor.setStyle("-color-text: white ;");
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        colorpicker_text_editor.valueProperty().addListener(e -> {
            var range = text_editor.getSelection();
            editTextCss(range, "-color-text", colorpicker_text_editor.getValue().toString().replace("0x", "#"));
        });
        colorpicker_background.valueProperty().addListener(e -> getPlatform().updateColor(colorpicker_background.getValue()));
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

        colorpicker.getStyleClass().add("button");


        button_borderless.setOnMouseClicked(e ->
                getPlatform().choiceSetList.forEach(t -> {
                    t.flag = FlagUtil.setFlag(t.flag, ChoiceSet.flagPosition_selectable, true);
                    t.updateFlag();
                })
        );
        button_border.setOnMouseClicked(e -> getPlatform().choiceSetList.forEach(t -> {
            t.flag = FlagUtil.setFlag(t.flag, ChoiceSet.flagPosition_selectable, false);
            t.updateFlag();
        }));

        button_background_preserve_ratio.setOnMouseClicked(e -> {
            this.getPlatform().flag = FlagUtil.setFlag(getPlatform().flag, AbstractPlatform.flagPosition_background_preserve_ratio, button_background_preserve_ratio.isSelected());
            this.getPlatform().updateFlag();
        });


        button_save.setOnMouseClicked(e ->
                save_describe_pane()
        );
        button_next.setOnMouseClicked(e -> {
            save_describe_pane();
            next();
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
                dropped = db.getFiles();
                isImageChanged = true;
                success = true;
            }
            e.setDropCompleted(success);
            e.consume();
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

        view_var_field.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                if (e.getClickCount() == 2) {
                    var variable = view_var_field.getSelectionModel().getSelectedIndex();
                    if (variable >= 0) {
                        var text = addTextIntoString(text_editor.getText(), text_editor.getAnchor(),
                                text_editor.getCaretPosition(),
                                "{" + VarData.var_map.keySet().toArray()[variable] + "}");
                        text_editor.clear();
                        text_editor.appendText(text);
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
                var v = LoadUtil.loadImage(db.getFiles().get(0));
                platform.background_image = v.getKey();
                platform.string_image_name = v.getValue();
                platform.isImageChanged = true;
                success = true;
            }
            e.setDropCompleted(success);
            e.consume();
        });

        pane_position.setOnMousePressed(e -> {
            platform.start_mouse_x = e.getSceneX();
            platform.start_mouse_y = e.getSceneY();
        });
        pane_position.setOnMouseReleased(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                if (!e.getTarget().equals(menu_mouse)) {
                    menu_mouse.hide();
                }
                if (nowSizeChange != null) {
                    SizeUtil.changeSizeComplete(nowSizeChange);
                    nowSizeChange.getKey().updateSizeFrom();

                    nowSizeChange.getKey().isClicked = false;
                    nowSizeChange.getValue().set(nowSizeChange.getKey());
                    commandTimeline.addCommand(nowSizeChange.getValue());
                    nowSizeChange = null;
                }
            } else if (e.getButton().equals(MouseButton.SECONDARY)) {
                menu_mouse.show(pane_position, e.getScreenX(), e.getScreenY());
            } else {
                menu_mouse.hide();
            }
        });

        menu_mouse.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            if (e.getButton() != MouseButton.PRIMARY) {
                e.consume();
            }
        });
        menu_mouse.setOnAction(e -> {
            var menu = (MenuItem) e.getTarget();
            var pos = getPositionFromMouse(platform.start_mouse_x, platform.start_mouse_y);

            if (menu == menu_create) {
                commandTimeline.excuteCommand(new CreateCommand(pos.x(), pos.y()));
            } else if (menu == menu_delete) {
                if (nowMouseInDataSet != null && nowMouseInDataSet.check_intersect(nowMouseInDataSet, pos.x(), pos.y())) {
                    commandTimeline
                            .excuteCommand(new DeleteCommand(nowMouseInDataSet, platform.local_x, platform.local_y));
                }
            } else if (menu == menu_copySize) {
                if (copyBound == null) {
                    copyBound = nowMouseInDataSet.bound;
                    menu_copySize.setText("Paste Size");
                } else {
                    nowMouseInDataSet.changeSize(copyBound.width, copyBound.height);
                    copyBound = null;
                    menu_copySize.setText("Copy Size");
                }
            } else if (menu == menu_saveAsImage) {
                var anchorpane_slider = PixelScaleGuiController.instance.anchorPane_slider;

                anchorpane_create.getChildren().add(anchorpane_slider);
                anchorpane_slider.setLayoutX(anchorpane_create.getWidth() / 2f - anchorpane_slider.getWidth() / 2f);
                anchorpane_slider.setLayoutY(anchorpane_create.getHeight() / 2f - anchorpane_slider.getHeight() / 2f);
            }
        });

        pane_position.setOnMouseDragged(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                var cursor = JavaFxMain.instance.scene_create.getCursor();
                var move_before = getPositionFromMouse(platform.start_mouse_x, platform.start_mouse_y);
                var move_after = getPositionFromMouse(e.getSceneX(), e.getSceneY());
                var move = move_after.sub(move_before);
                if ((cursor == null || cursor.equals(Cursor.DEFAULT))) {
                    move = move_after.sub(move_before).mul((float) platform.sensitivity);
                    updateMouseCoord(move.x(), move.y(), e.getSceneX(), e.getSceneY());
                } else if (nowSizeChange != null) {
                    SizeUtil.changeSize(nowSizeChange, move.x(), move.y());
                }
            }
        });

        view_var_type.getItems().addAll("&b | boolean", " \"\" | string", "floor | 내림", "ceil | 올림", "round | 반올림");
        view_var_type.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                if (e.getClickCount() == 2) {
                    var index = view_var_type.getSelectionModel().getSelectedIndex();
                    var anchor = text_editor.getAnchor();
                    var caret = text_editor.getCaretPosition();
                    String text = switch (index) {
                        case 0 -> addTextIntoString(text_editor.getText(), anchor, caret, "&b");
                        case 1 -> addTextIntoString(text_editor.getText(), anchor, caret, "\" \"");
                        case 2 -> addTextIntoString(text_editor.getText(), anchor, caret, "floor( )");
                        case 3 -> addTextIntoString(text_editor.getText(), anchor, caret, "ceil( )");
                        case 4 -> addTextIntoString(text_editor.getText(), anchor, caret, "round( )");
                        default -> "";
                    };
                    text_editor.clear();
                    text_editor.appendText(text);
                }
            }
        });

        setLocalization();
    }

    public void setLocalization() {
        button_darkmode.setText(getLocalization("generalSetting.darkmode"));
        button_background_preserve_ratio.setText(getLocalization("generalSetting.background_preserve"));
    }

    public Vector2f getPositionFromMouse(double mouse_x, double mouse_y) {
        Point2D mousePoint = new Point2D(mouse_x, mouse_y);
        Point2D posInZoomTarget = pane_position.sceneToLocal(mousePoint);

        return new Vector2f((float) (platform.local_x + posInZoomTarget.getX()),
                (float) (platform.local_y + posInZoomTarget.getY()));
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


    public SimpleEntry<Image, String> image = null;

    public void update() {
        if (isImageChanged) {
            isImageChanged = false;
            image = LoadUtil.loadImage(dropped.get(0));
            imagecell_describe.setImage(image.getKey());
        }

        if (VarData.isUpdated) {
            VarData.isUpdated = false;
            List<String> name_list = new ArrayList<>();
            for (var key : VarData.var_map.keySet()) {
                var value = VarData.var_map.get(key);
                name_list.add(key + "  |  " + value.data + "  |  " + value.type.toString());
            }
            view_var_field.getItems().clear();
            view_var_field.getItems().setAll(name_list);
            if (view_var_field.getItems().size() < 10) {
                for (int i = view_var_field.getItems().size(); i < 10; i++) {
                    view_var_field.getItems().add("");
                }
            }
        }
        commandTimeline.update();
        platform.update();
    }

    public void changeTab(Tab tab) {
        tabpane_make.getSelectionModel().select(tab);
    }

    public void save_shortcut() {
        getPlatform().save();
        save_describe_pane();
        save_position_pane();
        commandTimeline.save();
    }

    public void load_shortcut() {
        loadPlatform();
        load();
        commandTimeline.load();
        afterInit();
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

    @Override
    public void loadPlatformSetup() {
        this.button_background_preserve_ratio.setSelected(FlagUtil.getFlag(platform.flag, AbstractPlatform.flagPosition_background_preserve_ratio));
    }

    public void undo_shortcut() {
        commandTimeline.undoCommand();
    }

    public void redo_shortcut() {
        commandTimeline.redoCommand();
    }

    @Override
    public List<ImageCell> getBackgroundImageCellList() {
        var list = new ArrayList<ImageCell>();
        list.add(imagecell_background);
        return list;
    }

    @Override
    public Pane getChoicePane() {
        return pane_position;
    }

    @Override
    public Pane getChoicePaneParent() {
        return pane_position_parent;
    }

    @Override
    public AbstractPlatform getPlatform() {
        return platform;
    }

    @Override
    public void setPlatform(AbstractPlatform abstractPlatform) {
        this.platform = abstractPlatform;
    }

    @Override
    public ResizableCanvas getCanvas() {
        return canvas;
    }

    @Override
    public boolean isEditable() {
        return true;
    }
}
