package cyoap_main.design;

import java.io.IOException;

import org.fxmisc.richtext.InlineCssTextArea;

import cyoap_main.command.CombineCommand;
import cyoap_main.command.MoveCommand;
import cyoap_main.design.controller.createGui.CreateGuiController;
import cyoap_main.unit.Bound2f;
import cyoap_main.unit.Vector2f;
import cyoap_main.util.LoadUtil;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

public class ChoiceSetGuiComponent {
	public BorderPane pane = new BorderPane();
	public BorderPane pane_border = new BorderPane();
	public Pane pane_middle = new Pane();
	public HBox hbox = new HBox();
	public ImageView image = new ImageView();
	public InlineCssTextArea area = new InlineCssTextArea();
	public Text title = new Text();

	public ChoiceSet motherChoiceSet;

	public Color color;

	public static Border border_default = new Border(new BorderStroke(Color.BLACK,
			new BorderStrokeStyle(StrokeType.OUTSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10, 0, null),
			new CornerRadii(2), new BorderWidths(1)));

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
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		pane.setLayoutX(dataSet.posx);
		pane.setLayoutY(dataSet.posy);
		pane.setBorder(border_default);
		pane.setTop(pane_border);
		pane.setCenter(pane_middle);
		pane.setBottom(hbox);
		pane.setId("pane_choiceset");

		pane_middle.setPrefHeight(20);

		pane_border.setTop(title);
		pane_border.setCenter(image);
		pane_border.setBottom(area);
		pane_border.setBorder(new Border(new BorderStroke(null, null, Color.BLACK, null, null, null,
				BorderStrokeStyle.DASHED, null, new CornerRadii(2), new BorderWidths(2), null)));

		area.setEditable(false);

		pane_border.setPrefWidth(200);
		pane_border.setPrefHeight(100);
		area.setPrefHeight(100);
		image.setPreserveRatio(true);
		image.setFitWidth(200);

		pane_border.setMouseTransparent(true);

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
				double movex = CreateGuiController.instance.sensitivity
						* (e.getSceneX() - CreateGuiController.platform.start_x);
				double movey = CreateGuiController.instance.sensitivity
						* (e.getSceneY() - CreateGuiController.platform.start_y);
				CreateGuiController.platform.start_x = e.getSceneX();
				CreateGuiController.platform.start_y = e.getSceneY();
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
					CreateGuiController.instance.commandTimeline.excuteCommand(new CombineCommand(final_choice, dataSet));
				}
			}
			this.pane.setViewOrder(0.0d);
		});
		pane.setOnMouseEntered(e -> {
			CreateGuiController.instance.nowMouseInDataSet = dataSet;
		});

		updateColor();
	}

	public MoveCommand moveCommand = null;

	public void update() {
		area.clear();
		title.setText(motherChoiceSet.string_title);
		updateColor();
		if (motherChoiceSet.string_image_name != null && !motherChoiceSet.string_image_name.isEmpty()) {
			var simpleEntry = LoadUtil.loadImage(motherChoiceSet.string_image_name);
			image.setImage(simpleEntry.getKey());
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
