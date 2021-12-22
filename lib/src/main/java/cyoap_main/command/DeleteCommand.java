package cyoap_main.command;

import cyoap_main.design.choice.ChoiceSet;

public class DeleteCommand extends AbstractCommand {
	public double local_x;
	public double local_y;
	public ChoiceSet choiceSet;

	public DeleteCommand() {}

	public DeleteCommand(ChoiceSet choiceSet, double local_x, double local_y) {
		this.choiceSet = choiceSet;
		this.local_x = local_x;
		this.local_y = local_y;
	}

	@Override
	public void execute() {
		control.getChoicePane().getChildren().remove(choiceSet.getAnchorPane());
		control.getPlatform().choiceSetList.remove(choiceSet);
		control.nowMouseInDataSet = null;
	}

	@Override
	public void undo() {
		control.getChoicePane().getChildren().add(choiceSet.getAnchorPane());
		control.getPlatform().choiceSetList.add(choiceSet);
		choiceSet.updateCoordinate(-local_x, -local_y);
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
