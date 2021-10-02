package cyoap_main.command;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cyoap_main.design.choice.ChoiceSet;

public class TextChangeCommand extends AbstractCommand{
	public ChoiceSet choiceSet;
	public String string_title_after;
	public List<String> string_describe_after;
	public String string_image_name_after;

	public String string_title_before;
	public List<String> string_describe_before;
	public String string_image_name_before;
	
	public TextChangeCommand() {}
	
	public TextChangeCommand(ChoiceSet choiceSet) {
		this.choiceSet = choiceSet;
		this.string_title_before = choiceSet.string_title;
		this.string_describe_before = choiceSet.getSegmentList();
		this.string_image_name_before = choiceSet.string_image_name;
	}

	@JsonIgnore
	public void setText(ChoiceSet choiceSet) {
		this.string_title_after = choiceSet.string_title;
		this.string_describe_after = choiceSet.getSegmentList();
		this.string_image_name_after = choiceSet.string_image_name;
	}
	
	@Override
	public void excute() {
		choiceSet.string_title = this.string_title_after;
		choiceSet.setSegmentList(this.string_describe_after);
		choiceSet.string_image_name = this.string_image_name_after;
		choiceSet.update();
	}

	@Override
	public void undo() {
		choiceSet.string_title = this.string_title_before;
		choiceSet.setSegmentList(this.string_describe_before);
		choiceSet.string_image_name = this.string_image_name_before;
		choiceSet.update();
	}

	@Override
	public void check() {
		this.choiceSet = getChoiceSetFromTitle(this.choiceSet);
	}

	@Override
	public String getName() {
		return "Change Text of " + choiceSet.string_title;
	}
}
