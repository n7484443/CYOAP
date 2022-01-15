package cyoap_main.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Floats;
import cyoap_main.core.JavaFxMain;
import cyoap_main.design.choice.ChoiceSet;
import cyoap_main.design.node_extension.ImageCell;
import cyoap_main.platform.AbstractPlatform;
import cyoap_main.unit.Bound2f;
import cyoap_main.util.LoadUtil;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

public interface IGuiController extends IController {
    List<ImageCell> getBackgroundImageCellList();

    Pane getChoicePane();

    Pane getChoicePaneParent();

    void setPlatform(AbstractPlatform abstractPlatform);

    AbstractPlatform getPlatform();

    Canvas getCanvas();

    void update();

    <T> Class<? extends AbstractPlatform> getPlatformClass();

    default void load() {
        getPlatform().clearNodeOnPanePosition();
        var path = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/choiceSet");
        if (!path.exists())
            return;
        var file_list = Stream.of(path.list()).filter(name -> name.endsWith(".json")).toList();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            for (var file : file_list) {
                InputStreamReader writer = new InputStreamReader(
                        new FileInputStream(path.getAbsolutePath() + "/" + file), StandardCharsets.UTF_8);

                var data = objectMapper.readValue(writer, ChoiceSet.class);
                data.setUp(getChoicePane());
                getPlatform().choiceSetList.add(data);
                LoadUtil.loadSegment(data.guiComponent.area, data.segmentList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (var v : getPlatform().choiceSetList) {
            LoadUtil.setupChoiceSet(v);
        }
        getPlatform().update();
    }

    default void loadPlatform() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File file_platform_json = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/platform.json");
            if (file_platform_json.exists()) {
                var writer = new InputStreamReader(new FileInputStream(JavaFxMain.instance.directory.getAbsolutePath() + "/platform.json"), StandardCharsets.UTF_8);
                setPlatform(objectMapper.readValue(writer, getPlatformClass()));
                getPlatform().setUp(this);
                getPlatform().isImageChanged = true;
                getPlatform().updateFlag();
                loadPlatformSetup();

            } else {
                getPlatform().local_x = -getChoicePaneRealWidth() / 2;
                getPlatform().local_y = -getChoicePaneRealHeight() / 2;
                getChoicePane().setPrefSize(getPlatform().max_x - getPlatform().min_x, getPlatform().max_y - getPlatform().min_y);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    default void loadPlatformSetup() {
    }

    default void setUp() {
        this.getChoicePane().setOnScroll(e -> {
            getPlatform().scale += (e.getDeltaY() / 40.0) / 8;
            if (getPlatform().scale <= getPlatform().minimize)
                getPlatform().scale = getPlatform().minimize;
            if (getPlatform().scale >= getPlatform().maximize)
                getPlatform().scale = getPlatform().maximize;
            getPlatform().isMouseMoved = true;
        });
    }

    int allow_margin = 50;

    @SuppressWarnings("UnstableApiUsage")
    default void updateMouseCoord(double move_x, double move_y, double start_move_x, double start_move_y) {
        getPlatform().local_x -= move_x;
        getPlatform().local_y -= move_y;
        getPlatform().start_mouse_x = start_move_x;
        getPlatform().start_mouse_y = start_move_y;

        var l_x = (float) getPlatform().local_x;
        var l_y = (float) getPlatform().local_y;

        var real_width = (float) getChoicePaneRealWidth();
        var real_height = (float) getChoicePaneRealHeight();

        var bound = new Bound2f(getPlatform().min_x - allow_margin, getPlatform().min_y - allow_margin,
                getPlatform().getWidth() + allow_margin * 2, getPlatform().getHeight() + allow_margin * 2);
        bound.mul(getPlatform().scale);

        if (bound.width < real_width) {
            getPlatform().local_x = bound.x;
        } else {
            getPlatform().local_x = Floats.constrainToRange(l_x, bound.x, bound.x + bound.width - real_width);
        }
        if (bound.height < real_height) {
            getPlatform().local_y = bound.y;
        } else {
            getPlatform().local_y = Floats.constrainToRange(l_y, bound.y, bound.y + bound.height - real_height);
        }

        getPlatform().updateMouseCoordinate();
        getPlatform().isMouseMoved = true;
    }

    default double getChoicePaneRealWidth() {
        return getChoicePane().getParent().getParent().getBoundsInLocal().getWidth();
    }

    default double getChoicePaneRealHeight() {
        return getChoicePane().getParent().getParent().getBoundsInLocal().getHeight();
    }
}
