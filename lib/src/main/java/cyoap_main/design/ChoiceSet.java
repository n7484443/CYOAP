package cyoap_main.design;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cyoap_main.design.controller.MakeGUIController;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ChoiceSet {
	public String string_title;
	public String string_describe;
	public String string_image_name;

	private BorderPane pane = new BorderPane();
	private BorderPane border_pane = new BorderPane();
	private BorderPane middle_pane = new BorderPane();
	private HBox hbox = new HBox();
	private ImageView image = new ImageView();
	private TextArea area = new TextArea();
	private Text title = new Text();
	public int flag = 0;

	@JsonIgnore
	public final int flag_selectable = 1;

	public List<ChoiceSet> choiceSet_child = new ArrayList<ChoiceSet>();
	public ChoiceSet choiceSet_parent = null;

	public double posx;
	public double posy;

	public boolean checkFlag(int flag, int check) {
		if ((flag & check) > 0)
			return true;
		return false;
	}
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
		pane.setLayoutX(posx);
		pane.setLayoutY(posy);
		pane.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2), BorderWidths.DEFAULT)));
		pane.setTop(border_pane);
		pane.setCenter(middle_pane);
		pane.setBottom(hbox);
		
		middle_pane.setPrefHeight(20);
		
		border_pane.setTop(title);
		border_pane.setCenter(image);
		border_pane.setBottom(area);
		border_pane.setBorder(new Border(
				new BorderStroke(null, null, Color.BLACK, null, null, null, BorderStrokeStyle.DASHED, null, new CornerRadii(2), new BorderWidths(2), null)));
		
		area.setEditable(false);

		border_pane.setPrefWidth(200);
		border_pane.setPrefHeight(100);
		area.setPrefHeight(100);
		image.setPreserveRatio(true);
		image.setFitWidth(200);
		
		border_pane.setMouseTransparent(true);

		pane.setOnMouseClicked(e -> {
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				if (e.getClickCount() == 2) {
					MakeGUIController.instance.nowEditDataSet = this;
					MakeGUIController.instance.loadFromDataSet(this);
					MakeGUIController.instance.changeTab(MakeGUIController.instance.tab_describe);
					e.consume();
				}
			}
		});
		pane.setOnMouseDragged(e -> {
			if (e.getButton().equals(MouseButton.MIDDLE)) {
				double movex = MakeGUIController.instance.sensitivity
						* (e.getSceneX() - MakeGUIController.instance.start_x);
				double movey = MakeGUIController.instance.sensitivity
						* (e.getSceneY() - MakeGUIController.instance.start_y);
				MakeGUIController.instance.start_x = e.getSceneX();
				MakeGUIController.instance.start_y = e.getSceneY();
				updateRealPos(movex, movey);

			}
		});
		pane.setOnMouseReleased(e -> {
			if (e.getButton().equals(MouseButton.MIDDLE)) {
				ChoiceSet final_choice = null;
				for (var choiceSet : MakeGUIController.instance.choiceSetList) {
					if (choiceSet == this)
						continue;
					if (check_intersect(this, choiceSet)) {
						final_choice = choiceSet;
						break;
					}
				}
				if (final_choice != null) {
					final_choice.addSubChoiceSet(this);
				}
			}
		});
		pane.setOnMouseEntered(e -> {
			MakeGUIController.instance.nowMouseInDataSet = this;
		});
		pane_mother.getChildren().add(pane);
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
		this.area.setText(string_describe);
		this.title.setText(string_title);
		if (this.string_image_name != null && !this.string_image_name.isEmpty())
			this.image.setImage(new Image(this.string_image_name));
	}

	public void addSubChoiceSet(ChoiceSet sub) {
		MakeGUIController.instance.choiceSetList.remove(sub);

		this.choiceSet_child.add(sub);
		sub.choiceSet_parent = this;

		if (sub.choiceSet_parent != null) {
			sub.choiceSet_parent.getAnchorPane().getChildren().remove(sub.getAnchorPane());
		}
		this.hbox.getChildren().add(sub.getAnchorPane());
		this.area.setPrefWidth(this.pane.getWidth());
	}

	public void updatePos(double moveX, double moveY) {
		pane.relocate(posx + moveX, posy + moveY);
	}

	public void updateRealPos(double moveX, double moveY) {
		posx += moveX;
		posy += moveY;
		pane.relocate(posx - MakeGUIController.instance.local_x, posy - MakeGUIController.instance.local_y);
	}

	@JsonIgnore
	public BorderPane getAnchorPane() {
		return pane;
	}
}
