package cyoap_main.unit.command;

import cyoap_main.design.controller.MakeGUIController;

public abstract class AbstractCommand {
	public abstract void excute();
	public abstract void undo();
	public MakeGUIController control = MakeGUIController.instance;
}
