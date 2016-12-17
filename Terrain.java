package Indy;

import java.util.concurrent.ThreadLocalRandom;
import javafx.collections.FXCollections;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;

public class Terrain {
	private ObservableFloatArray _points;
	private ObservableFloatArray _texcoords;
	private ObservableIntegerArray _faces;
	private TriangleMesh _terrainMesh;
	private MeshView _terrainView;
	private float[][] _heightmap;
	private int _size;
	private float _scale;

	/*
	 * Terrain class handles creating the terrain mesh. Generates a heightmap
	 * with diamond square, turns it into a trianglemesh, and textures it
	 * according to the biome setting.
	 */

	public Terrain(int size, int detail, float scale, float smooth, int biome) {
		_size = size;
		_scale = scale;
		// collection of texcoords
		_texcoords = FXCollections.observableFloatArray();
		// collection of points
		_points = FXCollections.observableFloatArray();
		// collection of faces
		_faces = FXCollections.observableIntegerArray();
		DiamondSquare diamond = new DiamondSquare(_size, detail, _scale, smooth);
		_heightmap = diamond.generate();
		_terrainMesh = this.createMesh(_size, _heightmap);
		this.setTexture(biome);
	}

	// This is for creating a Triangle Mesh from the heightmap
	// It also creates texture coordinates dynamically while creating the mesh
	private TriangleMesh createMesh(int size, float[][] heightmap) {
		float sizef = (float) size;
		// vMark keeps track of which vertex we're working on
		Integer[][] vMark = new Integer[size][size];

		int vertexCounter = 0;
		for (int x = 0; x < size; x++) {
			for (int z = 0; z < size; z++) {
				float tempX = x;
				float tempY = heightmap[x][z];
				float tempZ = z;
				if (z + 1 < size && x + 1 < size) {
					Integer vCurrent = vMark[x][z];
					Integer vDown = vMark[x][z + 1];
					Integer vRight = vMark[x + 1][z];
					// When it reaches a new vertex point, add the temp values
					if (vCurrent == null) {
						_points.addAll(tempX, tempY, tempZ);
						// add a new tex coord that is the x and z values
						// scaled to percent of 100
						_texcoords.addAll(tempX / sizef, tempZ / sizef);
						vMark[x][z] = vertexCounter++;
						vCurrent = vMark[x][z];
					}
					// Whether the triangle it is a component of has all
					// vertices
					if (vDown == null) {
						// The point above
						_points.addAll(tempX);
						_points.addAll(heightmap[x][z + 1]);
						_points.addAll(tempZ);
						_texcoords.addAll(tempX / sizef, tempZ / sizef);
						vMark[x][z + 1] = vertexCounter++;
						vDown = vMark[x][z + 1];
					}
					if (vRight == null) {
						// The point to the right
						_points.addAll(tempX);
						_points.addAll(heightmap[x + 1][z]);
						_points.addAll(tempZ);
						_texcoords.addAll(tempX / sizef, tempZ / sizef);
						vMark[x + 1][z] = vertexCounter++;
						vRight = vMark[x + 1][z];
					}
					// 1,1 2,2 3,3
					_faces.addAll(vCurrent, vCurrent, vDown, vDown, vRight, vRight);
				}
				if (z - 1 >= 0 && x - 1 >= 0) {
					Integer vCurrent = vMark[x][z];
					Integer vUp = vMark[x][z - 1];
					Integer vLeft = vMark[x - 1][z];
					if (vCurrent == null) {
						_points.addAll(tempX, tempY, tempZ);
						_texcoords.addAll(tempX / sizef, tempZ / sizef);
						vMark[x][z] = vertexCounter++;
						vCurrent = vMark[x][z];
					}
					if (vUp == null) {
						_points.addAll(tempX);
						_points.addAll(heightmap[x - 1][z]);
						_points.addAll(tempZ);
						_texcoords.addAll(tempX / sizef, tempZ / sizef);
						vMark[x][z - 1] = vertexCounter++;
						vUp = vMark[x][z - 1];
					}
					if (vLeft == null) {
						_points.addAll(tempX);
						_points.addAll(heightmap[x][z - 1]);
						_points.addAll(tempZ);
						_texcoords.addAll(tempX / sizef, tempZ / sizef);
						vMark[x - 1][z] = vertexCounter++;
					}
					_faces.addAll(vCurrent, vCurrent, vUp, vUp, vLeft, vLeft);
				}
			}

		}
		TriangleMesh mesh = new TriangleMesh();
		mesh.getTexCoords().addAll(_texcoords);
		mesh.getPoints().addAll(_points);
		mesh.getFaces().addAll(_faces);

		return mesh;
	}

	/* For retreiving the meshview in game */
	public MeshView getView() {
		return _terrainView;
	}

	public float[][] getMap() {
		return _heightmap;
	}

	/* These two are used to scale the color gradient to the heightmap */
	private float getMax() {
		float maxValue = 0;
		for (int x = 0; x < _size; x++) {
			for (int y = 0; y < _size; y++) {
				if (_heightmap[x][y] > maxValue) {
					maxValue = _heightmap[x][y];
				}
			}
		}
		return maxValue;
	}

	private float getMin() {
		float minValue = 0;
		for (int x = 0; x < _size; x++) {
			for (int y = 0; y < _size; y++) {
				if (_heightmap[x][y] < minValue) {
					minValue = _heightmap[x][y];
				}
			}
		}
		return minValue;
	}

	/* Used to calculate the scale for smooth color transitions */
	private double scale(final double valueIn, final double baseMin, final double baseMax, final double limitMin,
			final double limitMax) {
		return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
	}

	/*
	 * This method uses the heightmap float[][] to generate a texture which is
	 * then wrapped over the terrain. It takes a biome integer so that different
	 * landscapes have different textures.
	 */
	private void setTexture(int biome) {
		float max = getMax();
		float min = getMin();
		WritableImage texImage = new WritableImage(_size, _size);
		PixelWriter pixelWriter = texImage.getPixelWriter();
		PixelReader pixel = texImage.getPixelReader();
		if (biome == 0) {
			// Alpine
			Image colors = new Image("Indy/alpine.jpg");
			pixel = colors.getPixelReader();
		} else if (biome == 1) {
			// Desert
			Image colors = new Image("Indy/desert.jpg");
			pixel = colors.getPixelReader();
		} else if (biome == 2) {
			// Plains
			Image colors = new Image("Indy/plains.jpg");
			pixel = colors.getPixelReader();
		} else if (biome == 3) {
			// Island
			Image colors = new Image("Indy/island.jpg");
			pixel = colors.getPixelReader();
		} else if (biome == 4) {
			// Arctic
			Image colors = new Image("Indy/arctic.jpg");
			pixel = colors.getPixelReader();
		} else if (biome == 5) {
			// Himalaya
			Image colors = new Image("Indy/himalaya.jpg");
			pixel = colors.getPixelReader();
		}

		for (int x = 0; x < _size; x++) {
			for (int y = 0; y < _size; y++) {
				float value = _heightmap[x][y];
				int rgb = (int) scale(value, min, max, 1, 254);
				Color color = pixel.getColor(1, rgb);
				pixelWriter.setColor(x, y, color);
			}
		}
		_terrainView = new MeshView(_terrainMesh);
		Image tex = texImage;
		PhongMaterial texture = new PhongMaterial();
		texture.setDiffuseMap(tex);
		_terrainView.setMaterial(texture);

	}

	/*
	 * This method uses a diamondsquare algorithm to generate positions for an
	 * object (in this case, trees) which are then placed on the terrain
	 * accordingly.
	 */
	public Group populateDiamond() {
		Tree tree = new Tree();
		Group objects = new Group();
		DiamondSquare populate = new DiamondSquare(_size, 4, 1, 1);
		float[][] placemap = populate.generate();
		for (int x = 5; x < _size - 5; x += 1) {
			for (int y = 5; y < _size - 5; y += 1) {
				float placeValue = placemap[x][y];
				Point3D terrainValue = new Point3D(x, y, (_heightmap[x][y] - 4));
				if (placeValue > .9 && terrainValue.getZ() < 0 && terrainValue.getZ() > -_scale) {
					// Right now specific to trees
					ImageView treeview = tree.getView();
					double rotate = ThreadLocalRandom.current().nextDouble(-8, 8);
					treeview.getTransforms().add(new Rotate(rotate, Rotate.Y_AXIS));
					tree.Place(treeview, terrainValue);
					objects.getChildren().add(treeview);
				}
			}
		}
		return objects;
	}
}
