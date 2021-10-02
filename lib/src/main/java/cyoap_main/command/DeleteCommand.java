package cyoap_main.command;

import cyoap_main.design.choice.ChoiceSet;

public class DeleteCommand extends AbstractCommand {
	public double localx;
	public double localy;
	public ChoiceSet choiceSet;

	public DeleteCommand() {}

	public DeleteCommand(ChoiceSet choiceSet, double local_x, double local_y) {
		this.choiceSet = choiceSet;
		this.localx = local_x;
		this.localy = local_y;
	}

	@Override
	public void excute() {
		control.getChoicePane().getChildren().remove(choiceSet.getAnchorPane());
		control.getPlatform().choiceSetList.remove(choiceSet);
		control.nowMouseInDataSet = null;
	}

	@Override
	public void undo() {
		choiceSet.setUp(control.getChoicePane());
		control.getPlatform().choiceSetList.add(choiceSet);
		choiceSet.updateCoordinate(-localx, -localy);
		choiceSet.updateFlag();
	}

	@Override
	public void check() {
		this.choiceSet = getChoiceSetFromTitle(this.choiceSet);
	}
	
	@Override
	public String getName() {
		return "Deleted " + choiceSet.string_title;
	}

}
