package cyoap_main.design;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import cyoap_main.design.controller.MakeGUIController;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ChoiceSet {
	public String string_title;
	public String string_describe;
	public String string_image_name;

	public final int color = 0x0067A3;// blue
	@JsonIgnore
	public ChoiceSetGuiComponent guiComponent = new ChoiceSetGuiComponent(color);

	public int flag = 0;

	@JsonIgnore
	public final int flagPosition_selectable = 0;

	@JsonManagedReference
	public List<ChoiceSet> choiceSet_child = new ArrayList<ChoiceSet>();
	
	@JsonBackReference
	public ChoiceSet choiceSet_parent = null;

	public double posx;
	public double posy;

	public ChoiceSet() {
		this("title", "", null, 0, 0);
	}

	public ChoiceSet(String title, String describe, Image image) {
		this(title, describe, image != null ? image.getUrl() : null, 0, 0);
	}

	public ChoiceSet(double posx, double posy) {
		this(null, null, null, posx, posy);
	}

	public ChoiceSet(String title, String describe) {
		this(title, describe, null, 0, 0);
	}

	public ChoiceSet(String title, String describe, String image_name, double posx, double posy) {
		this.string_title = title;
		this.string_describe = describe;
		this.string_image_name = image_name;
		this.posx = posx;
		this.posy = posy;
	}

	public void setUp(Pane pane_mother) {
		guiComponent.setUp(this);
		pane_mother.getChildren().add(guiComponent.pane);
	}

	public boolean check_intersect(ChoiceSet a, ChoiceSet b) {
		var a_xmin = a.posx;
		var a_ymin = a.posy;
		var a_width = a.getAnchorPane().getLayoutBounds().getWidth();
		var a_height = a.getAnchorPane().getLayoutBounds().getHeight();

		var b_xmin = b.posx;
		var b_ymin = b.posy;
		var b_width = b.getAnchorPane().getLayoutBounds().getWidth();
		var b_height = b.getAnchorPane().getLayoutBounds().getHeight();

		if (a_xmin + a_width < b_xmin)
			return false;
		if (a_xmin > b_xmin + b_width)
			return false;
		if (a_ymin + a_height < b_ymin)
			return false;
		if (a_ymin > b_ymin + b_height)
			return false;
		return true;
	}

	public boolean check_intersect(ChoiceSet a, double x, double y) {
		var a_xmin = a.posx;
		var a_ymin = a.posy;
		var a_width = a.getAnchorPane().getLayoutBounds().getWidth();
		var a_height = a.getAnchorPane().getLayoutBounds().getHeight();

		if (a_xmin + a_width < x)
			return false;
		if (a_xmin > x)
			return false;
		if (a_ymin + a_height < y)
			return false;
		if (a_ymin > y)
			return false;
		return true;
	}

	public void update() {
		guiComponent.update();
	}

	public void combineSubChoiceSet(ChoiceSet sub) {
		MakeGUIController.platform.choiceSetList.remove(sub);

		this.choiceSet_child.add(sub);
		sub.choiceSet_parent = this;

		guiComponent.combineSubChoiceSetComponenet(sub);
	}

	public void seperateSubChoiceSet(ChoiceSet sub) {
		this.choiceSet_child.remove(sub);
		MakeGUIController.platform.choiceSetList.add(sub);
		sub.choiceSet_parent = null;

		guiComponent.seperateSubChoiceSetComponenet(sub);
	}

	// 화면상의 위치
	public void updateCoordinate(double moveX, double moveY) {
		guiComponent.updatePos(posx + moveX, posy + moveY);
	}

	// 실제 위치
	public void updatePosition(double moveX, double moveY) {
		posx += moveX;
		posy += moveY;
		guiComponent.updatePos(posx - MakeGUIController.platform.local_x,
				posy - MakeGUIController.platform.local_y);
	}

	public void setCoordinate(double coordX, double coordY) {
		guiComponent.updatePos(coordX, coordY);
	}

	public void setPosition(double posX, double posY) {
		posx = posX;
		posy = posY;
		guiComponent.updatePos(posx - MakeGUIController.platform.local_x,
				posy - MakeGUIController.platform.local_y);
	}

	@JsonIgnore
	public BorderPane getAnchorPane() {
		return guiComponent.pane;
	}
}
