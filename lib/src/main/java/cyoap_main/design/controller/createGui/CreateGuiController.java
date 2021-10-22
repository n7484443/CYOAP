package cyoap_main.design.controller.createGui;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import cyoap_main.command.*;
import cyoap_main.design.node_extension.ImageCell;
import cyoap_main.design.node_extension.ResizableCanvas;
import cyoap_main.unit.Bound2f;
import cyoap_main.util.FontLoader;
import cyoap_main.util.SizeUtil;
import io.github.palexdev.materialfx.controls.*;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.fxmisc.richtext.InlineCssTextArea;

import com.fasterxml.jackson.databind.ObjectMapper;

import cyoap_main.core.JavaFxMain;
import cyoap_main.design.choice.ChoiceSet;
import cyoap_main.design.controller.IPlatformGuiController;
import cyoap_main.design.platform.AbstractPlatform;
import cyoap_main.design.platform.CreatePlatform;
import cyoap_main.grammer.Analyser;
import cyoap_main.grammer.VarData;
import cyoap_main.util.FlagUtil;
import cyoap_main.util.LoadUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
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

public class CreateGuiController implements IPlatformGuiController {
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
    public ColorPicker colorpicker_text_editor;
    @FXML
    public MFXComboBox<Label> combo_text_font;

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

    public void capture(float pixelScale) {

        var width_before = this.getChoicePane().getWidth();
        var before_height = this.getChoicePane().getHeight();
        var width_after = (platform.max_x - platform.min_x);
        var height_after = (platform.max_y - platform.min_y);

        this.getChoicePane().resize(width_after, height_after);

        platform.updatePositionAll(platform.local_x, platform.local_y);

        var spa = new SnapshotParameters();
        spa.setTransform(Transform.scale(pixelScale, pixelScale));
        spa.setViewport(new Rectangle2D(platform.min_x * pixelScale, platform.min_y * pixelScale,
                platform.max_x - platform.min_x, platform.max_y - platform.min_y));
        var writeableImage = this.getChoicePane().snapshot(spa,
                new WritableImage((int) (this.getChoicePane().getWidth() * pixelScale),
                        (int) (this.getChoicePane().getHeight() * pixelScale)));

        BufferedImage tempImg = SwingFXUtils.fromFXImage(writeableImage, null);
        String imageType = "png";
        File f = new File(JavaFxMain.instance.directory.getAbsolutePath() + File.separator + "file." + imageType);
        try {
            ImageIO.write(tempImg, imageType, f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        platform.updatePositionAll(-platform.local_x, -platform.local_y);
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
            Image image = new Image(LoadUtil.instance.loadInternalImage("/lib/image/tutorial_image.png"));
            imagecell_tutorialImage.setImage(image);
            imagecell_tutorialImage.setOnMouseClicked(t -> {
                anchorpane_create.getChildren().remove(imagecell_tutorialImage);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        pane_position_parent.getChildren().add(canvas);
        pane_position.getChildren().add(imagecell_background);

        gridpane_describe.add(imagecell_describe, 0, 1, 2, 1);
        GridPane.setMargin(imagecell_describe, new Insets(5));

        hbox_setting.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DASHED, new CornerRadii(2), new BorderWidths(2), null)));
        colorpicker_text_editor.getStyleClass().add("button");

        pane_text_editor.setCenter(text_editor);
        BorderPane.setMargin(text_editor, new Insets(2.5f, 0, 0, 0));

        canvas.setMouseTransparent(true);
        AnchorPane.setBottomAnchor(canvas, 0d);
        AnchorPane.setLeftAnchor(canvas, 0d);
        AnchorPane.setRightAnchor(canvas, 0d);
        AnchorPane.setTopAnchor(canvas, 0d);
        canvas.scaleXProperty().bind(getChoicePane().scaleXProperty());
        canvas.scaleYProperty().bind(getChoicePane().scaleYProperty());


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
            text_editor.setStyle(range.getStart(), range.getEnd(),
                    "-color-text: #" + colorpicker_text_editor.getValue().toString().replace("0x", "") + ";");
        });
        combo_text_font.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Label> observ,
                                                                                Label oldVal, Label newVal) -> {
            if (newVal != null) {
                var range = text_editor.getSelection();
                text_editor.setStyle(range.getStart(), range.getEnd(),
                        "-fx-font-family: " + newVal.getText() + ";");
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
            Bounds boundsInScene = gridpane_describe.localToScene(gridpane_describe.getBoundsInLocal());
            var pos_x = (float) (platform.local_x + platform.start_mouse_x - boundsInScene.getMinX());
            var pos_y = (float) (platform.local_y + platform.start_mouse_y - boundsInScene.getMinY());
            if (menu == menu_create) {
                commandTimeline.excuteCommand(new CreateCommand(pos_x, pos_y));
            } else if (menu == menu_delete) {
                if (nowMouseInDataSet != null && nowMouseInDataSet.check_intersect(nowMouseInDataSet, pos_x, pos_y)) {
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
                anchorpane_slider.setLayoutX(anchorpane_create.getWidth() / 2f - anchorpane_slider.getLayoutBounds().getWidth() / 2f);
                anchorpane_slider.setLayoutY(anchorpane_create.getHeight() / 2f - anchorpane_slider.getLayoutBounds().getHeight() / 2f);
            }
        });

        pane_position.setOnMouseDragged(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                var cursor = JavaFxMain.instance.scene_create.getCursor();
                float move_x = (float) (e.getSceneX() - platform.start_mouse_x);
                float move_y = (float) (e.getSceneY() - platform.start_mouse_y);
                if ((cursor == null || cursor.equals(Cursor.DEFAULT))) {
                    move_x *= platform.sensitivity;
                    move_y *= platform.sensitivity;
                    updateMouseCoord(move_x, move_y, e.getSceneX(), e.getSceneY());
                } else if (nowSizeChange != null) {
                    SizeUtil.changeSize(nowSizeChange, move_x, move_y);
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
        for (int i = 0; i < Font.getFamilies().size(); i++) {
            var name = Font.getFamilies().get(i);
            var label = new Label(Font.getFamilies().get(i));
            label.setStyle("-fx-font-family: " + Font.getFamilies().get(i) + ";");
            combo_text_font.getItems().add(label);
            if (Font.getFamilies().get(i).equals("NanumGothicOTF")) {
                combo_text_font.setSelectedValue(label);
            }
        }
    }

    public void loadPlatform() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File file_platform_json = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/platform.json");
            if (file_platform_json.exists()) {
                InputStreamReader writer = new InputStreamReader(new FileInputStream(JavaFxMain.instance.directory.getAbsolutePath() + "/platform.json"), StandardCharsets.UTF_8);
                platform = objectMapper.readValue(writer, AbstractPlatform.class);
                platform.setUp(this);
                platform.isImageChanged = true;
                platform.updateFlag();
                this.button_background_preserve_ratio.setSelected(FlagUtil.getFlag(platform.flag, AbstractPlatform.flagPosition_background_preserve_ratio));
            } else {
                platform.local_x = -getChoicePaneRealWidth() / 2;
                platform.local_y = -getChoicePaneRealHeight() / 2;
                getChoicePane().setPrefSize(platform.max_x - platform.min_x, platform.max_y - platform.min_y);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void undo_shortcut() {
        commandTimeline.undoCommand();
    }

    public void redo_shortcut() {
        commandTimeline.redoCommand();
    }

    @Override
    public ImageCell getBackgroundImageView() {
        return imagecell_background;
    }

    @Override
    public Pane getChoicePane() {
        return pane_position;
    }

    @Override
    public AbstractPlatform getPlatform() {
        return platform;
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
