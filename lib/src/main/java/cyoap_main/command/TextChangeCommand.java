package cyoap_main.command;

import cyoap_main.design.ChoiceSet;

public class TextChangeCommand extends AbstractCommand{
	public ChoiceSet choiceSet;
	public String string_title_after;
	public String string_describe_after;
	public String string_image_name_after;

	public String string_title_before;
	public String string_describe_before;
	public String string_image_name_before;
	
	public TextChangeCommand(ChoiceSet choiceSet, String string_title, String string_describe, String string_image_name) {
		this.choiceSet = choiceSet;
		this.string_title_before = string_title;
		this.string_describe_before = string_describe;
		this.string_image_name_before = string_image_name;
	}
	public void setText(String string_title, String string_describe, String string_image_name) {
		this.string_title_after = string_title;
		this.string_describe_after = string_describe;
		this.string_image_name_after = string_image_name;
	}
	
	@Override
	public void excute() {
		choiceSet.string_title = this.string_title_after;
		choiceSet.string_describe = this.string_describe_after;
		choiceSet.string_image_name = this.string_image_name_after;
	}

	@Override
	public void undo() {
		choiceSet.string_title = this.string_title_before;
		choiceSet.string_describe = this.string_describe_before;
		choiceSet.string_image_name = this.string_image_name_before;
	}

	@Override
	public String getName() {
		return "Change Text";
	}
}
