package cyoap_main.unit.command;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cyoap_main.design.controller.MakeGUIController;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, setterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class AbstractCommand {
	public abstract void excute();
	public abstract void undo();
	public abstract String getName();
	@JsonIgnore
	public MakeGUIController control = MakeGUIController.instance;
}
