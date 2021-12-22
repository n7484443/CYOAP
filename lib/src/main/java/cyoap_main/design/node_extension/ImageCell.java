package cyoap_main.design.node_extension;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

public class ImageCell extends Region {
	private final ImageView imageView;
	public boolean isPreserveRatio = true;
	public float pos_x = 0;
	public float pos_y = 0;
	public ReadOnlyDoubleWrapper width_real = new ReadOnlyDoubleWrapper();
	public ReadOnlyDoubleWrapper height_real = new ReadOnlyDoubleWrapper();
	public DoubleProperty min_real = new SimpleDoubleProperty();
	public IntegerProperty round = new SimpleIntegerProperty();
	public Rectangle rectangle;

	public ImageCell(Image image) {
		imageView = new ImageView(image);
		imageView.setSmooth(true);
		getChildren().add(imageView);
	}

	public ImageCell() {
		this(null);
	}

	public final void setImage(Image value) {
		imageView.setImage(value);
	}

	public final Image getImage() {
		return imageView.getImage();
	}

	public void setCut(int r) {
		round.set(r);
		rectangle = new Rectangle(getRealWidth(), getRealHeight());
		rectangle.widthProperty().bind(width_real);
		rectangle.heightProperty().bind(height_real);
		rectangle.xProperty().bind(widthProperty().subtract(width_real).divide(2));
		rectangle.yProperty().bind(heightProperty().subtract(height_real).divide(2));
		min_real.bind(Bindings.min(width_real, height_real));
		rectangle.arcWidthProperty().bind(min_real.divide(100).multiply(round));
		rectangle.arcHeightProperty().bind(min_real.divide(100).multiply(round));
		setClip(rectangle);
	}

	public double getRealWidth() {
		return width_real.get();
	}

	public double getRealHeight() {
		return height_real.get();
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		Insets insets = getInsets();
		double x = insets.getLeft();
		double y = insets.getTop();
		double width = getWidth() - x - insets.getRight();
		double height = getHeight() - y - insets.getBottom();

		Image image = getImage();
		double imageWidth = 0;
		double imageHeight = 0;
		if (image != null) {
			imageWidth = image.getWidth();
			imageHeight = image.getHeight();
		}
		if(isPreserveRatio){
			// scale ImageView to available size
			double factor = Math.min(width / imageWidth, height / imageHeight);
			if (Double.isFinite(factor) && factor > 0) {
				imageView.setFitHeight(factor * imageHeight);
				imageView.setFitWidth(factor * imageWidth);
				this.width_real.set(factor * imageWidth);
				this.height_real.set(factor * imageHeight);
				imageView.setVisible(true);
			} else {
				imageView.setVisible(false);
			}
		}else{
			imageView.setFitHeight(height);
			imageView.setFitWidth(width);
			imageView.setVisible(true);
		}

		// center ImageView in available area
		layoutInArea(imageView, x, y, width, height, 0, HPos.CENTER, VPos.CENTER);
	}
}
