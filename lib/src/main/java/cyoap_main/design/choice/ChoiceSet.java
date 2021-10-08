package cyoap_main.design.choice;

import java.util.ArrayList;
import java.util.List;

import org.fxmisc.richtext.model.StyledSegment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import cyoap_main.design.controller.createGui.CreateGuiController;
import cyoap_main.unit.Bound2f;
import cyoap_main.unit.Vector2f;
import cyoap_main.util.FlagUtil;
import cyoap_main.util.LoadUtil;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ChoiceSet {
	public String string_title;

	public List<StyledSegment<String, String>> segmentList = new ArrayList<>();

	public String string_image_name;

	@JsonIgnore
	public static final Color baseColor = Color.web("#E1E3ED");

	public Color color = baseColor;// blue

	@JsonIgnore
	public ChoiceSetGuiComponent guiComponent = new ChoiceSetGuiComponent(color);

	@JsonIgnore
	public static final int flagPosition_selectable = 0;
	@JsonIgnore
	public static final int flagPosition_horizontal = 1;
	@JsonIgnore
	public static final int flagPosition_emptyimage = 2;

	public int flag = flagPosition_selectable;

	@JsonManagedReference
	public List<ChoiceSet> choiceSet_child = new ArrayList<>();

	@JsonBackReference
	public ChoiceSet choiceSet_parent = null;

	public float pos_x;
	public float pos_y;
	
	public float width;
	public float height;

	@JsonIgnore
	public float minWidth = 200;
	@JsonIgnore
	public float minHeight = 250;

	public boolean isClicked = false;

	@JsonIgnore
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

	public ChoiceSet(String title, String image_name, float pos_x, float posy, float width, float height) {
		this.string_title = title;
		this.string_image_name = image_name;
		this.pos_x = pos_x;
		this.pos_y = posy;
		this.width = width;
		this.height = height;

		bound = new Bound2f(pos_x, posy, getWidth(), getHeight());
	}

	public void setUp(Pane pane_mother) {
		guiComponent.setUp(this);
		pane_mother.getChildren().add(guiComponent.pane);
	}

	public boolean check_intersect(ChoiceSet a, ChoiceSet b) {
		return a.bound.intersect(b.bound);
	}

	public boolean check_intersect(ChoiceSet a, float x, float y) {
		return a.bound.intersect(new Vector2f(x, y));
	}
	
	public float getWidth() {
		if (width < minWidth) {
			width = minWidth;
		}
		guiComponent.pane.setPrefWidth(width);
		return width;
	}
	
	public float getHeight() {
		if (height < minHeight) {
			height = minHeight;
		}
		guiComponent.pane.setPrefHeight(height);
		return height;
	}

	public void update() {
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
		getWidth();
		getHeight();
	}
	public void updateSizeFrom() {
		width = (float) getAnchorPane().getLayoutBounds().getWidth();
		height = (float) getAnchorPane().getLayoutBounds().getHeight();
	}

	public void updateBounds() {
		bound.x = pos_x;
		bound.y = pos_y;
		bound.width = getWidth();
		bound.height = getHeight();
	}

	public void combineSubChoiceSet(ChoiceSet sub) {
		CreateGuiController.platform.choiceSetList.remove(sub);

		this.choiceSet_child.add(sub);
		sub.choiceSet_parent = this;

		guiComponent.combineSubChoiceSetComponenet(sub);
	}

	public void seperateSubChoiceSet(ChoiceSet sub) {
		this.choiceSet_child.remove(sub);
		CreateGuiController.platform.choiceSetList.add(sub);
		sub.choiceSet_parent = null;

		guiComponent.seperateSubChoiceSetComponenet(sub);
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
		if (FlagUtil.getFlag(flag, flagPosition_selectable)) {
			guiComponent.pane.setBorder(null);
		} else {
			guiComponent.pane.setBorder(ChoiceSetGuiComponent.border_default);
		}
		if(FlagUtil.getFlag(flag, flagPosition_horizontal)) {
			minWidth = 250;
			minHeight = 200;
			guiComponent.setHorizontal(true);
		}else {
			minWidth = 200;
			minHeight = 250;
			guiComponent.setHorizontal(false);
		}
	}
	
	// 화면상의 위치
	public void updateCoordinate(double moveX, double moveY) {
		guiComponent.updatePos(pos_x + moveX, pos_y + moveY);
		updateBounds();
	}

	// 실제 위치
	public void updatePosition(double moveX, double moveY) {
		pos_x += moveX;
		pos_y += moveY;
		guiComponent.updatePos(pos_x - CreateGuiController.platform.local_x,
				pos_y - CreateGuiController.platform.local_y);
		updateBounds();
	}

	public void setCoordinate(double coord_X, double coord_Y) {
		guiComponent.updatePos(coord_X, coord_Y);
		updateBounds();
	}

	public void setPosition(float posX, float posY) {
		pos_x = posX;
		pos_y = posY;
		guiComponent.updatePos(pos_x - CreateGuiController.platform.local_x,
				pos_y - CreateGuiController.platform.local_y);
		updateBounds();
	}

	@JsonIgnore
	public GridPane getAnchorPane() {
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
				var splitedStr = v.split("\\}:\\{");
				splitedStr[0] = splitedStr[0].substring(1);
				splitedStr[1] = splitedStr[1].substring(0, splitedStr[1].length() - 1);
				var segment = new StyledSegment<>(splitedStr[0], splitedStr[1]);
				segmentList.add(segment);
			}
		}
	}

	public void changeSize(float w, float h) {
		this.width = w;
		this.height = h;
		getWidth();
		getHeight();
	}
}
