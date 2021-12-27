package cyoap_main.platform;

import cyoap_main.controller.IGuiController;

public class CreatePlatform extends AbstractPlatform {
    public CreatePlatform() {
    }

    public CreatePlatform(IGuiController guiController) {
        super(guiController);
    }

    @Override
    public boolean isEditable() {
        return true;
    }
}
