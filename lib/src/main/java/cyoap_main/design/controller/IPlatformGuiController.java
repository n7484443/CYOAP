package cyoap_main.design.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;

import cyoap_main.core.JavaFxMain;
import cyoap_main.design.choice.ChoiceSet;
import cyoap_main.design.node_extension.ImageCell;
import cyoap_main.design.platform.AbstractPlatform;
import cyoap_main.util.LoadUtil;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public interface IPlatformGuiController extends Initializable {
    ImageCell getBackgroundImageView();

    Pane getChoicePane();

    AbstractPlatform getPlatform();

    Canvas getCanvas();

    void update();

    boolean isEditable();

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
                data.update();
                getPlatform().choiceSetList.add(data);
                data.updateFlag();
                LoadUtil.loadSegment(data.guiComponent.area, data.segmentList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (var v : getPlatform().choiceSetList) {
            LoadUtil.setupChoiceSet(v);
        }
        for (var v : getPlatform().choiceSetList) {
            LoadUtil.loadChoiceSetParents(v);
        }
        getPlatform().update();
    }

    default void setUp() {
        this.getChoicePane().setOnScroll(e -> {
            getPlatform().scale += (e.getDeltaY() / 40.0) / 8;
            if (getPlatform().scale <= getPlatform().minimize)
                getPlatform().scale = getPlatform().minimize;
            if (getPlatform().scale >= getPlatform().maximize)
                getPlatform().scale = getPlatform().maximize;
        });
    }

    default void updateMouseCoord(double move_x, double move_y, double start_move_x, double start_move_y) {
        getPlatform().local_x -= move_x;
        getPlatform().local_y -= move_y;
        getPlatform().start_mouse_x = start_move_x;
        getPlatform().start_mouse_y = start_move_y;

        var bound = getChoicePane().getParent().getParent().getBoundsInLocal();
        var real_width = bound.getWidth();
        var real_height = bound.getHeight();

        if (getPlatform().local_x + real_width >= getPlatform().max_x)
            getPlatform().local_x = getPlatform().max_x - real_width;
        if (getPlatform().local_y + real_height >= getPlatform().max_y)
            getPlatform().local_y = getPlatform().max_y - real_height;
        if (getPlatform().local_x <= getPlatform().min_x)
            getPlatform().local_x = getPlatform().min_x;
        if (getPlatform().local_y <= getPlatform().min_y)
            getPlatform().local_y = getPlatform().min_y;

        getPlatform().updateMouseCoordinate();
    }
}
