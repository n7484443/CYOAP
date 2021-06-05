package cyoap_main.design;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cyoap_main.design.controller.MakeGUIController;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DataSet {
	public String string_title;
	public String string_describe;
	public String string_image_name;
	@JsonIgnore
	public VBox vbox = new VBox();
	@JsonIgnore
	public ImageView image = new ImageView();
	@JsonIgnore
	public TextArea area = new TextArea();
	@JsonIgnore
	public Text title = new Text();

	public double posx;
	public double posy;

	public DataSet(String title, String describe, Image image) {
		this(title, describe, image != null ? image.getUrl() : null, 0, 0);
	}

	public DataSet(double posx, double posy) {
		this(null, null, null, posx, posy);
	}

	public DataSet(String title, String describe) {
		this(title, describe, null, 0, 0);
	}

	public DataSet(String title, String describe, String image_name, double posx, double posy) {
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

		EventHandler<? super MouseEvent> t = e -> {
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				if (e.getClickCount() == 2) {
					MakeGUIController.instance.nowEditDataSet = this;
					MakeGUIController.instance.changeTab(MakeGUIController.instance.tab_describe);
				}
			}
		};

		vbox.setOnMouseClicked(t);
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
		System.out.println(moveX + ":" + moveY);
	}
}
