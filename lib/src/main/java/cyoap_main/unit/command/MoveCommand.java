package cyoap_main.unit.command;

import cyoap_main.design.ChoiceSet;

public class MoveCommand extends AbstractCommand{
	public double start_x;
	public double start_y;
	public double end_x;
	public double end_y;
	public ChoiceSet choiceset;
	
	public MoveCommand(double start_x, double start_y, ChoiceSet choiceset) {
		this.start_x = start_x;
		this.start_y = start_y;
		this.choiceset = choiceset;
	}
	
	public void setEnd(double end_x, double end_y) {
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
