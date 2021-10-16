package cyoap_main.command;

import cyoap_main.design.choice.ChoiceSet;

public class SizeChangeCommand extends AbstractCommand {
    public float before_width;
    public float before_height;
    public float before_pos_x;
    public float before_pos_y;
    public ChoiceSet choiceset;
    public float after_width;
    public float after_height;
    public float after_pos_x;
    public float after_pos_y;
    public SizeChangeCommand() {
    }

    public SizeChangeCommand(ChoiceSet choiceSet) {
        this.choiceset = choiceSet;
        this.before_width = this.choiceset.width;
        this.before_height = this.choiceset.height;
        this.before_pos_x = this.choiceset.pos_x;
        this.before_pos_y = this.choiceset.pos_y;
    }

    public void set(ChoiceSet choiceSet) {
        this.after_pos_x = choiceSet.pos_x;
        this.after_pos_y = choiceSet.pos_y;
        this.after_width = choiceSet.width;
        this.after_height = choiceSet.height;
    }

    @Override
    public void excute() {
        choiceset.pos_x = after_pos_x;
        choiceset.pos_y = after_pos_y;
        choiceset.width = after_width;
        choiceset.height = after_height;
    }

    @Override
    public void undo() {
        choiceset.pos_x = before_pos_x;
        choiceset.pos_y = before_pos_y;
        choiceset.width = before_width;
        choiceset.height = before_height;
    }

    @Override
    public void check() {
        this.choiceset = getChoiceSetFromTitle(this.choiceset);
    }

    @Override
    public String getName() {
        return "Size changed " + choiceset.string_title;
    }
}
