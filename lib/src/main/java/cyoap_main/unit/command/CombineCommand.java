package cyoap_main.unit.command;

import cyoap_main.design.ChoiceSet;
import cyoap_main.design.controller.MakeGUIController;

public class CombineCommand extends AbstractCommand {
	public ChoiceSet choiceSet_parent;
	public ChoiceSet choiceSet_children;

	public CombineCommand(ChoiceSet choiceSet_parent, ChoiceSet choiceSet_children) {
		this.choiceSet_parent = choiceSet_parent;
		this.choiceSet_children = choiceSet_children;
	}

	@Override
	public void excute() {
		choiceSet_parent.combineSubChoiceSet(choiceSet_children);
	}

	@Override
	public void undo() {
		choiceSet_parent.seperateSubChoiceSet(choiceSet_children);
		MakeGUIController.platform.updateMouseCoordinate();
	}

	@Override
	public String getName() {
		return "Combine Node";
	}

}
