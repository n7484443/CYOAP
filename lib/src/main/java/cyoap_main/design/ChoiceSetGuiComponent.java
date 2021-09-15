package cyoap_main.design;

import cyoap_main.command.CombineCommand;
import cyoap_main.command.MoveCommand;
import cyoap_main.design.controller.MakeGUIController;
import cyoap_main.unit.Bound2f;
import cyoap_main.unit.Vector2f;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ChoiceSetGuiComponent {
	public BorderPane pane = new BorderPane();
	public BorderPane border_pane = new BorderPane();
	public BorderPane middle_pane = new BorderPane();
	public HBox hbox = new HBox();
	public ImageView image = new ImageView();
	public TextArea area = new TextArea();
	public Text title = new Text();

	public ChoiceSet motherChoiceSet;

	public int color;

	public ChoiceSetGuiComponent() {

	}

	public ChoiceSetGuiComponent(int color) {
		this.color = color;
	}

	public void setUp(ChoiceSet dataSet) {
		motherChoiceSet = dataSet;
		pane.setLayoutX(dataSet.posx);
		pane.setLayoutY(dataSet.posy);
		pane.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2), BorderWidths.DEFAULT)));
		pane.setTop(border_pane);
		pane.setCenter(middle_pane);
		pane.setBottom(hbox);

		middle_pane.setPrefHeight(20);

		border_pane.setTop(title);
		border_pane.setCenter(image);
		border_pane.setBottom(area);
		border_pane.setBorder(new Border(new BorderStroke(null, null, Color.BLACK, null, null, null,
				BorderStrokeStyle.DASHED, null, new CornerRadii(2), new BorderWidths(2), null)));

		area.setEditable(false);

		border_pane.setPrefWidth(200);
		border_pane.setPrefHeight(100);
		area.setPrefHeight(100);
		image.setPreserveRatio(true);
		image.setFitWidth(200);

		border_pane.setMouseTransparent(true);

		pane.setOnMouseClicked(e -> {
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				if (e.getClickCount() == 2) {
					MakeGUIController.instance.nowEditDataSet = dataSet;
					MakeGUIController.instance.loadFromDataSet(dataSet);
					MakeGUIController.instance.changeTab(MakeGUIController.instance.tab_describe);
					e.consume();
				}
			}
		});
		pane.setOnMouseDragged(e -> {
			if (e.getButton().equals(MouseButton.MIDDLE)) {
				if (moveCommand == null) {
					moveCommand = new MoveCommand(dataSet.posx, dataSet.posy, dataSet);
				}
				double movex = MakeGUIController.instance.sensitivity
						* (e.getSceneX() - MakeGUIController.platform.start_x);
				double movey = MakeGUIController.instance.sensitivity
						* (e.getSceneY() - MakeGUIController.platform.start_y);
				MakeGUIController.platform.start_x = e.getSceneX();
				MakeGUIController.platform.start_y = e.getSceneY();
				dataSet.updatePosition(movex, movey);
			}
		});
		pane.setOnMouseReleased(e -> {
			if (e.getButton().equals(MouseButton.MIDDLE)) {
				if (moveCommand.start_x != dataSet.posx || moveCommand.start_y != dataSet.posy) {
					var v = moveCommand.checkOutline(this.motherChoiceSet, dataSet.posx, dataSet.posy);
					dataSet.posx = v.x;
					dataSet.posy = v.y;
					
					moveCommand.setEnd(v.x, v.y);
					MakeGUIController.instance.addCommand(moveCommand);
				}
				moveCommand = null;

				ChoiceSet final_choice = null;
				Bound2f bound = new Bound2f(dataSet.bound);
				float mul = 0.8f;
				bound.x += bound.width * (1f - mul) * 0.5f;
				bound.y += bound.height * (1f - mul) * 0.5f;
				bound.width *= mul;
				bound.height *= mul;
				for (var choiceSet : MakeGUIController.platform.choiceSetList) {
					if (choiceSet == dataSet)
						continue;
					if (bound.intersect(choiceSet.bound)) {
						final_choice = choiceSet;
						break;
					}
				}
				Vector2f v = MakeGUIController.platform.checkLine(dataSet, 10f);
				if (v != null) {
					dataSet.posx = v.x == 0 ? dataSet.posx : v.x;
					dataSet.posy = v.y == 0 ? dataSet.posy : v.y;
				}
				if (final_choice != null) {
					MakeGUIController.instance.excuteCommand(new CombineCommand(final_choice, dataSet));
				}
			}
		});
		pane.setOnMouseEntered(e -> {
			MakeGUIController.instance.nowMouseInDataSet = dataSet;
		});

		pane.setStyle("-fx-background-color: #" + Integer.toHexString(color));
	}

	public MoveCommand moveCommand = null;

	public void update() {
		area.setText(motherChoiceSet.string_describe);
		title.setText(motherChoiceSet.string_title);
		pane.setStyle("-fx-background-color: #" + Integer.toHexString(color));
		if (motherChoiceSet.string_image_name != null && !motherChoiceSet.string_image_name.isEmpty())
			image.setImage(new Image(motherChoiceSet.string_image_name));
	}

	public void updatePos(double moveX, double moveY) {
		pane.relocate(moveX, moveY);
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
		MakeGUIController.instance.pane_position.getChildren().add(sub.getAnchorPane());
		area.setPrefWidth(pane.getWidth());

	}
}
