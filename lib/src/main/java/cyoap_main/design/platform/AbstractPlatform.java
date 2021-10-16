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
import cyoap_main.unit.Vector2f;
import cyoap_main.util.FlagUtil;
import cyoap_main.util.LoadUtil;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

@JsonAutoDetect(getterVisibility = Visibility.PUBLIC_ONLY)
public class AbstractPlatform {
    @JsonIgnore
    public List<ChoiceSet> choiceSetList = new ArrayList<>();

    public double local_x = 0;// local position of screen
    public double local_y = 0;
    public int min_x = -800;
    public int min_y = -1600;
    public int max_x = 800;
    public int max_y = 1600;
    public float scale = 1.0f;
    @JsonIgnore
    public float maximize = 2f;
    @JsonIgnore
    public float minimize = 0.9f;

    public double sensitivity = 1f;

    @JsonIgnore
    public double move_x = 0;
    @JsonIgnore
    public double move_y = 0;
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

    public void setUp(IPlatformGuiController guiController){
        this.guiController = guiController;
    }

    public SimpleEntry<Vector2f, Integer> checkLine(ChoiceSet choiceSet, float bias) {
        var x_min = choiceSet.pos_x;
        var y_min = choiceSet.pos_y;
        var x_max = x_min + choiceSet.getWidth();
        var y_max = y_min + choiceSet.getHeight();

        float x_new = Float.MAX_VALUE;
        float y_new = Float.MAX_VALUE;

        int i = 0;
        for (var choice : choiceSetList) {
            float sizex = Float.POSITIVE_INFINITY;
            float sizey = Float.POSITIVE_INFINITY;
            float l = 0;
            if (choice == choiceSet)
                continue;
            var x_min2 = choice.pos_x;
            var y_min2 = choice.pos_y;
            var x_max2 = x_min2 + choice.getWidth();
            var y_max2 = y_min2 + choice.getHeight();
            var x_half2 = x_min2 + choice.getWidth() / 2;
            var y_half2 = y_min2 + choice.getHeight() / 2;

            l = Math.abs(x_min - x_min2);
            if (l < bias && l < sizex) {
                x_new = x_min2;
                sizex = l;
            }
            l = Math.abs(x_min - x_max2);
            if (l < bias && l < sizex) {
                x_new = x_max2;
                sizex = l;
            }
            l = Math.abs(x_max - x_min2);
            if (l < bias && l < sizex) {
                x_new = x_min2 - choiceSet.getWidth();
                i = FlagUtil.setFlag(i, 0);
                sizex = l;
            }
            l = Math.abs(x_max - x_max2);
            if (l < bias && l < sizex) {
                x_new = x_max2 - choiceSet.getWidth();
                i = FlagUtil.setFlag(i, 0);
                sizex = l;
            }

            l = Math.abs(x_min - x_half2);
            if (l < bias && l < sizex) {
                x_new = x_half2;
                sizex = l;
            }
            l = Math.abs(x_max - x_half2);
            if (l < bias && l < sizex) {
                x_new = x_half2 - choiceSet.getWidth();
                i = FlagUtil.setFlag(i, 1);
                sizex = l;
            }

            l = Math.abs(y_min - y_min2);
            if (l < bias && l < sizey) {
                y_new = y_min2;
                sizey = l;
            }
            l = Math.abs(y_min - y_max2);
            if (l < bias && l < sizey) {
                y_new = y_max2;
                sizey = l;
            }
            l = Math.abs(y_max - y_min2);
            if (l < bias && l < sizey) {
                y_new = y_min2 - choiceSet.getHeight();
                i = FlagUtil.setFlag(i, 2);
                sizey = l;
            }
            l = Math.abs(y_max - y_max2);
            if (l < bias && l < sizey) {
                y_new = y_max2 - choiceSet.getHeight();
                i = FlagUtil.setFlag(i, 2);
                sizey = l;
            }

            l = Math.abs(y_min - y_half2);
            if (l < bias && l < sizey) {
                y_new = y_half2;
                sizey = l;
            }
            l = Math.abs(y_max - y_half2);
            if (l < bias && l < sizey) {
                y_new = y_half2 - choiceSet.getHeight();
                i = FlagUtil.setFlag(i, 3);
                sizey = l;
            }
        }
        if (x_new == Float.MAX_VALUE && y_new == Float.MAX_VALUE) {
            return null;
        } else if (x_new == Float.MAX_VALUE && y_new != Float.MAX_VALUE) {
            return new SimpleEntry<Vector2f, Integer>(new Vector2f(0, y_new), i);
        } else if (x_new != Float.MAX_VALUE && y_new == Float.MAX_VALUE) {
            return new SimpleEntry<Vector2f, Integer>(new Vector2f(x_new, 0), i);
        } else {
            return new SimpleEntry<Vector2f, Integer>(new Vector2f(x_new, y_new), i);
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
        gc.setLineWidth(1);
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
