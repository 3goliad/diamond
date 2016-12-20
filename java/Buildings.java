package Indy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

public class Buildings {
	/*
	 * This class is used to generate the towns on the terrain. It uses a custom
	 * algorithm to determine tiles on the map most suitable for towns. It then
	 * generates them on those tiles.
	 */
	private float[][] _heightmap;
	private int _size;
	private int _s;
	private float _waterline;
	private PriorityQueue<Point3D> _sortedCorners;
	private Image _buildingmap;

	public Buildings(float[][] heightmap, int size, float waterline) {
		_buildingmap = new Image("/Indy/buildingmap.jpg");
		_waterline = waterline;
		_heightmap = heightmap;
		_size = size;
		_s = _size / Constants.GRID_SQUARES;
		this.createRegions();
	}

	/*
	 * This divides the heightmap float array into tiles, and cycles through
	 * them to determine the best candidates (those which are the flattest and
	 * closest to water). It uses a priority queue and comparator to create a
	 * list of towns according to favorability.
	 */
	public void createRegions() {
		// Using arraylist to keep track of each square.
		// Point3D in order to store the deviation of the region in the Z value
		ArrayList<Point3D> corners = new ArrayList<Point3D>();
		// Recording the lower right corner of each square
		// Starting at S because we don't need the upper/right edges
		for (int x = _s; x <= _size; x += _s) {
			for (int y = _s; y <= _size; y += _s) {
				Point3D point = new Point3D(x, y, 0);
				corners.add(point);
			}
		}
		// Now go through each square and find the best candidates
		for (int i = 0; i < corners.size(); i++) {
			Point3D point = corners.get(i);
			float avg = 0;
			int counter = 0;
			float high = _heightmap[(int) point.getX()][(int) point.getY()];
			float low = _heightmap[(int) point.getX()][(int) point.getY()];
			for (int x = ((int) point.getX()) - _s; x < point.getX(); x++) {
				for (int y = ((int) point.getY()) - _s; y < point.getY(); y++) {
					// find the highest and lowest values
					float height = _heightmap[x][y];
					if (height > high) {
						high = height;
					} else if (height < low) {
						low = height;
					}
					// add it to average
					avg = avg + height;
					counter++;
				}

			}
			avg = avg / (counter);
			// weigh according to mean elevation
			double difference = (high - low) - avg;
			// prevent underwater towns by weighing against tiles with an
			// average height
			// below the waterline
			if (avg < _waterline) {
				difference = difference * 4;
			}

			Point3D newpoint = new Point3D(point.getX(), point.getY(), difference);
			corners.set(i, newpoint);

		}
		// Now we transfer the arraylist to a priority queue
		PriorityQueue<Point3D> sortedCorners = new PriorityQueue<Point3D>(corners.size(), Comparator.comparingDouble(Point3D::getZ).reversed());
		for (Point3D point : corners) {
			sortedCorners.add(point);
		}
		// Remove everything but the lowest fifth using the Comparator
		for (int i = 0; i < (int) corners.size() * .85; i++) {
			sortedCorners.remove();
		}
		//
		_sortedCorners = sortedCorners;
	}

	/* This plants a town at the middle of the selected tiles. */
	public Group spawnBuildings() {
		Group towns = new Group();
		for (Point3D point : _sortedCorners) {

			double x = point.getX();
			double y = point.getY();
			x = x - (_s / 2);
			y = y - (_s / 2);

			Group town = spawnTown(x, y);
			towns.getChildren().add(town);
		}
		return towns;

	}

	/*
	 * This creates a randomized grid of buildings at the town location.
	 */
	private Group spawnTown(double x, double y) {
		// Original x location
		double og = x;
		// Group for scene graph
		Group town = new Group();

		int townsize = ThreadLocalRandom.current().nextInt(2, 20);
		for (int i = 0; i < townsize; i++) {
			float elevation = _heightmap[(int) x][(int) y];
			if ((elevation < _waterline - 2) && (y < _size) && (x > 0)) {
				// Jitter variables
				double j = ThreadLocalRandom.current().nextDouble(0, 2);
				double w = ThreadLocalRandom.current().nextDouble(0, 2);
				double z = ThreadLocalRandom.current().nextDouble(0, 2);
				int rotate = ThreadLocalRandom.current().nextInt(-1, 1);
				Box building = new Box(1 + j, 1 + z, 1 + w);
				PhongMaterial buildingColor = new PhongMaterial(Color.rgb(100 + 50 * rotate, 100, 100 - 50 * rotate));
				buildingColor.setSelfIlluminationMap(_buildingmap);
				building.setMaterial(buildingColor);
				building.setTranslateX(x);
				building.setTranslateZ(y);
				building.setTranslateY(elevation - 1);
				building.getTransforms().add(new Rotate(180 * rotate, Rotate.Y_AXIS));
				town.getChildren().add(building);
				x = x + 2 + j;
				if (x > og + (townsize * .5 / j)) {
					x = og + w;
					y = y + 2 + z;
				}
			}
		}
		return town;
	}

}
