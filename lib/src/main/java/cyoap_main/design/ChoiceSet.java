package cyoap_main.design;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cyoap_main.design.controller.MakeGUIController;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
public class ChoiceSet {
	public String string_title;
	public String string_describe;
	public String string_image_name;
	
	private AnchorPane pane = new AnchorPane();
	private VBox vbox = new VBox();
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
		if((flag & check) > 0)return true;
		return false;
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
		pane.getChildren().add(vbox);
		pane.setLayoutX(posx);
		pane.setLayoutY(posy);
		
		vbox.getChildren().addAll(title, image, area, hbox);
		vbox.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		vbox.setAlignment(Pos.CENTER);
		area.setEditable(false);

		area.setMaxWidth(200);
		area.setMaxHeight(100);
		image.setPreserveRatio(true);
		image.setFitWidth(200);

		title.setMouseTransparent(true);
		area.setMouseTransparent(true);
		image.setMouseTransparent(true);
		vbox.setMouseTransparent(true);

		pane.setOnMouseClicked(e -> {
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				if (e.getClickCount() == 2) {
					MakeGUIController.instance.nowEditDataSet = this;
					MakeGUIController.instance.loadFromDataSet(this);
					MakeGUIController.instance.changeTab(MakeGUIController.instance.tab_describe);
				}
			}
		});
		pane.setOnMouseDragged(e -> {
			if (e.getButton().equals(MouseButton.MIDDLE)) {
				double movex = MakeGUIController.instance.sensitivity * (e.getSceneX() - MakeGUIController.instance.start_x);
				double movey = MakeGUIController.instance.sensitivity * (e.getSceneY() - MakeGUIController.instance.start_y);
				MakeGUIController.instance.start_x = e.getSceneX();
				MakeGUIController.instance.start_y = e.getSceneY();
				updateRealPos(movex, movey);
				
			}
		});
		pane.setOnMouseReleased(e -> {
			if(e.getButton().equals(MouseButton.MIDDLE)) {
				ChoiceSet final_choice = null;
				System.out.println(1);
				for(var choiceSet : MakeGUIController.instance.choiceSetList) {
					if(choiceSet == this)continue;
					var bound = choiceSet.getAnchorPane().getLayoutBounds();					
					if(bound.intersects(this.getAnchorPane().getLayoutBounds())) {
						final_choice = choiceSet;
						break;
					}
				}
				if(final_choice != null) {
					final_choice.addSubChoiceSet(this);
				}
			}
		});
		pane.setOnMouseEntered(e -> {
			MakeGUIController.instance.nowMouseInDataSet = this;
		});
		pane_mother.getChildren().add(pane);
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
		if(sub.choiceSet_parent != null) {
			sub.choiceSet_parent.getAnchorPane().getChildren().remove(sub.getAnchorPane());
		}
		this.hbox.getChildren().add(sub.getAnchorPane());
		sub.choiceSet_parent = this;
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
	public AnchorPane getAnchorPane() {
		return pane;
	}
}
