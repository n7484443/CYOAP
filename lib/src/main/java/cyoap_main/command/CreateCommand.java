package cyoap_main.command;

import java.util.Random;

import cyoap_main.design.choice.ChoiceSet;

public class CreateCommand extends AbstractCommand{
	public ChoiceSet choiceSet;
	
	public CreateCommand() {}
	
	public CreateCommand(float x, float y) {
		
		int leftLimit = 48; // numeral '0'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 10;
	    Random random = new Random();

	    String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();

		choiceSet = new ChoiceSet(generatedString, x, y, ChoiceSet.default_size.x(), ChoiceSet.default_size.y());
		var v = checkOutline(choiceSet, x, y);
		choiceSet.setPosition(v.x(), v.y());
	}

	@Override
	public void execute() {
		choiceSet.setUp(control.getChoicePane());
		control.getPlatform().choiceSetList.add(choiceSet);
		choiceSet.setPosition(choiceSet.bound.x, choiceSet.bound.y);
		control.getPlatform().isMouseMoved = true;
	}

	@Override
	public void undo() {
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
