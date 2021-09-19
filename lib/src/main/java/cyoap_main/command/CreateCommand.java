package cyoap_main.command;

import cyoap_main.design.ChoiceSet;

public class CreateCommand extends AbstractCommand{
	public ChoiceSet choiceSet;
	
	public CreateCommand(float x, float y) {
		choiceSet = new ChoiceSet(x, y);
		var v = checkOutline(choiceSet, x, y);
		choiceSet.posx = v.x;
		choiceSet.posy = v.y;
	}
	@Override
	public void excute() {
		choiceSet.setUp(control.getChoicePane());
		control.getPlatform().choiceSetList.add(choiceSet);
		choiceSet.setPosition(choiceSet.posx, choiceSet.posy);
	}

	@Override
	public void undo() {
		control.getChoicePane().getChildren().remove(choiceSet.getAnchorPane());
		control.getPlatform().choiceSetList.remove(choiceSet);
		control.nowMouseInDataSet = null;
	}
	@Override
	public String getName() {
		return "Create Node";
	}
}
