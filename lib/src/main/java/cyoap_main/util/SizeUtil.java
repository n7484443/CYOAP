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

    public static boolean isInside(double a, double x, double b) {
        return (a <= x & x <= b) || (a >= x & x >= b);
    }

    public static boolean setCursorRound(double x, double y, double width_origin, double height_origin, float border, float round) {
        var width = width_origin;
        var height = height_origin;
        boolean b = true;
        if (round <= 10) round = 10;
        float b_small = (round - border) * (round - border);
        if (round < border) {
            b_small = 0;
        }
        var b_big = round * round;

        var pos = new Vector2f((float) x, (float) y);

        var left_up = new Vector2f(round, round);
        var right_up = new Vector2f((float) (width - round), round);
        var left_down = new Vector2f(round, (float) (height - round));
        var right_down = new Vector2f((float) (width - round), (float) (height - round));

        var lu = left_up.sub(pos).pow(2).sum();
        var ru = right_up.sub(pos).pow(2).sum();
        var ld = left_down.sub(pos).pow(2).sum();
        var rd = right_down.sub(pos).pow(2).sum();

        var scene_create = JavaFxMain.instance.scene_create;
        if (isInside(b_small, lu, b_big)) {
            scene_create.setCursor(Cursor.NW_RESIZE);
        } else if (isInside(b_small, ru, b_big)) {
            scene_create.setCursor(Cursor.NE_RESIZE);
        } else if (isInside(b_small, ld, b_big)) {
            scene_create.setCursor(Cursor.SW_RESIZE);
        } else if (isInside(b_small, rd, b_big)) {
            scene_create.setCursor(Cursor.SE_RESIZE);
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
        if (!nowControl.isClicked) nowControl.isClicked = true;

        if (cursor.equals(Cursor.NW_RESIZE)) {
            nowControl.changeSize(-move_x + sizeChangeCommand.before_width,
                    -move_y + sizeChangeCommand.before_height);
            nowControl.setPosition(move_x + sizeChangeCommand.before_pos_x,
                    move_y + sizeChangeCommand.before_pos_y);
        } else if (cursor.equals(Cursor.SE_RESIZE)) {
            nowControl.changeSize(move_x + sizeChangeCommand.before_width,
                    move_y + sizeChangeCommand.before_height);
            nowControl.setPosition(sizeChangeCommand.before_pos_x, sizeChangeCommand.before_pos_y);
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
    }

    public static void changeSizeComplete(SimpleEntry<ChoiceSet, SizeChangeCommand> nowSizeChange) {
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
                nowControl.changeSize(nowControl.getWidth(), nowControl.pos_y - list_point[0].y() + nowControl.getHeight());
                nowControl.setPosition(nowControl.pos_x, list_point[0].y());
            } else if (list_point[0].x() != Float.MAX_VALUE && list_point[0].y() == Float.MAX_VALUE) {//only change x
                nowControl.changeSize(nowControl.pos_x - list_point[0].x() + nowControl.getWidth(), nowControl.getHeight());
                nowControl.setPosition(list_point[0].x(), nowControl.pos_y);
            } else {//both change
                nowControl.changeSize(nowControl.pos_x - list_point[0].x() + nowControl.getWidth(), nowControl.pos_y - list_point[0].y() + nowControl.getHeight());
                nowControl.setPosition(list_point[0].x(), list_point[0].y());
            }
        } else if (list_point[3] != null && (cursor.equals(Cursor.S_RESIZE) || cursor.equals(Cursor.E_RESIZE) || cursor.equals(Cursor.SE_RESIZE))) {//x and y are positive
            if (list_point[3].x() == Float.MAX_VALUE && list_point[3].y() != Float.MAX_VALUE) {//only change y
                nowControl.changeSize(nowControl.getWidth(), list_point[3].y() - nowControl.pos_y);
            } else if (list_point[3].x() != Float.MAX_VALUE && list_point[3].y() == Float.MAX_VALUE) {//only change x
                nowControl.changeSize(list_point[3].x() - nowControl.pos_x, nowControl.getHeight());
            } else {//both change
                nowControl.changeSize(list_point[3].x() - nowControl.pos_x, list_point[3].y() - nowControl.pos_y);
            }
        } else if (list_point[2] != null && cursor.equals(Cursor.SW_RESIZE)) {
            if (list_point[2].x() == Float.MAX_VALUE && list_point[2].y() != Float.MAX_VALUE) {//only change y
                nowControl.changeSize(nowControl.getWidth(), list_point[2].y() - nowControl.pos_y);
            } else if (list_point[2].x() != Float.MAX_VALUE && list_point[2].y() == Float.MAX_VALUE) {//only change x
                nowControl.changeSize(nowControl.pos_x - list_point[2].x() + nowControl.getWidth(), nowControl.getHeight());
                nowControl.setPosition(list_point[2].x(), nowControl.pos_y);
            } else {//both change
                nowControl.changeSize(nowControl.pos_x - list_point[2].x() + nowControl.getWidth(), list_point[2].y() - nowControl.pos_y);
                nowControl.setPosition(list_point[2].x(), nowControl.pos_y);
            }
        } else if (list_point[1] != null && cursor.equals(Cursor.NE_RESIZE)) {
            if (list_point[1].x() == Float.MAX_VALUE && list_point[1].y() != Float.MAX_VALUE) {//only change y
                nowControl.changeSize(nowControl.getWidth(), nowControl.pos_y - list_point[2].y() + nowControl.getHeight());
                nowControl.setPosition(nowControl.pos_x, list_point[1].y());
            } else if (list_point[1].x() != Float.MAX_VALUE && list_point[1].y() == Float.MAX_VALUE) {//only change x
                nowControl.changeSize(list_point[1].x() - nowControl.pos_x, nowControl.getHeight());
            } else {//both change
                nowControl.changeSize(list_point[1].x() - nowControl.pos_x, nowControl.pos_y - list_point[2].y() + nowControl.getHeight());
                nowControl.setPosition(nowControl.pos_x, list_point[1].y());
            }
        }
    }

    public static Vector2f[] pointMagnet(ChoiceSet nowControl) {
        var list_point = new Vector2f[4];

        list_point[0] = CreateGuiController.platform.checkPoint(nowControl, new Vector2f(nowControl.pos_x, nowControl.pos_y), 10f);
        list_point[1] = CreateGuiController.platform.checkPoint(nowControl, new Vector2f(nowControl.pos_x + nowControl.getWidth(), nowControl.pos_y), 10f);
        list_point[2] = CreateGuiController.platform.checkPoint(nowControl, new Vector2f(nowControl.pos_x, nowControl.pos_y + nowControl.getHeight()), 10f);
        list_point[3] = CreateGuiController.platform.checkPoint(nowControl, new Vector2f(nowControl.pos_x + nowControl.getWidth(), nowControl.pos_y + nowControl.getHeight()), 10f);

        return list_point;
    }
}