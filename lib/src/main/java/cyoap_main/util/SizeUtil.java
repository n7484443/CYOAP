package cyoap_main.util;

import cyoap_main.command.SizeChangeCommand;
import cyoap_main.core.JavaFxMain;
import cyoap_main.design.choice.ChoiceSet;
import cyoap_main.controller.createGui.CreateGuiController;
import cyoap_main.unit.Vector2f;
import javafx.scene.Cursor;

import java.util.AbstractMap.SimpleEntry;

public class SizeUtil {
    public static boolean setCursor(double x, double y, double width_origin, double height_origin, float border) {
        var width = width_origin - x;
        var height = height_origin - y;
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
            JavaFxMain.instance.scene_create.setCursor(Cursor.DEFAULT);
            return false;
        }
        return true;
    }

    public static void setSize(SimpleEntry<ChoiceSet, SizeChangeCommand> nowSizeChange, float move_x, float move_y) {
        var scene_create = JavaFxMain.instance.scene_create;
        var nowControl = nowSizeChange.getKey();
        var sizeChangeCommand = nowSizeChange.getValue();
        var cursor = scene_create.getCursor();
        if (!nowControl.isClicked) nowControl.isClicked = true;

        if (cursor.equals(Cursor.NW_RESIZE)) {
            nowControl.setSize(-move_x + sizeChangeCommand.before.width,
                    -move_y + sizeChangeCommand.before.height);
            nowControl.setPosition(move_x + sizeChangeCommand.before.x,
                    move_y + sizeChangeCommand.before.y);
        } else if (cursor.equals(Cursor.SE_RESIZE)) {
            nowControl.setSize(move_x + sizeChangeCommand.before.width,
                    move_y + sizeChangeCommand.before.height);
            nowControl.setPosition(sizeChangeCommand.before.x, sizeChangeCommand.before.y);
        } else if (cursor.equals(Cursor.SW_RESIZE)) {
            nowControl.setSize(-move_x + sizeChangeCommand.before.width,
                    move_y + sizeChangeCommand.before.height);
            nowControl.setPosition(move_x + sizeChangeCommand.before.x,
                    sizeChangeCommand.before.y);
        } else if (cursor.equals(Cursor.NE_RESIZE)) {
            nowControl.setSize(move_x + sizeChangeCommand.before.width,
                    -move_y + sizeChangeCommand.before.height);
            nowControl.setPosition(sizeChangeCommand.before.x,
                    move_y + sizeChangeCommand.before.y);
        } else if (cursor.equals(Cursor.W_RESIZE)) {
            nowControl.setSize(-move_x + sizeChangeCommand.before.width,
                    sizeChangeCommand.before.height);
            nowControl.setPosition(move_x + sizeChangeCommand.before.x,
                    sizeChangeCommand.before.y);
        } else if (cursor.equals(Cursor.E_RESIZE)) {
            nowControl.setSize(move_x + sizeChangeCommand.before.width,
                    sizeChangeCommand.before.height);
            nowControl.setPosition(sizeChangeCommand.before.x,
                    sizeChangeCommand.before.y);
        } else if (cursor.equals(Cursor.N_RESIZE)) {
            nowControl.setSize(sizeChangeCommand.before.width,
                    -move_y + sizeChangeCommand.before.height);
            nowControl.setPosition(sizeChangeCommand.before.x,
                    move_y + sizeChangeCommand.before.y);
        } else if (cursor.equals(Cursor.S_RESIZE)) {
            nowControl.setSize(sizeChangeCommand.before.width,
                    move_y + sizeChangeCommand.before.height);
            nowControl.setPosition(sizeChangeCommand.before.x, sizeChangeCommand.before.y);
        }
    }

    public static void setSizeComplete(SimpleEntry<ChoiceSet, SizeChangeCommand> nowSizeChange) {
        var nowControl = nowSizeChange.getKey();
        var cursor = JavaFxMain.instance.scene_create.getCursor();

        var list_point = pointMagnet(nowControl);

        boolean b = true;
        for (var v : list_point) {
            if (v != null) b = false;
        }
        if (b) return;

        if (list_point[0] != null && (cursor.equals(Cursor.W_RESIZE) || cursor.equals(Cursor.N_RESIZE) || cursor.equals(Cursor.NW_RESIZE))) {//x and y are negative
            if (list_point[0].x() == Float.MAX_VALUE && list_point[0].y() != Float.MAX_VALUE) {//only change y
                nowControl.setSize(nowControl.getWidth(), nowControl.bound.y - list_point[0].y() + nowControl.getHeight());
                nowControl.setPosition(nowControl.bound.x, list_point[0].y());
            } else if (list_point[0].x() != Float.MAX_VALUE && list_point[0].y() == Float.MAX_VALUE) {//only change x
                nowControl.setSize(nowControl.bound.x - list_point[0].x() + nowControl.getWidth(), nowControl.getHeight());
                nowControl.setPosition(list_point[0].x(), nowControl.bound.y);
            } else {//both change
                nowControl.setSize(nowControl.bound.x - list_point[0].x() + nowControl.getWidth(), nowControl.bound.y - list_point[0].y() + nowControl.getHeight());
                nowControl.setPosition(list_point[0].x(), list_point[0].y());
            }
        } else if (list_point[3] != null && (cursor.equals(Cursor.S_RESIZE) || cursor.equals(Cursor.E_RESIZE) || cursor.equals(Cursor.SE_RESIZE))) {//x and y are positive
            if (list_point[3].x() == Float.MAX_VALUE && list_point[3].y() != Float.MAX_VALUE) {//only change y
                nowControl.setSize(nowControl.getWidth(), list_point[3].y() - nowControl.bound.y);
            } else if (list_point[3].x() != Float.MAX_VALUE && list_point[3].y() == Float.MAX_VALUE) {//only change x
                nowControl.setSize(list_point[3].x() - nowControl.bound.x, nowControl.getHeight());
            } else {//both change
                nowControl.setSize(list_point[3].x() - nowControl.bound.x, list_point[3].y() - nowControl.bound.y);
            }
        } else if (list_point[2] != null && cursor.equals(Cursor.SW_RESIZE)) {
            if (list_point[2].x() == Float.MAX_VALUE && list_point[2].y() != Float.MAX_VALUE) {//only change y
                nowControl.setSize(nowControl.getWidth(), list_point[2].y() - nowControl.bound.y);
            } else if (list_point[2].x() != Float.MAX_VALUE && list_point[2].y() == Float.MAX_VALUE) {//only change x
                nowControl.setSize(nowControl.bound.x - list_point[2].x() + nowControl.getWidth(), nowControl.getHeight());
                nowControl.setPosition(list_point[2].x(), nowControl.bound.y);
            } else {//both change
                nowControl.setSize(nowControl.bound.x - list_point[2].x() + nowControl.getWidth(), list_point[2].y() - nowControl.bound.y);
                nowControl.setPosition(list_point[2].x(), nowControl.bound.y);
            }
        } else if (list_point[1] != null && cursor.equals(Cursor.NE_RESIZE)) {
            if (list_point[1].x() == Float.MAX_VALUE && list_point[1].y() != Float.MAX_VALUE) {//only change y
                nowControl.setSize(nowControl.getWidth(), nowControl.bound.y - list_point[2].y() + nowControl.getHeight());
                nowControl.setPosition(nowControl.bound.x, list_point[1].y());
            } else if (list_point[1].x() != Float.MAX_VALUE && list_point[1].y() == Float.MAX_VALUE) {//only change x
                nowControl.setSize(list_point[1].x() - nowControl.bound.x, nowControl.getHeight());
            } else {//both change
                nowControl.setSize(list_point[1].x() - nowControl.bound.x, nowControl.bound.y - list_point[2].y() + nowControl.getHeight());
                nowControl.setPosition(nowControl.bound.x, list_point[1].y());
            }
        }
    }

    public static Vector2f[] pointMagnet(ChoiceSet nowControl) {
        var list_point = new Vector2f[4];

        list_point[0] = CreateGuiController.platform.checkPoint(nowControl, new Vector2f(nowControl.bound.x, nowControl.bound.y), 10f);
        list_point[1] = CreateGuiController.platform.checkPoint(nowControl, new Vector2f(nowControl.bound.x + nowControl.getWidth(), nowControl.bound.y), 10f);
        list_point[2] = CreateGuiController.platform.checkPoint(nowControl, new Vector2f(nowControl.bound.x, nowControl.bound.y + nowControl.getHeight()), 10f);
        list_point[3] = CreateGuiController.platform.checkPoint(nowControl, new Vector2f(nowControl.bound.x + nowControl.getWidth(), nowControl.bound.y + nowControl.getHeight()), 10f);

        return list_point;
    }
}