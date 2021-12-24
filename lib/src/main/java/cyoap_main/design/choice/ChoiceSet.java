package cyoap_main.design.choice;

import com.fasterxml.jackson.annotation.*;
import cyoap_main.controller.createGui.CreateGuiController;
import cyoap_main.unit.Bound2f;
import cyoap_main.unit.Vector2f;
import cyoap_main.util.FlagUtil;
import cyoap_main.util.LoadUtil;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.model.StyledSegment;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ChoiceSet {
    public static final Color baseColor = Color.web("#E1E3ED");
    public static final int flagPosition_selectable = 0;
    public static final int flagPosition_horizontal = 1;
    public static final int flagPosition_emptyImage = 2;
    public static final Vector2f default_size = new Vector2f(150, 150);

    public String string_title;
    public List<StyledSegment<String, String>> segmentList = new ArrayList<>();
    public String string_image_name;
    public Color color = baseColor;// blue
    @JsonIgnore
    public ChoiceSetGuiComponent guiComponent = new ChoiceSetGuiComponent(this);
    public int flag = flagPosition_selectable;
    @JsonManagedReference
    public List<ChoiceSet> choiceSet_child = new ArrayList<>();
    @JsonBackReference
    public ChoiceSet choiceSet_parent = null;
    public boolean isClicked = false;

    public int round = 0;

    public Bound2f bound;

    @JsonIgnore
    public boolean needUpdate = true;

    public ChoiceSet() {
        this("title", null, 0, 0, 0, 0);
    }

    public ChoiceSet(String title, String image) {
        this(title, image, 0, 0, 0, 0);
    }

    public ChoiceSet(float pos_x, float pos_y) {
        this("title", null, pos_x, pos_y, 0, 0);
    }

    public ChoiceSet(String title, float pos_x, float pos_y, float width, float height) {
        this(title, null, pos_x, pos_y, width, height);
    }

    public ChoiceSet(String title, String image_name, float pos_x, float pos_y, float width, float height) {
        this.string_title = title;
        this.string_image_name = image_name;
        bound = new Bound2f(pos_x, pos_y, width, height);
        if (width < guiComponent.pane.getMinWidth()) {
            bound.width = (float) guiComponent.pane.getMinWidth();
        }
        if (height < guiComponent.pane.getMinHeight()) {
            bound.height = (float) guiComponent.pane.getMinHeight();
        }
    }

    public void setUp(Pane pane_mother) {
        guiComponent.color = baseColor;
        guiComponent.setUp();
        pane_mother.getChildren().add(guiComponent.pane);
        needUpdate = true;
    }

    public boolean check_intersect(ChoiceSet a, float x, float y) {
        return a.bound.intersect(new Vector2f(x, y));
    }

    public void setSize(float width, float height) {
        bound.width = width;
        bound.height = height;

        var width_min = (float) Math.max(guiComponent.pane.getMinWidth(), 50);
        var height_min = (float) Math.max(guiComponent.pane.getMinHeight(), 50);
        if (width < width_min) {
            bound.width = width_min;
        }
        if (height < height_min) {
            bound.height = height_min;
        }

        getAnchorPane().setPrefWidth(bound.width);
        getAnchorPane().setPrefHeight(bound.height);
        getAnchorPane().requestLayout();

        needUpdate = true;
    }

    @JsonIgnore
    public float getHeight() {
        return bound.height;
    }

    @JsonIgnore
    public float getWidth() {
        return bound.width;
    }

    public void update() {
        updateFlag();
        setSize(bound.width, bound.height);
        updateColor();
        updateSegment();
        guiComponent.update();
        needUpdate = false;
    }

    public void updateSegment() {
        LoadUtil.loadSegment(guiComponent.area, this.segmentList);
    }

    public void combineSubChoiceSet(ChoiceSet sub) {
        CreateGuiController.platform.choiceSetList.remove(sub);

        this.choiceSet_child.add(sub);
        sub.choiceSet_parent = this;

        guiComponent.combineSubChoiceSetComponent(sub);

        needUpdate = true;
    }

    public void separateSubChoiceSet(ChoiceSet sub) {
        CreateGuiController.platform.choiceSetList.add(sub);

        this.choiceSet_child.remove(sub);
        sub.choiceSet_parent = null;

        guiComponent.separateSubChoiceSetComponent(sub);
        needUpdate = true;
    }

    public void changeColor(Color t) {
        if (this.color != t) {
            this.color = t;
            this.guiComponent.color = t;
            this.guiComponent.updateColor();
            needUpdate = true;
        }
    }

    public void updateColor() {
        this.guiComponent.color = color;
        this.guiComponent.updateColor();
    }

    public void updateFlag() {
        guiComponent.setBorder(FlagUtil.getFlag(flag, flagPosition_selectable));
        guiComponent.setHorizontal(FlagUtil.getFlag(flag, flagPosition_horizontal));
        guiComponent.setEmptyImage(FlagUtil.getFlag(flag, flagPosition_emptyImage));
    }

    // 화면상의 위치
    public void updateCoordinate(double move_x, double move_y) {
        guiComponent.setTranslation(move_x, move_y);
    }

    // 실제 위치
    public void updatePosition(double move_x, double move_y) {
        bound.x += move_x;
        bound.y += move_y;
        guiComponent.setPosition(bound.x, bound.y);
        setSize(bound.width, bound.height);
        needUpdate = true;
    }

    public void setPosition(float pos_x, float pos_y) {
        bound.x = pos_x;
        bound.y = pos_y;
        guiComponent.setPosition(bound.x, bound.y);
        setSize(bound.width, bound.height);
        needUpdate = true;
    }

    @JsonIgnore
    public Pane getAnchorPane() {
        return guiComponent.pane;
    }

    public String getColor() {
        return color.toString();
    }

    public void setColor(String s) {
        changeColor(Color.web(s));
    }

    public void render(GraphicsContext gc, double time) {
        this.guiComponent.render(gc, time);
    }

    @JsonGetter("segmentList")
    @JsonProperty("segmentList")
    public List<String> getSegmentList() {
        List<String> strList = new ArrayList<>();
        if (segmentList.isEmpty())
            return strList;
        for (var v : segmentList) {
            if (v == null) {
                strList.add("");
            } else {
                strList.add("{" + v.getSegment() + "}:{" + v.getStyle() + "}");
            }
        }
        return strList;
    }

    @JsonSetter("segmentList")
    public void setSegmentList(List<String> s) {
        segmentList.clear();
        for (var v : s) {
            if (v.isBlank()) {
                segmentList.add(null);
            } else {
                var splitStr = v.split("}:\\{");
                splitStr[0] = splitStr[0].substring(1);
                splitStr[1] = splitStr[1].substring(0, splitStr[1].length() - 1);
                var segment = new StyledSegment<>(splitStr[0], splitStr[1]);
                segmentList.add(segment);
            }
        }
        needUpdate = true;
    }

    @JsonIgnore
    public List<Float> get_snapList_x() {
        List<Float> arrayList = new ArrayList<>();
        arrayList.add(bound.x);
        arrayList.add(bound.x + getWidth());
        arrayList.add(bound.x + getWidth() / 2f);
        return arrayList;
    }

    @JsonIgnore
    public List<Float> get_snapList_y() {
        List<Float> arrayList = new ArrayList<>();
        arrayList.add(bound.y);
        arrayList.add(bound.y + getHeight());
        arrayList.add(bound.y + getHeight() / 2f);
        return arrayList;
    }
}
