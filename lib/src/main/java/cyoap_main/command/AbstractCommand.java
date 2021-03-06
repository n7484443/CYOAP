package cyoap_main.command;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import cyoap_main.design.choice.ChoiceSet;
import cyoap_main.controller.createGui.CreateGuiController;
import cyoap_main.unit.Vector2f;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class AbstractCommand {
	public abstract void execute();

	public abstract void undo();

	public void check() {
	}

	@JsonIgnore
	public abstract String getName();

	public AbstractCommand() {
	}

	@JsonIgnore
	public CreateGuiController control = CreateGuiController.instance;

	public Vector2f checkOutline(float x, float y) {
		if (x <= CreateGuiController.platform.min_x) {
			x = CreateGuiController.platform.min_x;
		} else if (x >= CreateGuiController.platform.max_x) {
			x = CreateGuiController.platform.max_x;
		}

		if (y <= CreateGuiController.platform.min_y) {
			y = CreateGuiController.platform.min_y;
		} else if (y >= CreateGuiController.platform.max_y) {
			y = CreateGuiController.platform.max_y;
		}
		return new Vector2f(x, y);
	}

	public Vector2f checkOutline(ChoiceSet choiceSet, float x, float y) {
		if (x <= CreateGuiController.platform.min_x) {
			CreateGuiController.platform.min_x = x;
            CreateGuiController.platform.isSizeChanged = true;
		} else if (x + choiceSet.getWidth() >= CreateGuiController.platform.max_x) {
            CreateGuiController.platform.max_x = x + choiceSet.getWidth();
            CreateGuiController.platform.isSizeChanged = true;
		}

		if (y <= CreateGuiController.platform.min_y) {
            CreateGuiController.platform.min_y = y;
            CreateGuiController.platform.isSizeChanged = true;
		} else if (y + choiceSet.getHeight() >= CreateGuiController.platform.max_y) {
            CreateGuiController.platform.max_y = y + choiceSet.getHeight();
            CreateGuiController.platform.isSizeChanged = true;
		}
		return new Vector2f(x, y);
	}

	@JsonIgnore
	public ChoiceSet getChoiceSetFromTitle(ChoiceSet choice) {
		for (var v : control.getPlatform().choiceSetList) {
			if (v.string_title.equals(choice.string_title)) {
				return v;
			}
		}
		return choice;
	}
}
