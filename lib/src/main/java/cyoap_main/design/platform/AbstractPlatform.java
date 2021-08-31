package cyoap_main.design.platform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cyoap_main.design.ChoiceSet;
import cyoap_main.design.controller.PlatformGuiController;
import javafx.scene.image.Image;

public class AbstractPlatform {
	public List<ChoiceSet> choiceSetList = new ArrayList<ChoiceSet>();
	public double local_x = 0;
	public double local_y = 0;
	public double move_x = 0;
	public double move_y = 0;
	public double start_x = 0;
	public double start_y = 0;
	public int min_x = -500;
	public int min_y = -500;
	public int max_x = 500;
	public int max_y = 500;
	public float scale = 1.0f;
	
	public Image image = null;
	public File image_file = null; 
	public PlatformGuiController guiController;
	public int flag = 0;
	@JsonIgnore
	public final int flag_maximize = 1<<0;
	public final int flag_center = 1<<1;

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
			
			var f_image = image.getWidth()/image.getHeight();
			var f_frame = guiController.getPane().getMaxWidth()/guiController.getPane().getMaxWidth();
			
			if(f_image < f_frame) {
				guiController.getBackgroundImageView().setFitWidth(guiController.getPane().getMaxWidth());
			}else {
				guiController.getBackgroundImageView().setFitHeight(guiController.getPane().getMaxHeight());
			}
		}
	}
}