package edu.indy;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

public class Clouds {
	private int _size;
	private WritableImage _clouds;
	private ImageView _cloudsView;
	private ImageView _cloudsView2;

	public Clouds(int size) {
		_size = size;
		_clouds = new WritableImage(_size, _size);
		_cloudsView = new ImageView();
		_cloudsView2 = new ImageView();
		this.generate();
	}

	public void generate() {
		float[][] transparencyArray = DiamondSquare.generate(_size, 4, 1, 3);
		PixelWriter pixelWriter = _clouds.getPixelWriter();
		float lastvalue = 0;
		for (int x = 0; x < _size; x++) {
			for (int y = 0; y < _size; y++) {
				// The color of each pixel is white, with a transparency value
				// generated from the diamondsquare
				float value = transparencyArray[x][y];
				double alpha = (float) (2 % value) * .5;
				if (value > lastvalue) {
					lastvalue = value;
				}
				Color color = Color.rgb(255, 255, 255, alpha);
				pixelWriter.setColor(x, y, color);
			}
		}
	}

	public Group cloudSetUp() {
		Group cloudGroup = new Group();
		Rectangle2D viewport = new Rectangle2D(0, 0, _size, _size);
		// First Tile
		_cloudsView.setImage(_clouds);
		_cloudsView.setViewport(viewport);
		_cloudsView.getTransforms().add(new Rotate(-90, Rotate.X_AXIS));
		_cloudsView.setTranslateY(-50);
		_cloudsView.setTranslateZ(_size);
		// Stretch transform so the clouds bleed over the terrain edge a bit
		_cloudsView.getTransforms().add(new Scale(1, 1.3));
		// Second Tile
		_cloudsView2.setImage(_clouds);
		_cloudsView2.setViewport(viewport);
		_cloudsView2.getTransforms().add(new Rotate(-90, Rotate.X_AXIS));
		_cloudsView2.setTranslateY(-50);
		_cloudsView2.setTranslateZ(_size);
		_cloudsView2.getTransforms().add(new Rotate(-180, Rotate.Y_AXIS));
		_cloudsView2.getTransforms().add(new Scale(1, 1.3));
		cloudGroup.getChildren().addAll(_cloudsView, _cloudsView2);
		return cloudGroup;
	}
	
	public void setOpacity(double alpha){
		_cloudsView.setOpacity(alpha);
		_cloudsView2.setOpacity(alpha);
	}

	/* could be a scroll method on a cloud holding class */
	public void scrollClouds() {
		_cloudsView.setX(_cloudsView.getX() + .001);
		_cloudsView2.setX(_cloudsView2.getX() - .001);
		if (_cloudsView.getX() >= _size) {
			_cloudsView.setX(-_size);
		}
		if (_cloudsView2.getX() <= -_size * 2) {
			_cloudsView2.setX(0);
		}
	}

}