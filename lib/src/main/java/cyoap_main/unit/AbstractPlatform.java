package cyoap_main.unit;

import java.util.ArrayList;
import java.util.List;

import cyoap_main.design.ChoiceSet;
import javafx.scene.layout.Pane;

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
	

	public void clearNodeOnPanePosition(Pane pane) {
		choiceSetList.clear();
		pane.getChildren().clear();
	}
}
