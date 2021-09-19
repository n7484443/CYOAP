package cyoap_main.design;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
	public String string_describe;
	public String string_image_name;

	@JsonIgnore
	public static final Color baseColor = Color.web("#96d9ff");

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
		this("title", "describe", null, 0, 0, 0, 0);
	}

	public ChoiceSet(String title, String describe, Image image) {
		this(title, describe, image != null ? image.getUrl() : null, 0, 0, 0, 0);
	}

	public ChoiceSet(float posx, float posy) {
		this("title", "describe", null, posx, posy, 0, 0);
	}

	public ChoiceSet(String title, String describe) {
		this(title, describe, null, 0, 0, 0, 0);
	}

	public ChoiceSet(String title, String describe, String image_name, float posx, float posy, float width,
			float height) {
		this.string_title = title;
		this.string_describe = describe;
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
}
