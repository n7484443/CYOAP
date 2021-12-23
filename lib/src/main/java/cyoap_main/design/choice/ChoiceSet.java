package cyoap_main.design.choice;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.*;
import org.fxmisc.richtext.model.StyledSegment;

import cyoap_main.controller.createGui.CreateGuiController;
import cyoap_main.unit.Bound2f;
import cyoap_main.unit.Vector2f;
import cyoap_main.util.FlagUtil;
import cyoap_main.util.LoadUtil;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ChoiceSet {
    @JsonIgnore
    public static final Color baseColor = Color.web("#E1E3ED");
    @JsonIgnore
    public static final int flagPosition_selectable = 0;
    @JsonIgnore
    public static final int flagPosition_horizontal = 1;
    @JsonIgnore
    public static final int flagPosition_emptyImage = 2;
    @JsonIgnore
    public final float minWidth = 150;
    @JsonIgnore
    public final float minHeight = 150;
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
    public float width;
    public float height;
    public boolean isClicked = false;

    public int round = 0;

    public Bound2f bound;

    public ChoiceSet() {
        this("title", null, 0, 0, 0, 0);
    }

    public ChoiceSet(String title, String image) {
        this(title, image, 0, 0, 0, 0);
    }

    public ChoiceSet(float pos_x, float pos_y) {
        this("title", null, pos_x, pos_y, 0, 0);
    }

    public ChoiceSet(String title) {
        this(title, null, 0, 0, 0, 0);
    }

    public ChoiceSet(String title, String image_name, float pos_x, float pos_y, float width, float height) {
        this.string_title = title;
        this.string_image_name = image_name;
        if (width < minWidth) {
            this.width = minWidth;
        } else {
            this.width = width;
        }
        if (width < minWidth) {
            this.height = minHeight;
        } else {
            this.height = height;
        }

        bound = new Bound2f(pos_x, pos_y, getWidth(), getHeight());
    }

    public void setUp(Pane pane_mother) {
        guiComponent.color = baseColor;
        guiComponent.setUp();
        pane_mother.getChildren().add(guiComponent.pane);
        update();
    }

    public boolean check_intersect(ChoiceSet a, float x, float y) {
        return a.bound.intersect(new Vector2f(x, y));
    }

    public float getWidth() {
        width = (float) getAnchorPane().getLayoutBounds().getWidth();
        return width;
    }

    public void setWidth(float width) {
        if (width < minWidth) {
            this.width = minWidth;
        } else {
            this.width = width;
        }
        getAnchorPane().setPrefWidth(this.width);
        getAnchorPane().requestLayout();
    }

    public float getHeight() {
        height = (float) getAnchorPane().getLayoutBounds().getHeight();
        return height;
    }

    public void setHeight(float height) {
        if (height < minHeight) {
            this.height = minHeight;
        } else {
            this.height = height;
        }
        getAnchorPane().setPrefHeight(this.height);
        getAnchorPane().requestLayout();
    }

    public void update() {
        updateFlag();
        updateSize();
        updateBounds();
        updateColor();
        updateSegment();
        guiComponent.update();
    }

    public void updateSegment() {
        LoadUtil.loadSegment(guiComponent.area, this.segmentList);
    }

    public void updateSize() {
        setWidth(width);
        setHeight(height);
        updateSizeFrom();
    }

    public void updateSizeFrom() {
        width = (float) getAnchorPane().getLayoutBounds().getWidth();
        height = (float) getAnchorPane().getLayoutBounds().getHeight();
    }

    public void updateBounds() {
        bound.width = getWidth();
        bound.height = getHeight();
    }

    public void combineSubChoiceSet(ChoiceSet sub) {
        CreateGuiController.platform.choiceSetList.remove(sub);

        this.choiceSet_child.add(sub);
        sub.choiceSet_parent = this;

        guiComponent.combineSubChoiceSetComponent(sub);

        sub.update();
        update();
    }

    public void separateSubChoiceSet(ChoiceSet sub) {
        CreateGuiController.platform.choiceSetList.add(sub);

        this.choiceSet_child.remove(sub);
        sub.choiceSet_parent = null;

        guiComponent.separateSubChoiceSetComponent(sub);
    }

    public void updateColor(Color t) {
        if (this.color != t) {
            this.color = t;
            this.guiComponent.color = t;
            this.guiComponent.updateColor();
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
        updateBounds();
    }

    public void setPosition(float pos_x, float pos_y) {
        bound.x = pos_x;
        bound.y = pos_y;
        guiComponent.setPosition(bound.x, bound.y);
        updateBounds();
    }

    @JsonIgnore
    public Pane getAnchorPane() {
        return guiComponent.pane;
    }

    public String getColor() {
        return color.toString();
    }

    public void setColor(String s) {
        updateColor(Color.web(s));
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
                var splitStr = v.split("\\}:\\{");
                splitStr[0] = splitStr[0].substring(1);
                splitStr[1] = splitStr[1].substring(0, splitStr[1].length() - 1);
                var segment = new StyledSegment<>(splitStr[0], splitStr[1]);
                segmentList.add(segment);
            }
        }
    }

    public void changeSize(float w, float h) {
        setWidth(w);
        setHeight(h);
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
