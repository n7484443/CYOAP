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
	public int min_x = -500;
	public int min_y = -500;
	public int max_x = 500;
	public int max_y = 500;
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
		List<Float> pointList_x_before = new ArrayList<Float>();
		List<Float> pointList_y_before = new ArrayList<Float>();

		for (var choice : choiceSetList) {
			pointList_x_before.add(choice.posx);
			pointList_x_before.add(choice.posx + choiceSet.getWidth());
			pointList_y_before.add(choice.posx);
			pointList_y_before.add(choice.posx + choiceSet.getHeight());
		}
		List<Float> pointList_x = new ArrayList<Float>();
		List<Float> pointList_y = new ArrayList<Float>();

		roop: for (int i = 0; i < pointList_x_before.size(); i++) {
			for (int j = 0; j < pointList_x.size(); j++) {
				if (Math.abs(pointList_x_before.get(i) - pointList_x.get(j)) < 1E-6) {
					continue roop;
				}
			}
			pointList_x.add(pointList_x_before.get(i));
		}

		roop: for (int i = 0; i < pointList_y_before.size(); i++) {
			for (int j = 0; j < pointList_y.size(); j++) {
				if (Math.abs(pointList_y_before.get(i) - pointList_y.get(j)) < 1E-6) {
					continue roop;
				}
			}
			pointList_y.add(pointList_y_before.get(i));
		}

		var x_min = choiceSet.posx;
		var y_min = choiceSet.posy;
		var x_max = x_min + choiceSet.getWidth();
		var y_max = y_min + choiceSet.getHeight();
		for (var x : pointList_x) {
			if (Math.abs(x - x_min) < bias)
				return new Vector2f(x, 0);
			if (Math.abs(x - x_max) < bias)
				return new Vector2f(x, 0);
		}
		for (var y : pointList_y) {
			if (Math.abs(y - y_min) < bias)
				return new Vector2f(0, y);
			if (Math.abs(y - y_max) < bias)
				return new Vector2f(0, y);
		}
		return null;
	}

	public Vector2f checkLine2(ChoiceSet choiceSet, float bias) {
		var x_min = choiceSet.posx;
		var y_min = choiceSet.posy;
		var x_max = x_min + choiceSet.getWidth();
		var y_max = y_min + choiceSet.getHeight();
		
		float x_new = Float.MAX_VALUE;
		float y_new = Float.MAX_VALUE;
		
		for (var choice : choiceSetList) {
			if(choice == choiceSet)continue;
			var x_min2 = choice.posx;
			var y_min2 = choice.posy;
			var x_max2 = x_min2 + choice.getWidth();
			var y_max2 = y_min2 + choice.getHeight();

			if (Math.abs(x_min - x_min2) < bias)
				x_new = x_min2;
			if (Math.abs(x_min - x_max2) < bias)
				x_new = x_max2;
			if (Math.abs(x_max - x_min2) < bias)
				x_new = x_min2 - choiceSet.getWidth();
			if (Math.abs(x_max - x_max2) < bias)
				x_new = x_max2 - choiceSet.getWidth();

			if (Math.abs(y_min - y_min2) < bias)
				y_new = y_min2;
			if (Math.abs(y_min - y_max2) < bias)
				y_new = y_max2;
			if (Math.abs(y_max - y_min2) < bias)
				y_new = y_min2 - choiceSet.getHeight();
			if (Math.abs(y_max - y_max2) < bias)
				y_new = y_max2 - choiceSet.getHeight();
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
		guiController.getPane().getChildren().clear();
	}

	public void update() {
		if (image_file != null && image == null) {
			image = new Image(image_file.toURI().toString());
			guiController.getBackgroundImageView().setImage(image);

			var f_image = image.getWidth() / image.getHeight();
			var f_frame = guiController.getPane().getMaxWidth() / guiController.getPane().getMaxWidth();

			if (f_image < f_frame) {
				guiController.getBackgroundImageView().setFitWidth(guiController.getPane().getMaxWidth());
			} else {
				guiController.getBackgroundImageView().setFitHeight(guiController.getPane().getMaxHeight());
			}
		}
		updateMouseCoordinate();
	}

	public void updateMouseCoordinate() {
		choiceSetList.forEach(d -> d.updateCoordinate(-local_x, -local_y));
	}

	public void updateCoordinateAll(double x, double y) {
		for (var node : choiceSetList) {
			node.updateCoordinate(x, y);
		}
	}

	public void updatePositionAll(double x, double y) {
		for (var node : choiceSetList) {
			node.updatePosition(x, y);
		}
	}
}
