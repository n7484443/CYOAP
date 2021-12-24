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
        this.before = new Bound2f(choiceset.bound);
    }

    public void set() {
        this.after = new Bound2f(choiceset.bound);
    }

    @Override
    public void execute() {
        choiceset.setPosition(after.x, after.y);
        choiceset.setSize(after.width, after.height);
    }

    @Override
    public void undo() {
        choiceset.setPosition(before.x, before.y);
        choiceset.setSize(before.width, before.height);
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
