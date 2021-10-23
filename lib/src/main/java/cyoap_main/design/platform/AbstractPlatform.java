package cyoap_main.design.platform;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.databind.ObjectMapper;
import cyoap_main.core.JavaFxMain;
import cyoap_main.design.choice.ChoiceSet;
import cyoap_main.design.controller.IPlatformGuiController;
import cyoap_main.design.controller.createGui.CreateGuiController;
import cyoap_main.unit.Vector2f;
import cyoap_main.util.FlagUtil;
import cyoap_main.util.LoadUtil;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

@JsonAutoDetect(getterVisibility = Visibility.PUBLIC_ONLY)
public class AbstractPlatform {
    @JsonIgnore
    public List<ChoiceSet> choiceSetList = new ArrayList<>();

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
    public boolean isImageChanged = false;

    @JsonIgnore
    public static final int flagPosition_background_preserve_ratio = 0;

    @JsonIgnore
    public IPlatformGuiController guiController;
    public int flag = 0;

    public boolean needUpdate = true;

    public void setUp(IPlatformGuiController guiController) {
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

        int i = 0;
        for (var choice : choiceSetList) {
            float size_x = Float.POSITIVE_INFINITY;
            float size_y = Float.POSITIVE_INFINITY;
            float l = 0;
            if (choice == choiceSet)
                continue;

            var vec_sub_x = choice.get_snapList_x();
            var vec_sub_y = choice.get_snapList_y();

            for (var v_ori_x : vec_original_x) {
                for (var v_sub_x : vec_sub_x) {
                    l = Math.abs(v_ori_x - v_sub_x);
                    if (l < bias && l < size_x) {
                        size_x = l;
                        x_new = v_sub_x - (v_ori_x - choiceSet.pos_x);
                        x_line = v_sub_x;
                    }
                }
            }
            for (var v_ori_y : vec_original_y) {
                for (var v_sub_y : vec_sub_y) {
                    l = Math.abs(v_ori_y - v_sub_y);
                    if (l < bias && l < size_y) {
                        size_y = l;
                        y_new = v_sub_y - (v_ori_y - choiceSet.pos_y);
                        y_line = v_sub_y;
                    }
                }
            }
        }
        if (x_new == Float.MAX_VALUE && y_new == Float.MAX_VALUE) {
            return null;
        } else if (x_new == Float.MAX_VALUE && y_new != Float.MAX_VALUE) {
            return new SimpleEntry<Vector2f, Vector2f>(new Vector2f(0, y_new), new Vector2f(x_line, y_line));
        } else if (x_new != Float.MAX_VALUE && y_new == Float.MAX_VALUE) {
            return new SimpleEntry<Vector2f, Vector2f>(new Vector2f(x_new, 0), new Vector2f(x_line, y_line));
        } else {
            return new SimpleEntry<Vector2f, Vector2f>(new Vector2f(x_new, y_new), new Vector2f(x_line, y_line));
        }
    }

    public AbstractPlatform() {
    }

    public AbstractPlatform(IPlatformGuiController guiController) {
        this.guiController = guiController;
    }

    public void clearNodeOnPanePosition() {
        choiceSetList.clear();
        guiController.getChoicePane().getChildren().clear();
        guiController.getChoicePane().getChildren().add(guiController.getBackgroundImageView());
    }

    public void setNodeDepth() {
        for (var c : choiceSetList) {
            var gui = c.guiComponent.pane;
            gui.setViewOrder(0.0d);
        }
        guiController.getBackgroundImageView().setViewOrder(10.0d);
    }

    public void update() {
        if (isImageChanged) {
            isImageChanged = false;
            if (string_image_name != null) {
                var image = LoadUtil.loadImage(string_image_name);
                background_image = image.getKey();
                string_image_name = image.getValue();

                guiController.getBackgroundImageView().setImage(background_image);
            }
            guiController.getBackgroundImageView().setPrefWidth(max_x - min_x);
            guiController.getBackgroundImageView().setPrefHeight(max_y - min_y);
        }
        guiController.getChoicePane().setScaleX(scale);
        guiController.getChoicePane().setScaleY(scale);
        updateMouseCoordinate();
        setNodeDepth();
        if (needUpdate) {
            needUpdate = false;
            choiceSetList.forEach(d -> {
                d.updateSizeFrom();
                d.updateSize();
            });
        }
    }

    public void render(GraphicsContext gc, double time) {
        choiceSetList.forEach(d -> d.render(gc, time));
        gc.setStroke(Color.INDIANRED);
        gc.setLineWidth(3);
        gc.setLineDashes(5);
        gc.setLineDashOffset((time * 20) % 1000);
        gc.strokeRect(min_x - local_x, min_y - local_y, max_x - min_x, max_y - min_y);
    }

    public void updateMouseCoordinate() {
        choiceSetList.forEach(d -> d.updateCoordinate(-local_x, -local_y));
        guiController.getBackgroundImageView().relocate(min_x - local_x, min_y - local_y);
    }

    public void updateCoordinateAll(double x, double y) {
        for (var node : choiceSetList) {
            node.updateCoordinate(x, y);
        }
        guiController.getBackgroundImageView().relocate(min_x - local_x + x, min_y - local_y + y);
    }

    public void updatePositionAll(double x, double y) {
        for (var node : choiceSetList) {
            node.updatePosition(x, y);
        }
        guiController.getBackgroundImageView().relocate(min_x - local_x + x, min_y - local_y + y);
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

    public void updateFlag(){
        guiController.getBackgroundImageView().isPreserveRatio = FlagUtil.getFlag(flag, flagPosition_background_preserve_ratio);
        guiController.getBackgroundImageView().requestLayout();
    }
}
