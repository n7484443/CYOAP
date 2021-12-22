package cyoap_main.design.choice;

import java.io.IOException;
import java.util.AbstractMap;

import cyoap_main.command.SizeChangeCommand;
import cyoap_main.unit.Vector2f;
import cyoap_main.util.RenderUtil;
import cyoap_main.util.SizeUtil;
import javafx.geometry.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import org.fxmisc.richtext.InlineCssTextArea;

import cyoap_main.command.CombineCommand;
import cyoap_main.command.MoveCommand;
import cyoap_main.core.JavaFxMain;
import cyoap_main.controller.createGui.CreateGuiController;
import cyoap_main.design.node_extension.ImageCell;
import cyoap_main.unit.Bound2f;
import cyoap_main.util.FlagUtil;
import cyoap_main.util.LoadUtil;
import javafx.scene.Cursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class ChoiceSetGuiComponent {
    public static Border border_default = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2)));
    /*
    pane─┬─pane_surround─┬──pane_inner─┬─hbox_title──title
         │               │             ├─image
         │               │             └─area
         └─pane_border   └──hbox_subChoiceSet──subChoiceSet
     */
    public AnchorPane pane = new AnchorPane();
    private GridPane pane_surround = new GridPane();
    private Pane pane_border = new Pane();
    private GridPane pane_inner = new GridPane();
    private HBox hbox_subChoiceSet = new HBox();
    public InlineCssTextArea area = new InlineCssTextArea();
    private ImageCell image = new ImageCell();
    private HBox hbox_title = new HBox();

    public ChoiceSet motherChoiceSet;

    public Color color;
    private Label title = new Label();

    public ChoiceSetGuiComponent(ChoiceSet choiceSet) {
        motherChoiceSet = choiceSet;
    }

    public void setUp() {
        pane.setLayoutX(motherChoiceSet.pos_x);
        pane.setLayoutY(motherChoiceSet.pos_y);

        pane.getChildren().add(pane_surround);
        pane.getChildren().add(pane_border);
        AnchorPane.setLeftAnchor(pane_surround, 0d);
        AnchorPane.setRightAnchor(pane_surround, 0d);
        AnchorPane.setTopAnchor(pane_surround, 0d);
        AnchorPane.setBottomAnchor(pane_surround, 0d);
        AnchorPane.setLeftAnchor(pane_border, 0d);
        AnchorPane.setRightAnchor(pane_border, 0d);
        AnchorPane.setTopAnchor(pane_border, 0d);
        AnchorPane.setBottomAnchor(pane_border, 0d);
        pane_border.setMouseTransparent(true);
        pane_surround.add(pane_inner, 0, 0);
        pane_surround.add(hbox_subChoiceSet, 0, 1);
        hbox_subChoiceSet.setVisible(false);

        try {
            area.setWrapText(true);
            area.getStylesheets().add(LoadUtil.instance.loadCss("/lib/css/text_editor.css"));
            area.getStyleClass().add("text-editor");
            area.setStyle("-color-text: white ;");
            area.setAutoHeight(true);
            area.setId("area_choiceSet");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Rectangle rectangle = new Rectangle(150, 150);
        rectangle.setArcWidth(10.0f);
        rectangle.setArcHeight(10.0f);
        rectangle.widthProperty().bind(pane.widthProperty());
        rectangle.heightProperty().bind(pane.heightProperty());

        pane.setClip(rectangle);
        pane_border.setBorder(border_default);

        GridPane.setHgrow(pane_inner, Priority.ALWAYS);
        GridPane.setVgrow(pane_inner, Priority.ALWAYS);
        pane.setId("pane_choiceset");

        hbox_title.getChildren().add(title);
        pane_inner.add(hbox_title, 0, 0);
        pane_inner.add(image, 0, 1);
        pane_inner.add(area, 0, 2);
        pane_inner.setAlignment(Pos.CENTER);
        pane_inner.setPadding(Insets.EMPTY);

        title.setTextAlignment(TextAlignment.CENTER);
        title.setAlignment(Pos.CENTER);
        HBox.setHgrow(hbox_title, Priority.ALWAYS);
        hbox_title.setAlignment(Pos.CENTER);
        hbox_title.setId("title_choiceset");

        area.setEditable(false);

        image.setId("image_choiceset");
        image.setCut(0);
        image.setPadding(new Insets(5));

        pane_inner.setMouseTransparent(true);
        float border = 9.5f;

        if (JavaFxMain.controller.isEditable()) {
            pane.setOnMouseMoved(e -> {
                boolean b = SizeUtil.setCursor(e.getX(), e.getY(), this.motherChoiceSet.getWidth(), this.motherChoiceSet.getHeight(), border);
                if (b) {
                    CreateGuiController.instance.nowSizeChange = new AbstractMap.SimpleEntry<>(motherChoiceSet, new SizeChangeCommand(motherChoiceSet));
                }
            });
            pane.setOnMouseClicked(e -> {
                if (e.getButton().equals(MouseButton.PRIMARY)) {
                    if (e.getClickCount() == 2) {
                        CreateGuiController.instance.nowEditDataSet = motherChoiceSet;
                        CreateGuiController.instance.loadFromDataSet(motherChoiceSet);
                        CreateGuiController.instance.changeTab(CreateGuiController.instance.tab_describe);
                        e.consume();
                    }
                }
                this.pane.toFront();
                this.pane.setViewOrder(-2.0d);
            });
            pane.setOnMouseDragged(e -> {
                if (e.getButton().equals(MouseButton.MIDDLE)) {
                    if (moveCommand == null) {
                        moveCommand = new MoveCommand(motherChoiceSet.pos_x, motherChoiceSet.pos_y, motherChoiceSet);
                    }
                    var platform = CreateGuiController.platform;
                    var pos_before = CreateGuiController.instance.getPositionFromMouse(platform.start_mouse_x, platform.start_mouse_y);
                    var pos_after = CreateGuiController.instance.getPositionFromMouse(e.getSceneX(), e.getSceneY());

                    pos_after.sub(pos_before);
                    var move = pos_after.sub(pos_before);

                    CreateGuiController.platform.start_mouse_x = e.getSceneX();
                    CreateGuiController.platform.start_mouse_y = e.getSceneY();
                    motherChoiceSet.updatePosition(move.x(), move.y());
                    update();
                }
                this.pane.toFront();
                this.pane.setViewOrder(-2.0d);
            });
            pane.setOnMouseReleased(e -> {
                if (e.getButton().equals(MouseButton.MIDDLE)) {
                    if (moveCommand == null)
                        return;
                    if (moveCommand.start_x != motherChoiceSet.pos_x || moveCommand.start_y != motherChoiceSet.pos_y) {
                        var v = moveCommand.checkOutline(this.motherChoiceSet, motherChoiceSet.pos_x, motherChoiceSet.pos_y);
                        moveCommand.setEnd(v.x(), v.y());
                        motherChoiceSet.setPosition(v.x(), v.y());
                        CreateGuiController.instance.commandTimeline.addCommand(moveCommand);
                    }
                    moveCommand = null;

                    ChoiceSet final_choice = null;
                    Bound2f bound = new Bound2f(motherChoiceSet.bound);
                    float mul = 0.8f;
                    bound.x += bound.width * (1f - mul) * 0.5f;
                    bound.y += bound.height * (1f - mul) * 0.5f;
                    bound.width *= mul;
                    bound.height *= mul;
                    for (var choiceSet : CreateGuiController.platform.choiceSetList) {
                        if (choiceSet == motherChoiceSet)
                            continue;
                        if (bound.intersect(choiceSet.bound)) {
                            final_choice = choiceSet;
                            break;
                        }
                    }
                    var t = CreateGuiController.platform.checkLine(motherChoiceSet, 10f);
                    if (t != null) {
                        var v = t.getKey();
                        motherChoiceSet.setPosition(v.x() == 0 ? motherChoiceSet.pos_x : v.x(), v.y() == 0 ? motherChoiceSet.pos_y : v.y());
                    }
                    if (final_choice != null) {
                        CreateGuiController.instance.commandTimeline
                                .excuteCommand(new CombineCommand(final_choice, motherChoiceSet));
                    }
                }
                this.pane.setViewOrder(0.0d);
            });
            pane.setOnMouseEntered(e -> CreateGuiController.instance.nowMouseInDataSet = motherChoiceSet);

            pane.setOnMouseExited(e -> {
                if (!motherChoiceSet.isClicked)
                    JavaFxMain.instance.scene_create.setCursor(Cursor.DEFAULT);
            });
        }

        updateColor();
        setHorizontal(false);
    }

    public MoveCommand moveCommand = null;

    public void update() {
        title.setText(motherChoiceSet.string_title);
        updateColor();
        if (motherChoiceSet.string_image_name != null && !motherChoiceSet.string_image_name.isEmpty()) {
            var simpleEntry = LoadUtil.loadImage(motherChoiceSet.string_image_name);
            image.setImage(simpleEntry.getKey());
        }
        image.round.set(motherChoiceSet.round);
    }

    public void setBorder(boolean b) {
        pane_border.setVisible(!b);
    }

    public void render(GraphicsContext gc, double time) {
        var platform = JavaFxMain.controller.getPlatform();
        var min_x = platform.min_x;
        var min_y = platform.min_y;

        RenderUtil.setStroke(gc, time, Color.CORNFLOWERBLUE);
        if (JavaFxMain.controller instanceof CreateGuiController creategui) {
            if (moveCommand != null && motherChoiceSet.equals(moveCommand.choiceset)) {
                var entry = platform.checkLine(motherChoiceSet, 10f);
                if (entry != null) {
                    var show_x = entry.getValue().x();
                    var show_y = entry.getValue().y();
                    if (show_x != Float.MAX_VALUE) {
                        gc.strokeLine(show_x - min_x, CreateGuiController.instance.getPlatform().min_y - min_y, show_x - min_x, CreateGuiController.instance.getPlatform().max_y - min_y);
                    }
                    if (show_y != Float.MAX_VALUE) {
                        gc.strokeLine(CreateGuiController.instance.getPlatform().min_x - min_x, show_y - min_y, CreateGuiController.instance.getPlatform().max_x - min_x, show_y - min_y);
                    }
                }
            }
            if (creategui.nowSizeChange != null && motherChoiceSet.equals(creategui.nowSizeChange.getKey())) {
                var cursor = JavaFxMain.instance.scene_create.getCursor();

                var list_point = SizeUtil.pointMagnet(motherChoiceSet);

                Vector2f point = null;
                if (list_point[0] != null && (cursor.equals(Cursor.W_RESIZE) || cursor.equals(Cursor.N_RESIZE) || cursor.equals(Cursor.NW_RESIZE))) {//x and y are negative
                    point = list_point[0];
                } else if (list_point[3] != null && (cursor.equals(Cursor.S_RESIZE) || cursor.equals(Cursor.E_RESIZE) || cursor.equals(Cursor.SE_RESIZE))) {//x and y are positive
                    point = list_point[3];
                } else if (list_point[2] != null && cursor.equals(Cursor.SW_RESIZE)) {
                    point = list_point[2];
                } else if (list_point[1] != null && cursor.equals(Cursor.NE_RESIZE)) {
                    point = list_point[1];
                }
                if (point != null) {
                    if (point.x() == Float.MAX_VALUE && point.y() != Float.MAX_VALUE) {//only change y
                        RenderUtil.renderStrokeHorizontal(gc, platform, point.y());
                    } else if (point.x() != Float.MAX_VALUE && point.y() == Float.MAX_VALUE) {//only change x
                        RenderUtil.renderStrokeVertical(gc, platform, point.x());
                    } else {//both change
                        RenderUtil.renderStrokeVertical(gc, platform, point.x());
                        RenderUtil.renderStrokeHorizontal(gc, platform, point.y());
                    }
                }
            }
        }

        if (motherChoiceSet.equals(CreateGuiController.instance.nowMouseInDataSet)) {
            RenderUtil.setStroke(gc, time, Color.BLUE);
            var gap = 4;
            var x_start = motherChoiceSet.pos_x - gap - min_x;
            var y_start = motherChoiceSet.pos_y - gap - min_y;

            gc.strokeRect(x_start, y_start, motherChoiceSet.getAnchorPane().getLayoutBounds().getWidth() + gap * 2, motherChoiceSet.getAnchorPane().getLayoutBounds().getHeight() + gap * 2);
        }
    }

    public void setPosition(double posX, double posY) {
        pane.relocate(posX, posY);
    }

    public void setTranslation(double moveX, double moveY) {
        pane.setTranslateX(moveX);
        pane.setTranslateY(moveY);
    }


    public void updateColor() {
        pane.setStyle("-fx-background-color: #" + color.toString().replace("0x", "") + ";");
        Color c = color.deriveColor(30, 1.0, 1 / 0.7f, 1.0);
        hbox_title.setStyle("-fx-background-color: #" + c.toString().replace("0x", "") + ";");
        if (c.getBrightness() < 0.5) {
            title.setStyle("-fx-text-fill: #" + Color.WHITE.toString().replace("0x", "") + ";");
        } else {
            title.setStyle("-fx-text-fill: #" + Color.BLACK.toString().replace("0x", "") + ";");
        }
    }

    public void combineSubChoiceSetComponent(ChoiceSet sub) {
        if (sub.choiceSet_parent != null) {
            sub.choiceSet_parent.getAnchorPane().getChildren().remove(sub.getAnchorPane());
        }
        if (!hbox_subChoiceSet.isVisible()) hbox_subChoiceSet.setVisible(true);

        if (!hbox_subChoiceSet.getChildren().contains(sub.getAnchorPane())) {
            hbox_subChoiceSet.getChildren().add(sub.getAnchorPane());
        }
    }

    public void separateSubChoiceSetComponent(ChoiceSet sub) {
        hbox_subChoiceSet.getChildren().remove(sub.getAnchorPane());
        CreateGuiController.instance.pane_position.getChildren().add(sub.getAnchorPane());
        if (hbox_subChoiceSet.getChildren().isEmpty()) hbox_subChoiceSet.setVisible(false);
    }

    public void setEmptyImage(boolean b) {
        if (b) {
            if ((motherChoiceSet.string_image_name == null || motherChoiceSet.string_image_name.isEmpty()) && pane_inner.getChildren().contains(image)) {
                pane_inner.getChildren().remove(image);
                if (FlagUtil.getFlag(motherChoiceSet.flag, ChoiceSet.flagPosition_horizontal)) {
                    GridPane.setColumnIndex(area, 0);
                    GridPane.setColumnSpan(area, 2);
                    GridPane.setRowIndex(area, 1);
                    GridPane.setRowSpan(area, 1);
                } else {
                    GridPane.setColumnIndex(area, 0);
                    GridPane.setColumnSpan(area, 1);
                    GridPane.setRowIndex(area, 1);
                    GridPane.setRowSpan(area, 2);
                }
            }
        } else {
            if (!pane_inner.getChildren().contains(image)) {
                setHorizontal(FlagUtil.getFlag(motherChoiceSet.flag, ChoiceSet.flagPosition_horizontal));
            }
        }
    }

    public void setHorizontal(boolean b) {
        int size = 20;
        hbox_title.setPrefHeight(size);
        GridPane.setHalignment(hbox_title, HPos.CENTER);
        GridPane.setValignment(hbox_title, VPos.CENTER);

        pane_inner.getChildren().remove(hbox_title);
        pane_inner.getChildren().remove(image);
        pane_inner.getChildren().remove(area);
        pane_inner.getColumnConstraints().clear();
        pane_inner.getRowConstraints().clear();
        pane_inner.setVgap(0);
        pane_inner.setHgap(0);
        pane_inner.add(hbox_title, 0, 0);
        if (b) {
            GridPane.setColumnSpan(hbox_title, 2);
            pane_inner.add(image, 0, 1);
            pane_inner.add(area, 1, 1);

            ColumnConstraints col1 = new ColumnConstraints();
            ColumnConstraints col2 = new ColumnConstraints();
            RowConstraints row1 = new RowConstraints();
            RowConstraints row2 = new RowConstraints();
            col1.setPercentWidth(32);
            col1.setHgrow(Priority.ALWAYS);
            col2.setPercentWidth(68);
            col2.setHgrow(Priority.ALWAYS);

            row1.setPercentHeight(10);
            row1.setVgrow(Priority.ALWAYS);
            row2.setPercentHeight(90);
            row2.setVgrow(Priority.ALWAYS);

            pane_inner.getColumnConstraints().addAll(col1, col2);
            pane_inner.getRowConstraints().addAll(row1, row2);
        } else {
            GridPane.setColumnSpan(hbox_title, 1);
            pane_inner.add(image, 0, 1);
            pane_inner.add(area, 0, 2);

            ColumnConstraints col1 = new ColumnConstraints();
            RowConstraints row1 = new RowConstraints();
            RowConstraints row2 = new RowConstraints();
            RowConstraints row3 = new RowConstraints();
            col1.setPercentWidth(100);
            col1.setHgrow(Priority.ALWAYS);

            row1.setPercentHeight(10);
            row1.setVgrow(Priority.ALWAYS);
            row2.setPercentHeight(90 * 0.68);
            row2.setVgrow(Priority.ALWAYS);
            row3.setPercentHeight(90 * 0.32);
            row3.setVgrow(Priority.ALWAYS);

            pane_inner.getColumnConstraints().addAll(col1);
            pane_inner.getRowConstraints().addAll(row1, row2, row3);
        }
    }

    public void addChoiceSetGui(Pane pane) {
        hbox_subChoiceSet.getChildren().add(pane);
    }
}
