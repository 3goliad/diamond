package Indy;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.AmbientLight;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;

public class Game{

	private BorderPane _game;
	private Camera _camera;
	private Group _root3d;
	private int _size,  _detail;
	private ImageView _cloudsView;
	private ImageView _cloudsView2;
	private float _scale, _smooth, _waterline;
	private double _cameraXVelocity, _cameraYVelocity, _cameraZVelocity;

	public Game(){
	
      /* code handling the two cloudsView is scattered across the file, create a class? */
		_cloudsView = new ImageView();
		_cloudsView2 = new ImageView();
		//controls the terrain dimensions
		_size = 200;
		_detail = 6;
		_scale = 20;
		_smooth = 2;
		_waterline = _scale;
		
		//Pane and Scene graph, 3d Group
		_cameraXVelocity = 0;
		_cameraYVelocity = 0;
		_cameraZVelocity = 0;
		_camera = new PerspectiveCamera(true);
		_game = new BorderPane();
		_root3d = new Group(_camera);
		//Lighting set up
				AmbientLight light = new AmbientLight();
				light.setColor(Color.rgb(80, 80, 100, .1));
				PointLight light2 = new PointLight();
				light2.setColor(Color.BEIGE);
				light2.setTranslateY(200);
				light2.setTranslateX(_size/2);
				light2.setTranslateZ(_size/2);
				_root3d.getChildren().addAll(light, light2);
		this.terrainSetUp();
		this.cloudSetUp();
		  SubScene subScene = new SubScene(_root3d, Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT, true,
	                SceneAntialiasing.BALANCED);
	       subScene.setCamera(_camera);
	       subScene.setFill(Color.WHITE);
		_game.setCenter(subScene);
		//Camera set up
		_camera.setNearClip(.1);
		_camera.setFarClip(1000000);
		_camera.getTransforms().add(new Rotate(-60, Rotate.X_AXIS));
		_camera.setTranslateY(-100);
		_camera.setTranslateX(50);
		_camera.setTranslateZ(0);
		//Key/Mouse Input
    _game.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
    	KeyCode code = e.getCode();
            if(code == KeyCode.UP || code == KeyCode.DOWN) {
                _cameraZVelocity = (code == KeyCode.UP ? Constants.CAMERA_SPEED : -Constants.CAMERA_SPEED);
            } else if(code == KeyCode.LEFT || code == KeyCode.RIGHT) {
            	 _cameraXVelocity = (code == KeyCode.RIGHT ? Constants.CAMERA_SPEED : -Constants.CAMERA_SPEED);
            } else if(code == KeyCode.Q || code == KeyCode.A) {
            	 _cameraYVelocity = (code == KeyCode.Q ? Constants.CAMERA_SPEED : -Constants.CAMERA_SPEED);
            }
            e.consume();
        });
    _game.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent e) -> {
    	KeyCode code = e.getCode();
            if(code == KeyCode.UP || code == KeyCode.DOWN) {
                _cameraZVelocity = 0;
            } else if(code == KeyCode.LEFT || code == KeyCode.RIGHT) {
            	 _cameraXVelocity = 0;
            } else if(code == KeyCode.Q || code == KeyCode.A) {
               _cameraYVelocity = 0;
            }
            e.consume();
        });
	    //_game.addEventHandler(MouseEvent.MOUSE_MOVED, new MouseHandler());
	    _game.requestFocus();
	    _game.setFocusTraversable(true);
	    this.setupTimeline();
	}

	public Pane getRoot() {
	        return _game;
	    }

	/*Timeline*/
	public void setupTimeline(){
        KeyFrame kf = new KeyFrame(Duration.millis(1),
                 (ActionEvent e) -> {
                    scrollClouds();
                    _camera.setTranslateY(_camera.getTranslateY()+_cameraYVelocity);
                    _camera.setTranslateX(_camera.getTranslateX()+_cameraXVelocity);
                    _camera.setTranslateZ(_camera.getTranslateZ()+_cameraZVelocity);
                 });
        Timeline timeline = new Timeline(kf);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /* could be a scroll method on a cloud holding class */
	 private void scrollClouds(){
		 _cloudsView.setX(_cloudsView.getX()+.001);
		 _cloudsView2.setX(_cloudsView2.getX()-.001);
		 if (_cloudsView.getX()>=_size){
		_cloudsView.setX(-_size);
		 }
		 if (_cloudsView2.getX()<=-_size*2){
		_cloudsView2.setX(0);
		 }
	 }

    private void cloudSetUp(){
    	Clouds clouds = new Clouds(_size);
    	Image cloudsImage = clouds.generate();
    	Rectangle2D viewport = new Rectangle2D(0, 0, _size, _size);
    	//First Tile
    	_cloudsView.setImage(cloudsImage);
    	_cloudsView.setViewport(viewport);
    	_cloudsView.getTransforms().add(new Rotate(-90, Rotate.X_AXIS));
    	_cloudsView.setTranslateY(-50);
    	_cloudsView.setTranslateZ(_size);
    	//Stretch transform so the clouds bleed over the terrain edge a bit
    	_cloudsView.getTransforms().add(new Scale(1, 1.3));
    	//Second Tile
    	_cloudsView2.setImage(cloudsImage);
    	_cloudsView2.setViewport(viewport);
    	_cloudsView2.getTransforms().add(new Rotate(-90, Rotate.X_AXIS));
    	_cloudsView2.setTranslateY(-50);
    	_cloudsView2.setTranslateZ(_size);
    	_cloudsView2.getTransforms().add(new Rotate(-180, Rotate.Y_AXIS));
    	_cloudsView2.getTransforms().add(new Scale(1, 1.3));
    	_root3d.getChildren().addAll(_cloudsView, _cloudsView2);
    }

    private void terrainSetUp(){
    	//Terrain
    	Terrain terrain = new Terrain(_size, _detail, _scale, _smooth);
    	MeshView pyramid = terrain.getView();
		pyramid.setDrawMode(DrawMode.FILL);
		pyramid.setCullFace(CullFace.FRONT);
	    // Terrain has set of objects implementing Structure that contain the ways that they change the base terrain
    	Buildings buildings = new Buildings(terrain.getMap(), _size, _waterline);
      // possible to change mesh coords with low overhead?
      // possible to place structures as independent meshes?
    	Group towns = buildings.spawnBuildings();
//		Box buildingtest = new Box(2,2,2);
//		buildingtest.setTranslateX(20);
//		PhongMaterial redstuff = new PhongMaterial(Color.RED);
//		buildingtest.setMaterial(redstuff);
    	//water
    	Water water = new Water(_size, _waterline);
		_root3d.getChildren().addAll( pyramid, towns, water.getWater(), terrain.populateDiamond());
    }

   

}
