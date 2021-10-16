package cyoap_main.util;

import cyoap_main.command.SizeChangeCommand;
import cyoap_main.core.JavaFxMain;
import cyoap_main.design.choice.ChoiceSet;
import javafx.scene.Cursor;

import java.util.AbstractMap.SimpleEntry;

public class SizeUtil {
    public static boolean setCursor(double x, double y, double width_origin, double height_origin, float border) {
        var width = width_origin - x;
        var height = height_origin - y;
        boolean b = true;
        var b_square = border * border;
        var scene_create = JavaFxMain.instance.scene_create;
        if (x * x < b_square && y * y < b_square) {
            scene_create.setCursor(Cursor.NW_RESIZE);
        } else if (width * width < b_square && y * y < b_square) {
            scene_create.setCursor(Cursor.NE_RESIZE);
        } else if (x * x < b_square && height * height < b_square) {
            scene_create.setCursor(Cursor.SW_RESIZE);
        } else if (width * width < b_square && height * height < b_square) {
            scene_create.setCursor(Cursor.SE_RESIZE);
        } else if (x < border) {
            scene_create.setCursor(Cursor.W_RESIZE);
        } else if (width < border) {
            scene_create.setCursor(Cursor.E_RESIZE);
        } else if (y < border) {
            scene_create.setCursor(Cursor.N_RESIZE);
        } else if (height < border) {
            scene_create.setCursor(Cursor.S_RESIZE);
        } else {
            b = false;
            JavaFxMain.instance.scene_create.setCursor(Cursor.DEFAULT);
        }
        return b;
    }

    public static void changeSize(SimpleEntry<ChoiceSet, SizeChangeCommand> nowSizeChange, float move_x, float move_y) {
        var scene_create = JavaFxMain.instance.scene_create;
        var nowControl = nowSizeChange.getKey();
        var sizeChangeCommand = nowSizeChange.getValue();
        var cursor = scene_create.getCursor();
        if (cursor.equals(Cursor.NW_RESIZE)) {
            nowControl.changeSize(-move_x + sizeChangeCommand.before_width,
                    -move_y + sizeChangeCommand.before_height);
            nowControl.setPosition(move_x + sizeChangeCommand.before_pos_x,
                    move_y + sizeChangeCommand.before_pos_y);
        } else if (cursor.equals(Cursor.SW_RESIZE)) {
            nowControl.changeSize(-move_x + sizeChangeCommand.before_width,
                    move_y + sizeChangeCommand.before_height);
            nowControl.setPosition(move_x + sizeChangeCommand.before_pos_x,
                    sizeChangeCommand.before_pos_y);
        } else if (cursor.equals(Cursor.NE_RESIZE)) {
            nowControl.changeSize(move_x + sizeChangeCommand.before_width,
                    -move_y + sizeChangeCommand.before_height);
            nowControl.setPosition(sizeChangeCommand.before_pos_x,
                    move_y + sizeChangeCommand.before_pos_y);
        } else if (cursor.equals(Cursor.SE_RESIZE)) {
            nowControl.changeSize(move_x + sizeChangeCommand.before_width,
                    move_y + sizeChangeCommand.before_height);
            nowControl.setPosition(sizeChangeCommand.before_pos_x, sizeChangeCommand.before_pos_y);
        } else if (cursor.equals(Cursor.W_RESIZE)) {
            nowControl.changeSize(-move_x + sizeChangeCommand.before_width,
                    sizeChangeCommand.before_height);
            nowControl.setPosition(move_x + sizeChangeCommand.before_pos_x,
                    sizeChangeCommand.before_pos_y);
        } else if (cursor.equals(Cursor.E_RESIZE)) {
            nowControl.changeSize(move_x + sizeChangeCommand.before_width,
                    sizeChangeCommand.before_height);
            nowControl.setPosition(sizeChangeCommand.before_pos_x,
                    sizeChangeCommand.before_pos_y);
        } else if (cursor.equals(Cursor.N_RESIZE)) {
            nowControl.changeSize(sizeChangeCommand.before_width,
                    -move_y + sizeChangeCommand.before_height);
            nowControl.setPosition(sizeChangeCommand.before_pos_x,
                    move_y + sizeChangeCommand.before_pos_y);
        } else if (cursor.equals(Cursor.S_RESIZE)) {
            nowControl.changeSize(sizeChangeCommand.before_width,
                    move_y + sizeChangeCommand.before_height);
            nowControl.setPosition(sizeChangeCommand.before_pos_x, sizeChangeCommand.before_pos_y);
        }
        nowControl.isClicked = true;
    }
}
