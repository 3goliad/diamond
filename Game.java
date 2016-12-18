package Indy;

import java.util.concurrent.ThreadLocalRandom;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.AmbientLight;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;

public class Game {

	private BorderPane _game;
	private Camera _camera;
	private Group _root3d;
	private int _size, _detail, _biome;
	private Clouds _clouds;
	private float _scale, _smooth, _waterline;
	private double _cameraXVelocity, _cameraYVelocity, _cameraZVelocity;
	private MeshView _terrainView;
	private Point3D _mousePoint;
	private Group _explosions;
	private Group _towns;
	private Group _trees;
	private Image _boomMap;
	private Image _burn;

	/*
	 * Game class is tasked with controlling the core mechanics of the program
	 * and instantiating other classes. Handles controls, interaction
	 * (explosions and exploding other objects), setting up lighting, and
	 * setting up the landscape.
	 */
	public Game(int terrainValue, double size, int time) {
		_game = new BorderPane();
		// controls the terrain dimensions
		_size = (int) size;
		// terrain value determines the type of terrain by setting
		// parameters in the diamondsquare, water, and terrain texture
		if (terrainValue == 0) {
			// Alpine
			_detail = 6;
			_scale = 30;
			_smooth = 2;
			_waterline = _scale;
			_biome = 0;
		} else if (terrainValue == 1) {
			// Desert
			_detail = 6;
			_scale = 15;
			_smooth = 3;
			_waterline = _scale + 10;
			_biome = 1;
		} else if (terrainValue == 2) {
			// Plains
			_detail = 6;
			_scale = 5;
			_smooth = 3;
			_waterline = _scale;
			_biome = 2;
		} else if (terrainValue == 3) {
			// Islands
			_detail = 6;
			_scale = 50;
			_smooth = 3;
			_waterline = _scale - 35;
			_biome = 3;
		} else if (terrainValue == 4) {
			// Arctic
			_detail = 6;
			_scale = 20;
			_smooth = 2;
			_waterline = _scale;
			_biome = 4;
		} else if (terrainValue == 5) {
			// Himalaya
			_detail = 6;
			_scale = 80;
			_smooth = 2;
			_waterline = _scale + 20;
			_biome = 5;
		}
		// Camera uses velocity for smooth movement
		_cameraXVelocity = 0;
		_cameraYVelocity = 0;
		_cameraZVelocity = 0;
		// Set up camera
		this.cameraSetUp();
		// Add it to scene graph
		_root3d = new Group(_camera);
		// Setting up instance variables for groups
		_explosions = new Group();
		_towns = new Group();
		_trees = new Group();
		// These are instance variables so they don't have to be loaded multiple
		// times
		_boomMap = new Image("/Indy/Boom.jpg");
		_burn = new Image("/Indy/burnttree.png");
		_terrainView = new MeshView();
		// Creates a terrain object, adds buildings and water, and then adds it to
		// the scene graph.
		// Terrain
		Terrain terrain = new Terrain(_size, _detail, _scale, _smooth, _biome);
		_terrainView = terrain.getView();
		_terrainView.setDrawMode(DrawMode.FILL);
		_terrainView.setCullFace(CullFace.FRONT);
		Buildings buildings = new Buildings(terrain.getMap(), _size, _waterline);
		_towns = buildings.spawnBuildings();
		// water
		Water water = new Water(_size, _waterline);
		// Trees
		_trees = terrain.populateDiamond();
		_root3d.getChildren().addAll(_terrainView, _towns, water.getWater(), _trees);
		// create clouds, gives them their own group so
		// they don't interfere with explosions in the scene graph.
		_clouds = new Clouds(_size);
		Group cloudGroup = new Group(_camera);
		cloudGroup.getChildren().add(_clouds.cloudSetUp());
		cloudGroup.setMouseTransparent(true);
		_root3d.getChildren().addAll(_explosions, cloudGroup);
		// Lighting set up
		this.lightSetUp(time);
		// Create a subScene for 3D rendering
		SubScene subScene = new SubScene(_root3d, Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT, true,
				SceneAntialiasing.BALANCED);
		subScene.setCamera(_camera);
		subScene.setFill(Color.BLACK);
		_game.setCenter(subScene);
		// Key/Mouse Input
		_mousePoint = new Point3D(0, 0, 0);
		// Key Controls. For smooth motion, tracks both key press and key
		// release.
		_game.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
			KeyCode code = e.getCode();
			if (code == KeyCode.UP || code == KeyCode.DOWN) {
				_cameraZVelocity = (code == KeyCode.UP ? Constants.CAMERA_SPEED : -Constants.CAMERA_SPEED);
				this.bounceCamera();
			} else if (code == KeyCode.LEFT || code == KeyCode.RIGHT) {
				_cameraXVelocity = (code == KeyCode.RIGHT ? Constants.CAMERA_SPEED : -Constants.CAMERA_SPEED);
				this.bounceCamera();
			} else if (code == KeyCode.Q || code == KeyCode.A) {
				_cameraYVelocity = (code == KeyCode.Q ? Constants.CAMERA_SPEED : -Constants.CAMERA_SPEED);
				this.bounceCamera();
			}
			e.consume();
		});
		_game.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent e) -> {
			KeyCode code = e.getCode();
			if (code == KeyCode.UP || code == KeyCode.DOWN) {
				_cameraZVelocity = 0;
			} else if (code == KeyCode.LEFT || code == KeyCode.RIGHT) {
				_cameraXVelocity = 0;
			} else if (code == KeyCode.Q || code == KeyCode.A) {
				_cameraYVelocity = 0;
			}
			e.consume();
		});
		// If a mouse click is detected on the terrain meshview,
		// Trigger an explosion on that point.
		_terrainView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				PickResult pr = mouseEvent.getPickResult();
				System.out.println(pr.getIntersectedPoint());
				System.out.println(pr.getIntersectedNode());
				_mousePoint = pr.getIntersectedPoint();
				Explode(_mousePoint);
			}
		});
		_game.requestFocus();
		_game.setFocusTraversable(true);
		this.setupTimeline();
	}

	public Pane getRoot() {
		return _game;
	}

	/* Sets up camera */
	private void cameraSetUp() {
		_camera = new PerspectiveCamera(true);
		_camera.setNearClip(.1);
		_camera.setFarClip(1000000);
		_camera.getTransforms().add(new Rotate(-70, Rotate.X_AXIS));
		_camera.setTranslateY(-100);
		_camera.setTranslateX(50);
		_camera.setTranslateZ(0);
	}

	/* Sets up lighting according to time of day */
	private void lightSetUp(int time) {
		// Lighting set up
		if (time == 0) {
			// Day
			AmbientLight light = new AmbientLight();
			light.setColor(Color.rgb(80, 80, 100, .1));
			PointLight light2 = new PointLight();
			light2.setColor(Color.BEIGE);
			light2.setTranslateY(200);
			light2.setTranslateX(_size / 2);
			light2.setTranslateZ(_size / 2);
			_root3d.getChildren().addAll(light, light2);
			_clouds.setOpacity(.5);
		} else if (time == 1) {
			// Night
			AmbientLight light = new AmbientLight();
			light.setColor(Color.rgb(20, 20, 20, .1));
			PointLight light2 = new PointLight();
			light2.setColor(Color.rgb(40, 40, 55, .1));
			light2.setTranslateY(200);
			light2.setTranslateX(_size / 2);
			light2.setTranslateZ(_size / 2);
			_root3d.getChildren().addAll(light, light2);
			_clouds.setOpacity(.3);
		} else {
			// Sunset
			AmbientLight light = new AmbientLight();
			light.setColor(Color.rgb(60, 60, 132, .1));
			PointLight light2 = new PointLight();
			light2.setColor(Color.rgb(255, 160, 0));
			light2.setTranslateY(100);
			light2.setTranslateX(_size / 2);
			light2.setTranslateZ(_size / 2);
			_root3d.getChildren().addAll(light, light2);
			_clouds.setOpacity(.5);
		}
	}

	/* Timeline */
	public void setupTimeline() {
		KeyFrame kf = new KeyFrame(Duration.millis(15), (ActionEvent e) -> {
			_clouds.scrollClouds();
			_camera.setTranslateY(_camera.getTranslateY() + _cameraYVelocity);
			_camera.setTranslateX(_camera.getTranslateX() + _cameraXVelocity);
			_camera.setTranslateZ(_camera.getTranslateZ() + _cameraZVelocity);
			this.bounceCamera();

		});
		Timeline timeline = new Timeline(kf);
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	/*
	 * Method which generates an explosion at a given point. Calls treeburn()
	 * and buildingexplode() at the end to check if the explosion singed any
	 * trees or caused any buildings to explode. Can lead to recursive chain
	 * reactions.
	 */
	private void Explode(Point3D point) {
		// jitter to make the explosions vary more;
		double j = ThreadLocalRandom.current().nextDouble(0, 4);
		Sphere sphere = new Sphere();
		// Sphere starts out very small
		sphere.setRadius(.01);
		PhongMaterial material = new PhongMaterial(Color.ANTIQUEWHITE);
		material.setSelfIlluminationMap(_boomMap);
		material.setDiffuseColor(Color.rgb(255, 255, 255, 1));
		sphere.setMaterial(material);
		sphere.setTranslateX(point.getX());
		sphere.setTranslateY(point.getY());
		sphere.setTranslateZ(point.getZ());
		PointLight light = new PointLight();
		light.setColor(Color.ORANGE);
		light.setTranslateX(point.getX());
		light.setTranslateY(point.getY());
		light.setTranslateZ(point.getZ());
		_explosions.getChildren().addAll(sphere, light);
		// First explosion
		ScaleTransition st = new ScaleTransition(Duration.millis(4000), sphere);
		st.setInterpolator(Interpolator.EASE_IN);
		st.setByX(j * 200f);
		st.setByY(j * 200f);
		st.setByZ(j * 200f);
		st.setDelay(Duration.millis(50 * j));
		// Retraction
		ScaleTransition rt = new ScaleTransition(Duration.millis(500 * j), sphere);
		rt.setInterpolator(Interpolator.EASE_OUT);
		rt.setToX(j * .7f);
		rt.setToY(j * .7f);
		rt.setToZ(j * .7f);
		// Lights
		final Animation lightOn = new Transition() {
			{
				setCycleDuration(Duration.millis(1500));
			}

			protected void interpolate(double frac) {
				int n = (int) (255 * frac);
				light.setColor(Color.rgb(n, n / 2, 0));

			}
		};
		lightOn.setDelay(Duration.millis(500 * j));
		// Dimming Light
		final Animation lightOff = new Transition() {
			{
				setCycleDuration(Duration.millis(500 * j));
			}

			protected void interpolate(double frac) {
				int n = (int) (255 * frac);
				light.setColor(Color.rgb(255 - n, (255 / 2) - n / 2, 0));

			}
		};
		// Making sphere transparent...kind of works
		final Animation SphereOff = new Transition() {
			{
				setCycleDuration(Duration.millis(500 * j));
			}

			protected void interpolate(double frac) {
				double n = 1 * frac;
				int c = (int) frac * 255;
				material.setDiffuseColor(Color.rgb(255 - c, 255 - c, 255 - c, 1 - n));
				sphere.setMaterial(material);
			}
		};
		ParallelTransition boom = new ParallelTransition(st, lightOn);
		ParallelTransition fade = new ParallelTransition(rt, SphereOff, lightOff);
		boom.play();
		boom.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				fade.play();
				buildingExplode(point);
				treeBurn(point);

			}
		});
	}

	/*
	 * this method tests if there are any buildings near the point where an
	 * explosion occurs by looping through the scene graph of towns. if the
	 * explosion was close enough, it triggers another explosion and moves the
	 * building away to prevent infinitely recursing explosions.
	 */
	private void buildingExplode(Point3D boom) {

		for (int i = 0; i < _towns.getChildren().size(); i++) {
			Node town = _towns.getChildren().get(i);
			if (town instanceof Group) {
				for (Node nodeIn : ((Group) town).getChildren()) {
					Point3D point = new Point3D(nodeIn.getTranslateX(), nodeIn.getTranslateY(), nodeIn.getTranslateZ());
					if (point.distance(boom) < 10) {
						nodeIn.setTranslateY(-10000);
						Explode(point);
					}
				}
			}

		}
	}

	/*
	 * similar to the last method, checks if any trees are nearby the explosion
	 * so that it can singe them by replacing them with a different imageview.
	 */
	private void treeBurn(Point3D boom) {
		for (int i = 0; i < _trees.getChildren().size(); i++) {
			Node tree = _trees.getChildren().get(i);
			Point3D point = new Point3D(tree.getTranslateX(), tree.getTranslateY(), tree.getTranslateZ());
			if (point.distance(boom) < 10) {
				ImageView _treeview = new ImageView(_burn);
				Rectangle2D viewport = new Rectangle2D(0, 0, 5, 9);
				_treeview.setScaleX(.25);
				_treeview.setScaleY(.25);
				_treeview.setViewport(viewport);
				_treeview.setTranslateX(tree.getTranslateX());
				_treeview.setTranslateY(tree.getTranslateY());
				_treeview.setTranslateZ(tree.getTranslateZ());
				_explosions.getChildren().add(_treeview);
				tree.setTranslateY(-10000);
			}
		}
	}

	/* Keeps the camera from going outside the terrain bounds */
	private void bounceCamera() {
		// Keep track of height so that the bounding box can adjust accordingly
		double height = _camera.getTranslateY() / 4;
		if (_camera.getTranslateY() < -360) {
			_camera.setTranslateY(-359);
			_cameraYVelocity = 0;
		} else if (_camera.getTranslateX() >= _size + height) {
			_camera.setTranslateX(_size - 1 + height);
		} else if (_camera.getTranslateX() <= 1 - height) {
			_camera.setTranslateX(2 - height);
		} else if (_camera.getTranslateZ() >= _size - 45 + height * 2) {
			_camera.setTranslateZ(_size - 46 + height * 2);
		} else if (_camera.getTranslateZ() <= 10 + height) {
			_camera.setTranslateZ(10 + height);
		} else if (_camera.getTranslateY() >= -50 + height) {
			_camera.setTranslateY(-51 + height);
		}
	}
}
