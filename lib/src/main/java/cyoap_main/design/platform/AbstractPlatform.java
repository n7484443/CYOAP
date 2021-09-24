package cyoap_main.design.platform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cyoap_main.design.ChoiceSet;
import cyoap_main.design.controller.PlatformGuiController;
import cyoap_main.unit.Vector2f;
import javafx.scene.image.Image;

@JsonAutoDetect(getterVisibility = Visibility.PUBLIC_ONLY)
public class AbstractPlatform {
	public List<ChoiceSet> choiceSetList = new ArrayList<ChoiceSet>();
	public double local_x = 0;// local position of screen
	public double local_y = 0;
	public int min_x = -800;
	public int min_y = -1600;
	public int max_x = 800;
	public int max_y = 1600;
	public float scale = 1.0f;

	@JsonIgnore
	public double move_x = 0;
	@JsonIgnore
	public double move_y = 0;
	@JsonIgnore
	public double start_x = 0;
	@JsonIgnore
	public double start_y = 0;

	public Image image = null;
	public File image_file = null;

	@JsonIgnore
	public PlatformGuiController guiController;
	public int flag = 0;

	@JsonIgnore
	public final int flag_maximize = 1 << 0;
	@JsonIgnore
	public final int flag_center = 1 << 1;

	public Vector2f checkLine(ChoiceSet choiceSet, float bias) {
		var x_min = choiceSet.posx;
		var y_min = choiceSet.posy;
		var x_max = x_min + choiceSet.getWidth();
		var y_max = y_min + choiceSet.getHeight();

		float x_new = Float.MAX_VALUE;
		float y_new = Float.MAX_VALUE;

		for (var choice : choiceSetList) {
			if (choice == choiceSet)
				continue;
			var x_min2 = choice.posx;
			var y_min2 = choice.posy;
			var x_max2 = x_min2 + choice.getWidth();
			var y_max2 = y_min2 + choice.getHeight();
			var x_half2 = x_min2 + choice.getWidth() / 2;
			var y_half2 = y_min2 + choice.getHeight() / 2;

			if (Math.abs(x_min - x_min2) < bias)
				x_new = x_min2;
			if (Math.abs(x_min - x_max2) < bias)
				x_new = x_max2;
			if (Math.abs(x_max - x_min2) < bias)
				x_new = x_min2 - choiceSet.getWidth();
			if (Math.abs(x_max - x_max2) < bias)
				x_new = x_max2 - choiceSet.getWidth();

			if (Math.abs(x_min - x_half2) < bias)
				x_new = x_half2;
			if (Math.abs(x_max - x_half2) < bias)
				x_new = x_half2 - choiceSet.getWidth();

			if (Math.abs(y_min - y_min2) < bias)
				y_new = y_min2;
			if (Math.abs(y_min - y_max2) < bias)
				y_new = y_max2;
			if (Math.abs(y_max - y_min2) < bias)
				y_new = y_min2 - choiceSet.getHeight();
			if (Math.abs(y_max - y_max2) < bias)
				y_new = y_max2 - choiceSet.getHeight();

			if (Math.abs(y_min - y_half2) < bias)
				y_new = y_half2;
			if (Math.abs(y_max - y_half2) < bias)
				y_new = y_half2 - choiceSet.getHeight();
		}
		if (x_new == Float.MAX_VALUE && y_new == Float.MAX_VALUE) {
			return null;
		} else if (x_new == Float.MAX_VALUE && y_new != Float.MAX_VALUE) {
			return new Vector2f(0, y_new);
		} else if (x_new != Float.MAX_VALUE && y_new == Float.MAX_VALUE) {
			return new Vector2f(x_new, 0);
		} else {
			return new Vector2f(x_new, y_new);
		}
	}

	public AbstractPlatform() {
	}

	public AbstractPlatform(PlatformGuiController guiController) {
		this.guiController = guiController;
	}

	public void clearNodeOnPanePosition() {
		choiceSetList.clear();
		guiController.getChoicePane().getChildren().clear();
		guiController.getChoicePane().getChildren().add(guiController.getBackgroundImageView());
	}
	
	public void setNodeDepth() {
		for(var c : choiceSetList) {
			var gui = c.guiComponent.pane;
			gui.setViewOrder(0.0d);
		}
		guiController.getBackgroundImageView().setViewOrder(10.0d);
	}

	public void update() {
		if (image_file != null && image == null) {
			image = new Image(image_file.toURI().toString());
			guiController.getBackgroundImageView().setImage(image);

			var f_image = image.getWidth() / image.getHeight();
			var f_frame = (max_y - min_y) / (max_x - min_x);

			guiController.getBackgroundImageView().setSmooth(true);
			if (f_image < f_frame) {
				guiController.getBackgroundImageView().setFitWidth(max_x - min_x);
				guiController.getBackgroundImageView().setFitHeight(max_y - min_y);
			} else {
				guiController.getBackgroundImageView().setFitWidth(max_x - min_x);
				guiController.getBackgroundImageView().setFitHeight(max_y - min_y);
			}
		}
		guiController.getChoicePane().setScaleX(scale);
		guiController.getChoicePane().setScaleY(scale);
		updateMouseCoordinate();
		setNodeDepth();
	}

	public void updateMouseCoordinate() {
		choiceSetList.forEach(d -> d.updateCoordinate(-local_x, -local_y));
		guiController.getBackgroundImageView().relocate(min_x - local_x, min_y - local_y);
	}

	public void updateCoordinateAll(double x, double y) {
		for (var node : choiceSetList) {
			node.updateCoordinate(x, y);
		}
		guiController.getBackgroundImageView().relocate(min_x - local_x + x, min_y - local_y + y);
	}

	public void updatePositionAll(double x, double y) {
		for (var node : choiceSetList) {
			node.updatePosition(x, y);
		}
		guiController.getBackgroundImageView().relocate(min_x - local_x + x, min_y - local_y + y);
	}
}
