package cyoap_main.design;

import java.io.IOException;

import org.fxmisc.richtext.InlineCssTextArea;

import cyoap_main.command.CombineCommand;
import cyoap_main.command.MoveCommand;
import cyoap_main.core.JavaFxMain;
import cyoap_main.design.controller.createGui.CreateGuiController;
import cyoap_main.unit.Bound2f;
import cyoap_main.unit.Vector2f;
import cyoap_main.util.LoadUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.TextAlignment;

public class ChoiceSetGuiComponent {
	public GridPane pane = new GridPane();
	public GridPane pane_border = new GridPane();
	public Pane pane_middle = new Pane();
	public HBox hbox = new HBox();
	public HBox hbox_image = new HBox();
	public ImageView image = new ImageView();
	public InlineCssTextArea area = new InlineCssTextArea();
	public Label title = new Label();

	public ChoiceSet motherChoiceSet;

	public Color color;

	public float width_before = 0;
	public float height_before = 0;
	public float x_before = 0;
	public float y_before = 0;

	public float minWidth = 200;
	public float minHeight = 250;

	public static Border border_default = new Border(new BorderStroke(Color.BLACK,
			new BorderStrokeStyle(StrokeType.OUTSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10, 0, null),
			new CornerRadii(2.5), new BorderWidths(1)));

	public ChoiceSetGuiComponent() {

	}

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

		pane.setLayoutX(dataSet.posx);
		pane.setLayoutY(dataSet.posy);
		pane.setBorder(border_default);
		pane.add(pane_border, 0, 0);

		GridPane.setHgrow(pane_border, Priority.ALWAYS);
		GridPane.setVgrow(pane_border, Priority.ALWAYS);
		pane.setId("pane_choiceset");

		pane_middle.setPrefHeight(20);

		pane_border.add(title, 0, 0);
		pane_border.add(hbox_image, 0, 1);

		hbox_image.setAlignment(Pos.CENTER);
		hbox_image.getChildren().add(image);

		pane_border.add(area, 0, 3);
		pane_border.setVgap(5);
		pane_border.setHgap(0);
		pane_border.setAlignment(Pos.CENTER);
		pane_border.setPadding(Insets.EMPTY);

		title.setTextAlignment(TextAlignment.CENTER);
		title.setAlignment(Pos.CENTER);
		title.setId("title_choiceset");

		area.setEditable(false);

		image.setPreserveRatio(true);
		image.setId("image_choiceset");

		int size = 20;
		title.setPrefHeight(size);
		title.prefWidthProperty().bind(pane.widthProperty());

		image.fitHeightProperty().bind(pane.heightProperty().subtract(size).multiply(3 / 5f));

		area.prefHeightProperty().bind(pane.heightProperty().subtract(size).subtract(image.fitHeightProperty()));
		area.prefWidthProperty().bind(pane.widthProperty());

		pane_border.setMouseTransparent(true);
		float border = 9f;

		if (JavaFxMain.controller.isEditable()) {
			pane.setOnMouseMoved(e -> {
				var x = e.getX();
				var y = e.getY();
				var width = this.motherChoiceSet.getWidth();
				var height = this.motherChoiceSet.getHeight();
				boolean b = false;
				if ((0 - x) * (0 - x) < border * border && (0 - y) * (0 - y) < border * border) {
					JavaFxMain.instance.scene_create.setCursor(Cursor.NW_RESIZE);
					b = true;
				} else if ((width - x) * (width - x) < border * border && (0 - y) * (0 - y) < border * border) {
					JavaFxMain.instance.scene_create.setCursor(Cursor.NE_RESIZE);
					b = true;
				} else if ((0 - x) * (0 - x) < border * border && (height - y) * (height - y) < border * border) {
					JavaFxMain.instance.scene_create.setCursor(Cursor.SW_RESIZE);
					b = true;
				} else if ((width - x) * (width - x) < border * border
						&& (height - y) * (height - y) < border * border) {
					JavaFxMain.instance.scene_create.setCursor(Cursor.SE_RESIZE);
					b = true;
				} else {
					JavaFxMain.instance.scene_create.setCursor(Cursor.DEFAULT);
				}
				if (b) {
					CreateGuiController.instance.nowControl = this.motherChoiceSet;
					width_before = this.motherChoiceSet.getWidth();
					height_before = this.motherChoiceSet.getHeight();
					x_before = this.motherChoiceSet.posx;
					y_before = this.motherChoiceSet.posy;
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
						moveCommand = new MoveCommand(dataSet.posx, dataSet.posy, dataSet);
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
					if (moveCommand.start_x != dataSet.posx || moveCommand.start_y != dataSet.posy) {
						var v = moveCommand.checkOutline(this.motherChoiceSet, dataSet.posx, dataSet.posy);
						dataSet.posx = v.x;
						dataSet.posy = v.y;

						moveCommand.setEnd(v.x, v.y);
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
					Vector2f v = CreateGuiController.platform.checkLine(dataSet, 10f);
					if (v != null) {
						dataSet.posx = v.x == 0 ? dataSet.posx : v.x;
						dataSet.posy = v.y == 0 ? dataSet.posy : v.y;
					}
					if (final_choice != null) {
						CreateGuiController.instance.commandTimeline
								.excuteCommand(new CombineCommand(final_choice, dataSet));
					}
				}
				this.pane.setViewOrder(0.0d);
			});
			pane.setOnMouseEntered(e -> {
				CreateGuiController.instance.nowMouseInDataSet = dataSet;
			});
			
			pane.setOnMouseExited(e -> {
				if (!motherChoiceSet.isClicked)
					JavaFxMain.instance.scene_create.setCursor(Cursor.DEFAULT);
			});
		}

		updateColor();
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
		if (motherChoiceSet.equals(CreateGuiController.instance.nowMouseInDataSet)) {
			gc.setStroke(Color.BLUE);
			gc.setLineWidth(1);
			gc.setLineDashes(5);
			gc.setLineDashOffset((time * 20) % 1000);
			var gap = 4;
			var x1 = motherChoiceSet.posx - gap - JavaFxMain.controller.getPlatform().local_x;
			var x2 = motherChoiceSet.posx + motherChoiceSet.getWidth() + gap
					- JavaFxMain.controller.getPlatform().local_x;
			var y1 = motherChoiceSet.posy - gap - JavaFxMain.controller.getPlatform().local_y;
			var y2 = motherChoiceSet.posy + motherChoiceSet.getHeight() + gap
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

	public void combineSubChoiceSetComponenet(ChoiceSet sub) {
		if (sub.choiceSet_parent != null) {
			sub.choiceSet_parent.getAnchorPane().getChildren().remove(sub.getAnchorPane());
		}
		hbox.getChildren().add(sub.getAnchorPane());
		area.setPrefWidth(pane.getWidth());
	}

	public void seperateSubChoiceSetComponenet(ChoiceSet sub) {
		hbox.getChildren().remove(sub.getAnchorPane());
		CreateGuiController.instance.pane_position.getChildren().add(sub.getAnchorPane());
		area.setPrefWidth(pane.getWidth());

	}
}
