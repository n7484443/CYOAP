package cyoap_main.design.choice;

import java.io.IOException;

import org.fxmisc.richtext.InlineCssTextArea;

import cyoap_main.command.CombineCommand;
import cyoap_main.command.MoveCommand;
import cyoap_main.core.JavaFxMain;
import cyoap_main.design.controller.createGui.CreateGuiController;
import cyoap_main.design.node_extension.ImageCell;
import cyoap_main.unit.Bound2f;
import cyoap_main.util.FlagUtil;
import cyoap_main.util.LoadUtil;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.TextAlignment;

public class ChoiceSetGuiComponent {
	public GridPane pane = new GridPane();
	public GridPane pane_border = new GridPane();
	public HBox hbox = new HBox();
	public ImageCell image = new ImageCell();
	public InlineCssTextArea area = new InlineCssTextArea();
	public Label title = new Label();

	public ChoiceSet motherChoiceSet;

	public Color color;

	public float width_before = 0;
	public float height_before = 0;
	public float x_before = 0;
	public float y_before = 0;

	public static Border border_default = new Border(new BorderStroke(Color.BLACK,
			new BorderStrokeStyle(StrokeType.OUTSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10, 0, null),
			new CornerRadii(2.5), new BorderWidths(1)));

	public ChoiceSetGuiComponent(Color color) {
		this.color = color;
	}

	public void setUp(ChoiceSet dataSet) {
		motherChoiceSet = dataSet;

		try {
			area.setWrapText(true);
			area.getStylesheets().add(LoadUtil.instance.loadCss("/lib/css/texteditor.css"));
			area.getStyleClass().add("text-editor");
			area.setStyle("-color-text: white ;");
			area.setAutoHeight(true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		pane.setLayoutX(dataSet.pos_x);
		pane.setLayoutY(dataSet.pos_y);
		pane.setBorder(border_default);
		pane.add(pane_border, 0, 0);

		GridPane.setHgrow(pane_border, Priority.ALWAYS);
		GridPane.setVgrow(pane_border, Priority.ALWAYS);
		pane.setId("pane_choiceset");

		pane_border.add(title, 0, 0);
		pane_border.add(image, 0, 1);

		// image.setAlignment(Pos.CENTER);

		pane_border.add(area, 0, 2);
		pane_border.setAlignment(Pos.CENTER);
		pane_border.setPadding(Insets.EMPTY);

		title.setTextAlignment(TextAlignment.CENTER);
		title.setAlignment(Pos.CENTER);
		title.setId("title_choiceset");

		area.setEditable(false);

		image.setId("image_choiceset");

		pane_border.setMouseTransparent(true);
		float border = 9f;

		if (JavaFxMain.controller.isEditable()) {
			pane.setOnMouseMoved(e -> {
				var x = e.getX();
				var y = e.getY();
				var width = this.motherChoiceSet.getWidth() - x;
				var height = this.motherChoiceSet.getHeight() - y;
				boolean b = false;
				var b_square = border * border;
				if (x * x < b_square && y * y < b_square) {
					JavaFxMain.instance.scene_create.setCursor(Cursor.NW_RESIZE);
					b = true;
				} else if (width * width < b_square && y * y < b_square) {
					JavaFxMain.instance.scene_create.setCursor(Cursor.NE_RESIZE);
					b = true;
				} else if (x * x < b_square && height * height < b_square) {
					JavaFxMain.instance.scene_create.setCursor(Cursor.SW_RESIZE);
					b = true;
				} else if (width * width < b_square && height * height < b_square) {
					JavaFxMain.instance.scene_create.setCursor(Cursor.SE_RESIZE);
					b = true;
				} else {
					JavaFxMain.instance.scene_create.setCursor(Cursor.DEFAULT);
				}
				if (b) {
					CreateGuiController.instance.nowControl = this.motherChoiceSet;
					width_before = this.motherChoiceSet.getWidth();
					height_before = this.motherChoiceSet.getHeight();
					x_before = this.motherChoiceSet.pos_x;
					y_before = this.motherChoiceSet.pos_y;
				}
			});
			pane.setOnMouseClicked(e -> {
				if (e.getButton().equals(MouseButton.PRIMARY)) {
					if (e.getClickCount() == 2) {
						CreateGuiController.instance.nowEditDataSet = dataSet;
						CreateGuiController.instance.loadFromDataSet(dataSet);
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
						moveCommand = new MoveCommand(dataSet.pos_x, dataSet.pos_y, dataSet);
					}
					double movex = CreateGuiController.platform.sensitivity
							* (e.getSceneX() - CreateGuiController.platform.start_mouse_x);
					double movey = CreateGuiController.platform.sensitivity
							* (e.getSceneY() - CreateGuiController.platform.start_mouse_y);
					CreateGuiController.platform.start_mouse_x = e.getSceneX();
					CreateGuiController.platform.start_mouse_y = e.getSceneY();
					dataSet.updatePosition(movex, movey);
				}
				this.pane.toFront();
				this.pane.setViewOrder(-2.0d);
			});
			pane.setOnMouseReleased(e -> {
				if (e.getButton().equals(MouseButton.MIDDLE)) {
					if (moveCommand == null)
						return;
					if (moveCommand.start_x != dataSet.pos_x || moveCommand.start_y != dataSet.pos_y) {
						var v = moveCommand.checkOutline(this.motherChoiceSet, dataSet.pos_x, dataSet.pos_y);
						dataSet.pos_x = v.x();
						dataSet.pos_y = v.y();

						moveCommand.setEnd(v.x(), v.y());
						CreateGuiController.instance.commandTimeline.addCommand(moveCommand);
					}
					moveCommand = null;

					ChoiceSet final_choice = null;
					Bound2f bound = new Bound2f(dataSet.bound);
					float mul = 0.8f;
					bound.x += bound.width * (1f - mul) * 0.5f;
					bound.y += bound.height * (1f - mul) * 0.5f;
					bound.width *= mul;
					bound.height *= mul;
					for (var choiceSet : CreateGuiController.platform.choiceSetList) {
						if (choiceSet == dataSet)
							continue;
						if (bound.intersect(choiceSet.bound)) {
							final_choice = choiceSet;
							break;
						}
					}
					var t = CreateGuiController.platform.checkLine(dataSet, 10f);
					if (t != null) {
						var v = t.getKey();
						dataSet.pos_x = v.x() == 0 ? dataSet.pos_x : v.x();
						dataSet.pos_y = v.y() == 0 ? dataSet.pos_y : v.y();
					}
					if (final_choice != null) {
						CreateGuiController.instance.commandTimeline
								.excuteCommand(new CombineCommand(final_choice, dataSet));
					}
				}
				this.pane.setViewOrder(0.0d);
			});
			pane.setOnMouseEntered(e -> CreateGuiController.instance.nowMouseInDataSet = dataSet);

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
	}

	public void render(GraphicsContext gc, double time) {
		var lx = JavaFxMain.controller.getPlatform().local_x;
		var ly = JavaFxMain.controller.getPlatform().local_y;
		if (moveCommand != null && motherChoiceSet.equals(moveCommand.choiceset)) {
			var entry = CreateGuiController.platform.checkLine(motherChoiceSet, 10f);
			if (entry != null) {
				var v = entry.getKey();
				var flag = entry.getValue();
				var show_x = (v.x() == 0 ? motherChoiceSet.pos_x : v.x()) - lx;
				var show_y = (v.y() == 0 ? motherChoiceSet.pos_y : v.y()) - ly;
				gc.setStroke(Color.CORNFLOWERBLUE);
				gc.setLineWidth(1);
				gc.setLineDashes(5);
				gc.setLineDashOffset((time * 20) % 1000);
				if (FlagUtil.getFlag(flag, 0)) {
					show_x += motherChoiceSet.getWidth();
				} else if (FlagUtil.getFlag(flag, 1)) {
					show_x += motherChoiceSet.getWidth() / 2;
				}
				gc.strokeLine(show_x, 0, show_x, gc.getCanvas().getHeight());

				if (FlagUtil.getFlag(flag, 2)) {
					show_y += motherChoiceSet.getHeight();
				} else if (FlagUtil.getFlag(flag, 3)) {
					show_y += motherChoiceSet.getHeight() / 2;
				}
				gc.strokeLine(0, show_y, gc.getCanvas().getWidth(), show_y);
			}
		}
		if (motherChoiceSet.equals(CreateGuiController.instance.nowMouseInDataSet)) {
			gc.setStroke(Color.BLUE);
			gc.setLineWidth(1);
			gc.setLineDashes(5);
			gc.setLineDashOffset((time * 20) % 1000);
			var gap = 4;
			var x1 = motherChoiceSet.pos_x - gap - lx;
			var x2 = motherChoiceSet.pos_x + motherChoiceSet.getAnchorPane().getLayoutBounds().getWidth() + gap
					- JavaFxMain.controller.getPlatform().local_x;
			var y1 = motherChoiceSet.pos_y - gap - ly;
			var y2 = motherChoiceSet.pos_y + motherChoiceSet.getAnchorPane().getLayoutBounds().getHeight() + gap
					- JavaFxMain.controller.getPlatform().local_y;
			gc.strokeLine(x1, y1, x1, y2);
			gc.strokeLine(x2, y2, x2, y1);
			gc.strokeLine(x2, y1, x1, y1);
			gc.strokeLine(x1, y2, x2, y2);
		}
	}

	public void updatePos(double moveX, double moveY) {
		pane.relocate(moveX, moveY);
	}

	public void updateColor() {
		pane.setStyle("-fx-background-color: #" + color.toString().replace("0x", "") + ";");
	}

	public void combineSubChoiceSetComponent(ChoiceSet sub) {
		if (sub.choiceSet_parent != null) {
			sub.choiceSet_parent.getAnchorPane().getChildren().remove(sub.getAnchorPane());
		}
		if(!pane.getChildren().contains(hbox)){
			pane.add(hbox, 0, 3);
		}
		if(!hbox.getChildren().contains(sub.getAnchorPane())){
			hbox.getChildren().add(sub.getAnchorPane());
		}
	}

	public void seperateSubChoiceSetComponent(ChoiceSet sub) {
		pane.getChildren().remove(hbox);
		hbox.getChildren().remove(sub.getAnchorPane());
		CreateGuiController.instance.pane_position.getChildren().add(sub.getAnchorPane());
	}

	public void setHorizontal(boolean b) {
		int size = 20;
		title.setPrefHeight(size);
		GridPane.setHalignment(title, HPos.CENTER);
		GridPane.setValignment(title, VPos.CENTER);

		pane_border.getChildren().remove(title);
		pane_border.getChildren().remove(image);
		pane_border.getChildren().remove(area);
		pane_border.getColumnConstraints().clear();
		pane_border.getRowConstraints().clear();
		if (b) {
			pane_border.setVgap(0);
			pane_border.setHgap(5);

			pane_border.add(title, 0, 0);
			GridPane.setColumnSpan(title, 2);
			pane_border.add(image, 0, 1);
			pane_border.add(area, 1, 1);

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

			pane_border.getColumnConstraints().addAll(col1, col2);
			pane_border.getRowConstraints().addAll(row1, row2);
		} else {
			pane_border.setVgap(5);
			pane_border.setHgap(0);

			pane_border.add(title, 0, 0);
			GridPane.setColumnSpan(title, 1);
			pane_border.add(image, 0, 1);
			pane_border.add(area, 0, 2);

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

			pane_border.getColumnConstraints().addAll(col1);
			pane_border.getRowConstraints().addAll(row1, row2, row3);
		}
	}
}
