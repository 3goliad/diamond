package Indy;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class Water {
	private Box _water;

	/*
	 * A simple class. Just creates a thin box that as tall and wide as the map.
	 * Makes it blue and transparent. The constructor takes a width parameter so
	 * that the water is the size of the map, and a waterline parameter so the
	 * waterline can be adjusted on different biomes.
	 */
	public Water(int width, float waterline) {
		_water = new Box(width, 1, width);
		PhongMaterial mat = new PhongMaterial(Color.DODGERBLUE);
		mat.setDiffuseColor(Color.rgb(0, 0, 200, .4));
		_water.setMaterial(mat);
		_water.setTranslateY(waterline);
		_water.setTranslateZ(width / 2);
		_water.setTranslateX(width / 2);

	}

	/* Returns the water box */
	public Box getWater() {
		return _water;
	}

}
