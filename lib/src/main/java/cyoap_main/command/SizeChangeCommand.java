package cyoap_main.command;

import cyoap_main.design.choice.ChoiceSet;
import cyoap_main.unit.Bound2f;

public class SizeChangeCommand extends AbstractCommand {
    public Bound2f before;
    public ChoiceSet choiceset;
    public Bound2f after;

    public SizeChangeCommand() {
    }

    public SizeChangeCommand(ChoiceSet choiceSet) {
        this.choiceset = choiceSet;
        this.before = new Bound2f(choiceset.bound.x, choiceset.bound.y, choiceset.width, choiceset.height);
    }

    public void set() {
        this.after = new Bound2f(choiceset.bound.x, choiceset.bound.y, choiceset.width, choiceset.height);
    }

    @Override
    public void execute() {
        choiceset.setPosition(after.x, after.y);
        choiceset.changeSize(after.width, after.height);
        choiceset.update();
    }

    @Override
    public void undo() {
        choiceset.setPosition(before.x, before.y);
        choiceset.changeSize(before.width, before.height);
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
