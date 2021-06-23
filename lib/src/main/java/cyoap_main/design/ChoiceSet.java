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
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
public class ChoiceSet {
	public String string_title;
	public String string_describe;
	public String string_image_name;
	
	private VBox vbox = new VBox();
	private ImageView image = new ImageView();
	private TextArea area = new TextArea();
	private Text title = new Text();
	
	public List<ChoiceSet> subChoiceSet = new ArrayList<ChoiceSet>();

	public double posx;
	public double posy;

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

	public void setUp(Pane pane) {
		vbox.getChildren().addAll(title, image, area);
		vbox.setLayoutX(posx);
		vbox.setLayoutY(posy);
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

		vbox.setOnMouseClicked(e -> {
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				if (e.getClickCount() == 2) {
					MakeGUIController.instance.nowEditDataSet = this;
					MakeGUIController.instance.loadFromDataSet(this);
					MakeGUIController.instance.changeTab(MakeGUIController.instance.tab_describe);
				}
			}
		});
		vbox.setOnMouseDragged(e -> {
			if (e.getButton().equals(MouseButton.MIDDLE)) {
				double movex = MakeGUIController.instance.sensitivity * (e.getSceneX() - MakeGUIController.instance.start_x);
				double movey = MakeGUIController.instance.sensitivity * (e.getSceneY() - MakeGUIController.instance.start_y);
				MakeGUIController.instance.start_x = e.getSceneX();
				MakeGUIController.instance.start_y = e.getSceneY();
				updateRealPos(movex, movey);
				
			}
		});
		vbox.setOnMouseEntered(e -> {
			MakeGUIController.instance.nowMouseInDataSet = this;
		});
		pane.getChildren().add(vbox);
	}
	
	public void update() {
		this.area.setText(string_describe);
		this.title.setText(string_title);
		if (this.string_image_name != null && !this.string_image_name.isEmpty())
			this.image.setImage(new Image(this.string_image_name));
		
	}

	public void updatePos(double moveX, double moveY) {
		vbox.relocate(posx + moveX, posy + moveY);
	}
	

	public void updateRealPos(double moveX, double moveY) {
		posx += moveX;
		posy += moveY;
		vbox.relocate(posx - MakeGUIController.instance.local_x, posy - MakeGUIController.instance.local_y);
	}
	
	@JsonIgnore
	public VBox getVbox() {
		return vbox;
	}
}
