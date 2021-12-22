package cyoap_main.command;

import cyoap_main.design.choice.ChoiceSet;
import cyoap_main.controller.createGui.CreateGuiController;

public class CombineCommand extends AbstractCommand {
	public ChoiceSet choiceSet_parent;
	public ChoiceSet choiceSet_children;

	public CombineCommand() {}

	public CombineCommand(ChoiceSet choiceSet_parent, ChoiceSet choiceSet_children) {
		this.choiceSet_parent = choiceSet_parent;
		this.choiceSet_children = choiceSet_children;
	}

	@Override
	public void execute() {
		choiceSet_parent.combineSubChoiceSet(choiceSet_children);
	}

	@Override
	public void undo() {
		choiceSet_parent.separateSubChoiceSet(choiceSet_children);
		CreateGuiController.platform.updateMouseCoordinate();
	}

	@Override
	public void check() {
		this.choiceSet_parent = getChoiceSetFromTitle(this.choiceSet_parent);
		this.choiceSet_children = getChoiceSetFromTitle(this.choiceSet_children);
	}

	@Override
	public String getName() {
		return choiceSet_parent.string_title + " combines " + choiceSet_children.string_title;
	}

}
