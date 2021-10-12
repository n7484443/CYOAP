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

import cyoap_main.design.node_extension.ImageCell;
import cyoap_main.design.node_extension.ResizableCanvas;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.fxmisc.richtext.InlineCssTextArea;

import com.fasterxml.jackson.databind.ObjectMapper;

import cyoap_main.command.CommandTimeline;
import cyoap_main.command.CreateCommand;
import cyoap_main.command.DeleteCommand;
import cyoap_main.command.TextChangeCommand;
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
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    public TextField text_title;
    @FXML
    public ColorPicker colorpicker;
    @FXML
    public ListView<String> view_var_field;
    @FXML
    public ListView<String> view_var_type;
    @FXML
    public ListView<String> view_command_timeline;
    public ImageCell imagecell_describe = new ImageCell();
    @FXML
    public MenuItem menu_create;
    @FXML
    public MenuItem menu_delete;
    @FXML
    public MenuItem menu_connect;
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
    public RadioButton button_darkmode;
    @FXML
    public RadioButton button_background_preserve_ratio;


    @FXML
    public RadioButton button_outline;
    @FXML
    public RadioButton button_horizon;
    @FXML
    public RadioButton button_emptyimage;

    public List<RadioButton> button_list = new ArrayList<>();
    @FXML
    public Button button_border;
    @FXML
    public Button button_borderless;

    public BorderPane pane_text_editor = new BorderPane();
    public VBox pane_setting = new VBox();
    public InlineCssTextArea text_editor = new InlineCssTextArea();
    public ColorPicker colorpicker_text_editor = new ColorPicker();

    public ResizableCanvas canvas = new ResizableCanvas();

    public List<File> dropped;
    public boolean isImageChanged = false;

    public CommandTimeline commandTimeline = new CommandTimeline();
    public static AbstractPlatform platform;

    public ChoiceSet nowControl;
    public ChoiceSet nowEditDataSet;
    public ChoiceSet nowMouseInDataSet;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUp();
        pane_position_parent.getChildren().add(canvas);
        pane_position.getChildren().add(imagecell_background);
        AnchorPane.setBottomAnchor(canvas, 0d);
        AnchorPane.setLeftAnchor(canvas, 0d);
        AnchorPane.setRightAnchor(canvas, 0d);
        AnchorPane.setTopAnchor(canvas, 0d);

        gridpane_describe.add(pane_text_editor, 2, 1);
        gridpane_describe.add(imagecell_describe, 0, 1, 2, 1);

        pane_text_editor.setPrefWidth(893 - 412 - 6);
        pane_text_editor.setPrefHeight(574);
        pane_text_editor.setLayoutX(412);
        pane_text_editor.setLayoutY(32);

        pane_setting.setPrefHeight(30);
        pane_setting.setBorder(new Border(new BorderStroke(null, null, Color.BLACK, null, null, null,
                BorderStrokeStyle.DASHED, null, new CornerRadii(2), new BorderWidths(2), null)));
        pane_setting.getChildren().add(colorpicker_text_editor);
        colorpicker_text_editor.getStyleClass().add("button");

        pane_text_editor.setTop(pane_setting);
        pane_text_editor.setCenter(text_editor);

        canvas.setMouseTransparent(true);

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
                if (nowControl != null) {
                    nowControl.updateSizeFrom();
                    nowControl.isClicked = false;
                    nowControl = null;
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
                } else if (nowControl != null) {
                    if (cursor.equals(Cursor.NW_RESIZE)) {
                        nowControl.changeSize(-move_x + nowControl.guiComponent.width_before,
                                -move_y + nowControl.guiComponent.height_before);
                        nowControl.setPosition(move_x + nowControl.guiComponent.x_before,
                                move_y + nowControl.guiComponent.y_before);
                    } else if (cursor.equals(Cursor.SW_RESIZE)) {
                        nowControl.changeSize(-move_x + nowControl.guiComponent.width_before,
                                move_y + nowControl.guiComponent.height_before);
                        nowControl.setPosition(move_x + nowControl.guiComponent.x_before,
                                nowControl.guiComponent.y_before);
                    } else if (cursor.equals(Cursor.NE_RESIZE)) {
                        nowControl.changeSize(move_x + nowControl.guiComponent.width_before,
                                -move_y + nowControl.guiComponent.height_before);
                        nowControl.setPosition(nowControl.guiComponent.x_before,
                                move_y + nowControl.guiComponent.y_before);
                    } else if (cursor.equals(Cursor.SE_RESIZE)) {
                        nowControl.changeSize(move_x + nowControl.guiComponent.width_before,
                                move_y + nowControl.guiComponent.height_before);
                        nowControl.setPosition(nowControl.guiComponent.x_before, nowControl.guiComponent.y_before);
                    }
                    nowControl.isClicked = true;
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

    public void capture(float pixelScale) {

        var width_before = this.getChoicePane().getWidth();
        var height_before = this.getChoicePane().getHeight();
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
        this.getChoicePane().resize(width_before, height_before);
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
        button_outline.setSelected(FlagUtil.getFlag(dataSet.flag, ChoiceSet.flagPosition_selectable));
        button_horizon.setSelected(FlagUtil.getFlag(dataSet.flag, ChoiceSet.flagPosition_horizontal));
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

    public CreateGuiController() {
        CreateGuiController.instance = this;
        platform = new CreatePlatform(instance);
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
    }

    public void loadPlatform() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            InputStreamReader writer = new InputStreamReader(new FileInputStream(JavaFxMain.instance.directory.getAbsolutePath() + "/platform.json"), StandardCharsets.UTF_8);

            platform = objectMapper.readValue(writer, AbstractPlatform.class);
            platform.setUp(this);
            platform.isImageChanged = true;
            platform.updateFlag();

            this.button_background_preserve_ratio.setSelected(FlagUtil.getFlag(platform.flag, AbstractPlatform.flagPosition_background_preserve_ratio));
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
    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public boolean isEditable() {
        return true;
    }
}
