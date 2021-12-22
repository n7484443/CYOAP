package cyoap_main.command;

import cyoap_main.design.choice.ChoiceSet;
import cyoap_main.unit.Vector2f;

public class SizeChangeCommand extends AbstractCommand {
    public Vector2f before_size;
    public Vector2f before_pos;
    public ChoiceSet choiceset;
    public Vector2f after_size;
    public Vector2f after_pos;

    public SizeChangeCommand() {
    }

    public SizeChangeCommand(ChoiceSet choiceSet) {
        this.choiceset = choiceSet;
        this.before_size = new Vector2f(choiceset.width, choiceset.height);
        this.before_pos = new Vector2f(choiceset.pos_x, choiceset.pos_y);
    }

    public void set(ChoiceSet choiceSet) {
        this.after_size = new Vector2f(choiceSet.width, choiceSet.height);
        this.after_pos = new Vector2f(choiceSet.pos_x, choiceSet.pos_y);
    }

    @Override
    public void execute() {
        choiceset.setPosition(after_pos.x(), after_pos.y());
        choiceset.changeSize(after_size.x(), after_size.y());
        choiceset.update();
    }

    @Override
    public void undo() {
        choiceset.setPosition(before_pos.x(), before_pos.y());
        choiceset.changeSize(before_size.x(), before_size.y());
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
