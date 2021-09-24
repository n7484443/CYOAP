package cyoap_main.design;

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
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ChoiceSet {
	public String string_title;
	
	public List<StyledSegment<String, String>> segmentList = new ArrayList<StyledSegment<String, String>>();
	
	public String string_image_name;

	@JsonIgnore
	public static final Color baseColor = Color.web("#E1E3ED");

	public Color color = baseColor;// blue

	@JsonIgnore
	public ChoiceSetGuiComponent guiComponent = new ChoiceSetGuiComponent(color);

	@JsonIgnore
	public static final int flagPosition_selectable = 0;

	public int flag = flagPosition_selectable;

	@JsonManagedReference
	public List<ChoiceSet> choiceSet_child = new ArrayList<ChoiceSet>();

	@JsonBackReference
	public ChoiceSet choiceSet_parent = null;

	public float posx;
	public float posy;
	public float width;
	public float height;

	@JsonIgnore
	public Bound2f bound;

	public ChoiceSet() {
		this("title", null, 0, 0, 0, 0);
	}

	public ChoiceSet(String title, Image image) {
		this(title, image != null ? image.getUrl() : null, 0, 0, 0, 0);
	}

	public ChoiceSet(float posx, float posy) {
		this("title", null, posx, posy, 0, 0);
	}

	public ChoiceSet(String title) {
		this(title, null, 0, 0, 0, 0);
	}

	public ChoiceSet(String title, String image_name, float posx, float posy, float width,
			float height) {
		this.string_title = title;
		this.string_image_name = image_name;
		this.posx = posx;
		this.posy = posy;
		this.width = width;
		this.height = height;

		bound = new Bound2f(posx, posy, getWidth(), getHeight());
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

	@JsonIgnore
	public float getWidth() {
		return width == 0 ? (float) getAnchorPane().getLayoutBounds().getWidth() : width;
	}

	@JsonIgnore
	public float getHeight() {
		return height == 0 ? (float) getAnchorPane().getLayoutBounds().getHeight() : height;
	}

	public void update() {
		guiComponent.update();
		updateBounds();
		updateColor();
	}

	public void updateBounds() {
		bound.x = posx;
		bound.y = posy;
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
	}

	// 화면상의 위치
	public void updateCoordinate(double moveX, double moveY) {
		guiComponent.updatePos(posx + moveX, posy + moveY);
		updateBounds();
	}

	// 실제 위치
	public void updatePosition(double moveX, double moveY) {
		posx += moveX;
		posy += moveY;
		guiComponent.updatePos(posx - CreateGuiController.platform.local_x,
				posy - CreateGuiController.platform.local_y);
		updateBounds();
	}

	public void setCoordinate(double coordX, double coordY) {
		guiComponent.updatePos(coordX, coordY);
		updateBounds();
	}

	public void setPosition(float posX, float posY) {
		posx = posX;
		posy = posY;
		guiComponent.updatePos(posx - CreateGuiController.platform.local_x,
				posy - CreateGuiController.platform.local_y);
		updateBounds();
	}

	@JsonIgnore
	public BorderPane getAnchorPane() {
		return guiComponent.pane;
	}

	public String getColor() {
		return color.toString();
	}

	public void setColor(String s) {
		updateColor(Color.web(s));
	}
	
	@JsonGetter("segmentList")
	@JsonProperty("segmentList")
	public List<String> getSegmentList() {
		List<String> strList = new ArrayList<String>();
		if(segmentList.isEmpty())return strList;
		for(var v : segmentList) {
			if(v == null) {
				strList.add("");
			}else {
				strList.add("{" + v.getSegment() + "}:{" + v.getStyle() + "}");
			}
		}
		return strList;
	}

	@JsonSetter("segmentList")
	public void setSegmentList(List<String> s) {
		for(var v : s) {
			if(v.isBlank()) {
				segmentList.add(null);
			}else {
				var splitedStr = v.split("\\}:\\{");
				splitedStr[0] = splitedStr[0].substring(1);
				splitedStr[1] = splitedStr[1].substring(0, splitedStr[1].length() - 1);
				var segment = new StyledSegment<String, String>(splitedStr[0], splitedStr[1]);
				segmentList.add(segment);
			}
		}
	}
}
