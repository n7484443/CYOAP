package cyoap_main.controller.createGui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import cyoap_main.command.*;
import cyoap_main.controller.IGuiController;
import cyoap_main.core.JavaFxMain;
import cyoap_main.design.choice.ChoiceSet;
import cyoap_main.design.node_extension.ImageCell;
import cyoap_main.design.node_extension.ResizableCanvas;
import cyoap_main.grammer.Analyser;
import cyoap_main.grammer.VariableDataBase;
import cyoap_main.platform.AbstractPlatform;
import cyoap_main.platform.CreatePlatform;
import cyoap_main.unit.Bound2f;
import cyoap_main.unit.Vector2f;
import cyoap_main.util.FlagUtil;
import cyoap_main.util.LoadUtil;
import cyoap_main.util.LocalizationUtil;
import cyoap_main.util.SizeUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Transform;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public GridPane gridpane_general;
    @FXML
    public MFXListView<String> view_var_field;
    @FXML
    public MFXListView<String> view_var_type;
    @FXML
    public MFXListView<String> view_command_timeline;
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
    public MFXButton button_border;
    @FXML
    public MFXButton button_borderless;
    @FXML
    public ColorPicker colorpicker_background;

    @FXML
    public VBox vbox_background_order;
    @FXML
    public MFXScrollPane scrollpane_background_order;

    @FXML
    public DescribeGuiController describeGuiController;

    public ResizableCanvas canvas = new ResizableCanvas();

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
            ImageIO.write(tempImg, imageType, imageOutputStream);
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
        VariableDataBase.getInstance().isUpdated = true;

        var pair = Analyser.getInstance().parser(describeGuiController.text_editor.getText());
        if (pair != null) {
            Analyser.getInstance().analyseList(pair.getValue());
        }
        if (nowEditDataSet != null) {
            var command = new TextChangeCommand(nowEditDataSet);
            if (!describeGuiController.text_title.getText().equals(nowEditDataSet.string_title)) {
                var number_of = (int) this.getPlatform().choiceSetList.stream().filter(t -> t != nowEditDataSet && t.string_title.equals(describeGuiController.text_title.getText())).count();
                if (number_of == 0) {
                    nowEditDataSet.string_title = describeGuiController.text_title.getText();
                } else {
                    System.err.println("duplicated title! " + nowEditDataSet.string_title);
                }
            }
            if (describeGuiController.image != null)
                nowEditDataSet.string_image_name = describeGuiController.image.getValue();
            nowEditDataSet.color = describeGuiController.colorpicker.getValue();
            nowEditDataSet.round = describeGuiController.imagecell_describe.round.get();

            var v = describeGuiController.button_list.stream().map(ToggleButton::isSelected).collect(Collectors.toList());
            nowEditDataSet.flag = FlagUtil.createFlag(v);

            LoadUtil.paragraphToSegment(describeGuiController.text_editor.getDocument().getParagraphs(), nowEditDataSet.segmentList);

            nowEditDataSet.needUpdate = true;
            command.setText(nowEditDataSet);
            commandTimeline.addCommand(command);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public void save_position_pane() {
        ObjectMapper objectMapper = new ObjectMapper();
        File dir = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/choiceSet");
        try {
            MoreFiles.deleteRecursively(dir.toPath(), RecursiveDeleteOption.ALLOW_INSECURE);
            dir.mkdir();
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
        describeGuiController.text_title.setText(dataSet.string_title);
        LoadUtil.loadSegment(describeGuiController.text_editor, dataSet.segmentList);
        describeGuiController.colorpicker.setValue(dataSet.color);
        if (dataSet.string_image_name != null && !dataSet.string_image_name.isEmpty()) {
            describeGuiController.image = LoadUtil.loadImage(dataSet.string_image_name);
            describeGuiController.imagecell_describe.setImage(describeGuiController.image.getKey());
            describeGuiController.imagecell_describe.setCut(dataSet.round);
        }
        for (int i = 0; i < describeGuiController.button_list.size(); i++) {
            describeGuiController.button_list.get(i).setSelected(FlagUtil.getFlag(dataSet.flag, i));
        }
        dataSet.updateFlag();
    }

    public void next() {
        CreateGuiController.instance.changeTab(CreateGuiController.instance.tab_position);
        this.describeGuiController.clear();
        this.nowEditDataSet = null;
    }

    @Override
    public void nodeInit() {
        setUp();
        ImageCell imagecell_tutorialImage = new ImageCell();
        anchorpane_create.getChildren().add(imagecell_tutorialImage);
        try {
            var image_source = "/lib/image/";
            Image image = new Image(LoadUtil.getInstance().loadInternalImage(image_source + "tutorial_image.png"));
            imagecell_tutorialImage.setImage(image);
            imagecell_tutorialImage.setOnMouseClicked(t ->
                    anchorpane_create.getChildren().remove(imagecell_tutorialImage)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        scrollpane_background_order.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        pane_position_parent.getChildren().add(canvas);
        pane_position.getChildren().add(imagecell_background);

        colorpicker_background.getStyleClass().add("button");

        canvas.setMouseTransparent(true);
        canvas.toFront();
    }

    @Override
    public void localizationInit() {
        button_darkmode.setText(LocalizationUtil.getInstance().getLocalization("generalSetting.darkmode"));
        button_background_preserve_ratio.setText(LocalizationUtil.getInstance().getLocalization("generalSetting.background_preserve"));
    }

    public Vector2f getPositionFromMouse(double mouse_x, double mouse_y) {
        Point2D mousePoint = new Point2D(mouse_x, mouse_y);
        Point2D posInZoomTarget = pane_position.sceneToLocal(mousePoint);

        return new Vector2f((float) (platform.local_x + posInZoomTarget.getX()),
                (float) (platform.local_y + posInZoomTarget.getY()));
    }

    public void update() {
        describeGuiController.update();

        if (VariableDataBase.getInstance().isUpdated) {
            VariableDataBase.getInstance().isUpdated = false;
            List<String> name_list = new ArrayList<>();
            for (var key : VariableDataBase.getInstance().var_map.keySet()) {
                var value = VariableDataBase.getInstance().var_map.get(key);
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

    @Override
    public Class<CreatePlatform> getPlatformClass() {
        return CreatePlatform.class;
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
        describeGuiController.afterInit();
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
        platform = abstractPlatform;
    }

    @Override
    public ResizableCanvas getCanvas() {
        return canvas;
    }

    @Override
    public void eventInit() {
        colorpicker_background.valueProperty().addListener(e -> getPlatform().updateColor(colorpicker_background.getValue()));
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

        view_var_field.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                if (e.getClickCount() == 2) {
                    var variable = view_var_field.getSelectionModel().getSelectedIndex();
                    if (variable >= 0) {
                        var text = addTextIntoString(describeGuiController.text_editor.getText(), describeGuiController.text_editor.getAnchor(),
                                describeGuiController.text_editor.getCaretPosition(),
                                "{" + VariableDataBase.getInstance().var_map.keySet().toArray()[variable] + "}");
                        describeGuiController.text_editor.clear();
                        describeGuiController.text_editor.appendText(text);
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
                    SizeUtil.setSizeComplete(nowSizeChange);
                    nowSizeChange.getKey().needUpdate = true;

                    nowSizeChange.getKey().isClicked = false;
                    nowSizeChange.getValue().set();
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
                    nowMouseInDataSet.setSize(copyBound.width, copyBound.height);
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
                    SizeUtil.setSize(nowSizeChange, move.x(), move.y());
                }
            }
        });

        view_var_type.getItems().addAll("&b | boolean", " \"\" | string", "floor | 내림", "ceil | 올림", "round | 반올림");
        view_var_type.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                if (e.getClickCount() == 2) {
                    var index = view_var_type.getSelectionModel().getSelectedIndex();
                    var anchor = describeGuiController.text_editor.getAnchor();
                    var caret = describeGuiController.text_editor.getCaretPosition();
                    String text = switch (index) {
                        case 0 -> addTextIntoString(describeGuiController.text_editor.getText(), anchor, caret, "&b");
                        case 1 -> addTextIntoString(describeGuiController.text_editor.getText(), anchor, caret, "\" \"");
                        case 2 -> addTextIntoString(describeGuiController.text_editor.getText(), anchor, caret, "floor( )");
                        case 3 -> addTextIntoString(describeGuiController.text_editor.getText(), anchor, caret, "ceil( )");
                        case 4 -> addTextIntoString(describeGuiController.text_editor.getText(), anchor, caret, "round( )");
                        default -> "";
                    };
                    describeGuiController.text_editor.clear();
                    describeGuiController.text_editor.appendText(text);
                }
            }
        });
    }
}
