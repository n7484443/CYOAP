package cyoap_main.unit.command;

import cyoap_main.design.ChoiceSet;
import javafx.scene.layout.Pane;

public class CreateCommand extends AbstractCommand{
	public double localx;
	public double localy;
	public Pane pane;
	public ChoiceSet choiceSet;
	public CreateCommand(double x, double y, double local_x, double local_y, Pane pane) {
		choiceSet = new ChoiceSet(x, y);
		this.localx = local_x;
		this.localy = local_y;
		this.pane = pane;
	}
	@Override
	public void excute() {
		choiceSet.setUp(pane);
		control.platform.choiceSetList.add(choiceSet);
		choiceSet.updatePos(-localx, -localy);
	}

	@Override
	public void undo() {
		pane.getChildren().remove(choiceSet.getAnchorPane());
		control.platform.choiceSetList.remove(choiceSet);
		control.nowMouseInDataSet = null;
	}
}
