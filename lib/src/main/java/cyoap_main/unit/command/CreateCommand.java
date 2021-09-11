package cyoap_main.unit.command;

import cyoap_main.design.ChoiceSet;

public class CreateCommand extends AbstractCommand{
	public double localx;
	public double localy;
	public ChoiceSet choiceSet;
	
	public CreateCommand(float x, float y, double local_x, double local_y) {
		choiceSet = new ChoiceSet(x, y);
		this.localx = local_x;
		this.localy = local_y;
	}
	@Override
	public void excute() {
		choiceSet.setUp(control.getPane());
		control.getPlatform().choiceSetList.add(choiceSet);
		choiceSet.setPosition(choiceSet.posx, choiceSet.posy);
	}

	@Override
	public void undo() {
		control.getPane().getChildren().remove(choiceSet.getAnchorPane());
		control.getPlatform().choiceSetList.remove(choiceSet);
		control.nowMouseInDataSet = null;
	}
	@Override
	public String getName() {
		return "Create Node";
	}
}
