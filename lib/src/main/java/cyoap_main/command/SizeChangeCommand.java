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
        this.before_width = choiceset.width;
        this.before_height = choiceset.height;
        this.before_pos_x = choiceset.pos_x;
        this.before_pos_y = choiceset.pos_y;
    }

    public void set(ChoiceSet choiceSet) {
        this.after_width = choiceSet.width;
        this.after_height = choiceSet.height;
        this.after_pos_x = choiceSet.pos_x;
        this.after_pos_y = choiceSet.pos_y;
    }

    @Override
    public void excute() {
        choiceset.setPosition(after_pos_x, after_pos_y);
        choiceset.changeSize(after_width, after_height);
        choiceset.update();
    }

    @Override
    public void undo() {
        choiceset.setPosition(before_pos_x, before_pos_y);
        choiceset.changeSize(before_width, before_height);
        choiceset.update();
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
