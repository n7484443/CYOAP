package cyoap_main.unit.command;

import cyoap_main.design.ChoiceSet;
import javafx.scene.layout.Pane;

public class DeleteCommand extends AbstractCommand{
	public double localx;
	public double localy;
	public Pane pane;
	public ChoiceSet choiceSet;
	public DeleteCommand(ChoiceSet choiceSet, double local_x, double local_y, Pane pane) {
		this.choiceSet = choiceSet;
		this.localx = local_x;
		this.localy = local_y;
		this.pane = pane;
	}
	@Override
	public void excute() {
		pane.getChildren().remove(choiceSet.getAnchorPane());
		control.platform.choiceSetList.remove(choiceSet);
		control.nowMouseInDataSet = null;
	}

	@Override
	public void undo() {
		choiceSet.setUp(pane);
		control.platform.choiceSetList.add(choiceSet);
		choiceSet.updatePos(-localx, -localy);
	}

}
