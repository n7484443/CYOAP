package cyoap_main.command;

import cyoap_main.design.choice.ChoiceSet;

public class MoveCommand extends AbstractCommand{
	public float start_x;
	public float start_y;
	public float end_x;
	public float end_y;
	public ChoiceSet choiceset;
	
	public MoveCommand() {}
	
	public MoveCommand(float start_x, float start_y, ChoiceSet choiceset) {
		this.start_x = start_x;
		this.start_y = start_y;
		this.choiceset = choiceset;
	}
	
	public void setEnd(float end_x, float end_y) {
		this.end_x = end_x;
		this.end_y = end_y;
	}

	@Override
	public void execute() {
		choiceset.setPosition(end_x, end_y);
	}

	@Override
	public void undo() {
		choiceset.setPosition(start_x, start_y);		
	}

	@Override
	public void check() {
		this.choiceset = getChoiceSetFromTitle(this.choiceset);
	}

	@Override
	public String getName() {
		return "Moved " + choiceset.string_title;
	}

}
