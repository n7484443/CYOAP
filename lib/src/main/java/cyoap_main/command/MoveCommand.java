package cyoap_main.command;

import cyoap_main.design.ChoiceSet;

public class MoveCommand extends AbstractCommand{
	public float start_x;
	public float start_y;
	public float end_x;
	public float end_y;
	public ChoiceSet choiceset;
	
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
	public void excute() {
		choiceset.setPosition(end_x, end_y);
	}

	@Override
	public void undo() {
		choiceset.setPosition(start_x, start_y);		
	}

	@Override
	public String getName() {
		return "Move Node";
	}

}
