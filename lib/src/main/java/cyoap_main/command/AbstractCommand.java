package cyoap_main.command;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cyoap_main.design.ChoiceSet;
import cyoap_main.design.controller.MakeGUIController;
import cyoap_main.unit.Vector2f;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, setterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class AbstractCommand {
	public abstract void excute();

	public abstract void undo();

	public abstract String getName();

	@JsonIgnore
	public MakeGUIController control = MakeGUIController.instance;

	public Vector2f checkOutline(float x, float y) {
		if (x <= MakeGUIController.platform.min_x) {
			x = MakeGUIController.platform.min_x;
		} else if (x >= MakeGUIController.platform.max_x) {
			x = MakeGUIController.platform.max_x;
		}

		if (y <= MakeGUIController.platform.min_y) {
			y = MakeGUIController.platform.min_y;
		} else if (y >= MakeGUIController.platform.max_y) {
			y = MakeGUIController.platform.max_y;
		}
		return new Vector2f(x, y);
	}

	public Vector2f checkOutline(ChoiceSet choiceSet, float x, float y) {
		if (x <= MakeGUIController.platform.min_x) {
			x = MakeGUIController.platform.min_x;
		} else if (x + choiceSet.getWidth() >= MakeGUIController.platform.max_x) {
			x = MakeGUIController.platform.max_x - choiceSet.getWidth();
		}

		if (y <= MakeGUIController.platform.min_y) {
			y = MakeGUIController.platform.min_y;
		} else if (y + choiceSet.getHeight() >= MakeGUIController.platform.max_y) {
			y = MakeGUIController.platform.max_y - choiceSet.getHeight();
		}
		return new Vector2f(x, y);
	}
}
