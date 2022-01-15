package cyoap_main.platform;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import cyoap_main.controller.IGuiController;
import cyoap_main.controller.createGui.CreateGuiController;
import cyoap_main.core.JavaFxMain;
import cyoap_main.design.choice.ChoiceSet;
import cyoap_main.design.node_extension.ImageCell;
import cyoap_main.unit.Vector2f;
import cyoap_main.util.FlagUtil;
import cyoap_main.util.LoadUtil;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(getterVisibility = Visibility.PUBLIC_ONLY)
public class AbstractPlatform {

    @JsonIgnore
    public boolean isEditable() {
        return false;
    }

    @JsonIgnore
    public List<ChoiceSet> choiceSetList = new ArrayList<>();

    @JsonIgnore
    public static final Color baseColor = Color.WHITE;
    public Color color_background;

    public double local_x = 0;// local position of screen
    public double local_y = 0;
    public float min_x = -800;
    public float min_y = -1600;
    public float max_x = 800;
    public float max_y = 1600;
    public float scale = 1.0f;
    @JsonIgnore
    public float maximize = 2.5f;
    @JsonIgnore
    public float minimize = 0.5f;
    @JsonIgnore
    public double sensitivity = 1f;
    @JsonIgnore
    public double start_mouse_x = 0;
    @JsonIgnore
    public double start_mouse_y = 0;

    @JsonIgnore
    public Image background_image;

    public String string_image_name;
    @JsonIgnore
    public boolean isImageChanged = true;
    @JsonIgnore
    public boolean isSizeChanged = true;
    @JsonIgnore
    public boolean isMouseMoved = true;

    @JsonIgnore
    public static final int flagPosition_background_preserve_ratio = 0;

    @JsonIgnore
    public IGuiController guiController;
    public int flag = 0;

    public boolean needUpdate = true;

    public AbstractPlatform(IGuiController guiController) {
        this.guiController = guiController;
    }

    /**
     * @return setting position, line position
     **/
    public SimpleEntry<Vector2f, Vector2f> checkLine(ChoiceSet choiceSet, float bias) {
        float x_new = Float.MAX_VALUE;
        float y_new = Float.MAX_VALUE;
        float x_line = Float.MAX_VALUE;
        float y_line = Float.MAX_VALUE;
        var vec_original_x = choiceSet.get_snapList_x();
        var vec_original_y = choiceSet.get_snapList_y();

        for (var choice : choiceSetList) {
            float size_x = Float.POSITIVE_INFINITY;
            float size_y = Float.POSITIVE_INFINITY;
            if (choice == choiceSet)
                continue;

            var vec_sub_x = choice.get_snapList_x();
            var vec_sub_y = choice.get_snapList_y();

            for (var v_ori_x : vec_original_x) {
                for (var v_sub_x : vec_sub_x) {
                    float l = Math.abs(v_ori_x - v_sub_x);
                    if (l < bias && l < size_x) {
                        size_x = l;
                        x_new = v_sub_x - (v_ori_x - choiceSet.bound.x);
                        x_line = v_sub_x;
                    }
                }
            }
            for (var v_ori_y : vec_original_y) {
                for (var v_sub_y : vec_sub_y) {
                    float l = Math.abs(v_ori_y - v_sub_y);
                    if (l < bias && l < size_y) {
                        size_y = l;
                        y_new = v_sub_y - (v_ori_y - choiceSet.bound.y);
                        y_line = v_sub_y;
                    }
                }
            }
        }
        if (x_new == Float.MAX_VALUE && y_new == Float.MAX_VALUE) {
            return null;
        } else if (x_new == Float.MAX_VALUE) {
            return new SimpleEntry<>(new Vector2f(0, y_new), new Vector2f(x_line, y_line));
        } else if (y_new == Float.MAX_VALUE) {
            return new SimpleEntry<>(new Vector2f(x_new, 0), new Vector2f(x_line, y_line));
        } else {
            return new SimpleEntry<>(new Vector2f(x_new, y_new), new Vector2f(x_line, y_line));
        }
    }

    public Vector2f checkPoint(ChoiceSet choiceSet, Vector2f point, float bias) {
        float x_new = Float.MAX_VALUE;
        float y_new = Float.MAX_VALUE;

        for (var choice : choiceSetList) {
            float size_x = Float.POSITIVE_INFINITY;
            float size_y = Float.POSITIVE_INFINITY;
            if (choice == choiceSet)
                continue;

            var vec_sub_x = choice.get_snapList_x();
            var vec_sub_y = choice.get_snapList_y();

            for (var v_sub_x : vec_sub_x) {
                float l = Math.abs(point.x() - v_sub_x);
                if (l < bias && l < size_x) {
                    size_x = l;
                    x_new = v_sub_x;
                }
            }
            for (var v_sub_y : vec_sub_y) {
                float l = Math.abs(point.y() - v_sub_y);
                if (l < bias && l < size_y) {
                    size_y = l;
                    y_new = v_sub_y;
                }
            }
        }
        if (x_new == Float.MAX_VALUE && y_new == Float.MAX_VALUE) {
            return null;
        }
        return new Vector2f(x_new, y_new);
    }

    public AbstractPlatform() {
    }

    public void setUp(IGuiController guiController) {
        this.guiController = guiController;
    }

    public void clearNodeOnPanePosition() {
        choiceSetList.clear();
        guiController.getChoicePane().getChildren().clear();
        guiController.getChoicePane().getChildren().add(guiController.getBackgroundImageCellList().get(0));
    }

    public void setNodeDepth() {
        for (var c : choiceSetList) {
            var gui = c.guiComponent.pane;
            gui.setViewOrder(0.0d);
        }
        for (var image : guiController.getBackgroundImageCellList()) {
            image.setViewOrder(10.d);
        }
    }

    public void update() {
        if (isImageChanged) {
            isImageChanged = false;
            if (string_image_name != null) {
                var image = LoadUtil.loadImage(string_image_name);
                background_image = image.getKey();
                string_image_name = image.getValue();

                guiController.getBackgroundImageCellList().get(0).setImage(background_image);
            }
            if (guiController instanceof CreateGuiController create_gui) {
                for (var controller_imageCell : guiController.getBackgroundImageCellList()) {
                    create_gui.vbox_background_order.getChildren().clear();
                    var imageCell = new ImageCell(controller_imageCell.getImage());
                    imageCell.pos_x = controller_imageCell.pos_x;
                    imageCell.pos_y = controller_imageCell.pos_y;
                    imageCell.maxWidthProperty().bind(create_gui.scrollpane_background_order.widthProperty());
                    create_gui.vbox_background_order.getChildren().add(imageCell);
                }
            }

        }
        if (isSizeChanged) {
            isSizeChanged = false;
            guiController.getCanvas().setWidth(max_x - min_x);
            guiController.getCanvas().setHeight(max_y - min_y);
            guiController.getCanvas().setLayoutX(min_x);
            guiController.getCanvas().setLayoutY(min_y);
            for (var background : guiController.getBackgroundImageCellList()) {
                background.setPrefWidth(max_x - min_x);
                background.setPrefHeight(max_y - min_y);
                background.pos_x = min_x;
                background.pos_y = min_y;
                background.relocate(background.pos_x, background.pos_y);//center 기준
            }
            isMouseMoved = true;
        }
        if (isMouseMoved) {
            isMouseMoved = false;
            guiController.getChoicePaneParent().setScaleX(scale);
            guiController.getChoicePaneParent().setScaleY(scale);
            updateMouseCoordinate();
        }

        if (needUpdate) {
            needUpdate = false;
            setNodeDepth();
        }
        choiceSetList.forEach(c -> {
            if (c.needUpdate) c.update();
        });
    }

    public void render(GraphicsContext gc, double time) {
        choiceSetList.forEach(d -> d.render(gc, time));
        gc.setStroke(Color.INDIANRED);
        gc.setLineWidth(3);
        gc.setLineDashes(5);
        gc.setLineDashOffset((time * 20) % 1000);
        gc.strokeRect(0, 0, max_x - min_x, max_y - min_y);
    }

    public void updateMouseCoordinate() {
        choiceSetList.forEach(d -> d.updateCoordinate(-local_x, -local_y));

        for (var node_background : guiController.getBackgroundImageCellList()) {
            node_background.setTranslateX(-local_x);
            node_background.setTranslateY(-local_y);
        }
        guiController.getCanvas().setTranslateX(-local_x);
        guiController.getCanvas().setTranslateY(-local_y);
    }

    public void updateTranslationAll(double x, double y) {
        this.isMouseMoved = true;
        for (var node : choiceSetList) {
            node.updateCoordinate(x, y);
        }

        for (var node_background : guiController.getBackgroundImageCellList()) {
            node_background.setTranslateX(x);
            node_background.setTranslateY(y);
        }

        guiController.getCanvas().setTranslateX(min_x - x);
        guiController.getCanvas().setTranslateY(min_y - y);
    }

    public void save() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(JavaFxMain.instance.directory.getAbsolutePath() + "/platform.json"),
                    StandardCharsets.UTF_8);
            objectMapper.writeValue(writer, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateFlag() {
        boolean b = FlagUtil.getFlag(flag, flagPosition_background_preserve_ratio);
        for (var l : guiController.getBackgroundImageCellList()) {
            l.isPreserveRatio = b;
            l.requestLayout();
        }
    }

    public String getColor_background() {
        return color_background.toString();
    }

    public void setColor_background(String s) {
        updateColor(Color.web(s));
    }

    public void updateColor(Color color_new) {
        color_background = color_new;
        if (guiController != null)
            guiController.getChoicePaneParent().setStyle("-fx-background-color: " + color_new.toString().replace("0x", "#") + ";");
    }

    @JsonIgnore
    public float getWidth() {
        return max_x - min_x;
    }

    @JsonIgnore
    public float getHeight() {
        return max_y - min_y;
    }
}
