package cyoap_main.unit.command;

import cyoap_main.design.ChoiceSet;

public class DeleteCommand extends AbstractCommand {
	public double localx;
	public double localy;
	public ChoiceSet choiceSet;

	public DeleteCommand(ChoiceSet choiceSet, double local_x, double local_y) {
		this.choiceSet = choiceSet;
		this.localx = local_x;
		this.localy = local_y;
	}

	@Override
	public void excute() {
		control.getPane().getChildren().remove(choiceSet.getAnchorPane());
		control.getPlatform().choiceSetList.remove(choiceSet);
		control.nowMouseInDataSet = null;
	}

	@Override
	public void undo() {
		choiceSet.setUp(control.getPane());
		control.getPlatform().choiceSetList.add(choiceSet);
		choiceSet.updateCoordinate(-localx, -localy);
	}

	@Override
	public String getName() {
		return "Delete Node";
	}

}
