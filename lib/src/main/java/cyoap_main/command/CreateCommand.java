package cyoap_main.command;

import cyoap_main.design.choice.ChoiceSet;

public class CreateCommand extends AbstractCommand{
	public ChoiceSet choiceSet;
	
	public CreateCommand() {}
	
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
	public void check() {
		this.choiceSet = getChoiceSetFromTitle(this.choiceSet);
	}
	
	@Override
	public String getName() {
		return "Created " + choiceSet.string_title;
	}
}
