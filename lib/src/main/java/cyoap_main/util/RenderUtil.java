package cyoap_main.util;

import cyoap_main.design.controller.createGui.CreateGuiController;
import cyoap_main.design.platform.AbstractPlatform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RenderUtil {
    public static void setStroke(GraphicsContext gc, double time, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(1);
        gc.setLineDashes(5);
        gc.setLineDashOffset((time * 20) % 1000);
    }

    public static void renderStrokeVertical(GraphicsContext gc, AbstractPlatform platform, float x) {
        var lx = platform.min_x;
        var ly = platform.min_y;
        gc.strokeLine(x - lx, CreateGuiController.instance.getPlatform().min_y - ly, x - lx, CreateGuiController.instance.getPlatform().max_y - ly);
    }

    public static void renderStrokeHorizontal(GraphicsContext gc, AbstractPlatform platform, float y) {
        var lx = platform.min_x;
        var ly = platform.min_y;
        gc.strokeLine(CreateGuiController.instance.getPlatform().min_x - lx, y - ly, CreateGuiController.instance.getPlatform().max_x - lx, y - ly);
    }
}
