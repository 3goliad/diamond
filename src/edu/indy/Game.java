package edu.indy;

import Indy.fxyz3d.shapes.composites.PolyLine3D;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.AmbientLight;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;

public class Game{

	private BorderPane _game;
	private Camera _camera;
	private Group _root3d;
	private int _size,  _detail;
	private Clouds _clouds;
	private float _scale, _smooth, _waterline;
	private double _cameraXVelocity, _cameraYVelocity, _cameraZVelocity;

	public Game(int terrainValue, double size){
		_size = (int) size;
		//controls the terrain dimensions
		if (terrainValue == 0){
			//Alpine
			_detail = 6;
			_scale = 30;
			_smooth = 2;
			_waterline = _scale;
		} else if (terrainValue == 1){
			//Desert
			_detail = 6;
			_scale = 15;
			_smooth = 3;
			_waterline = _scale+10;
		} else if (terrainValue == 2){
			//Plains
			_detail = 6;
			_scale = 5;
			_smooth = 3;
			_waterline = _scale;
		}
		
		
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
		_clouds = new Clouds(_size);
		this.terrainSetUp();
		
		  SubScene subScene = new SubScene(_root3d, Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT, true,
	                SceneAntialiasing.BALANCED);
	       subScene.setCamera(_camera);
	       subScene.setFill(Color.WHITE);
		_game.setCenter(subScene);
		//Camera set up
		_camera.setNearClip(.1);
		_camera.setFarClip(1000000);
		_camera.getTransforms().add(new Rotate(-70, Rotate.X_AXIS));
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
                    _clouds.scrollClouds();
                    _camera.setTranslateY(_camera.getTranslateY()+_cameraYVelocity);
                    _camera.setTranslateX(_camera.getTranslateX()+_cameraXVelocity);
                    _camera.setTranslateZ(_camera.getTranslateZ()+_cameraZVelocity);
                    this.bounceCamera();
                 });
        Timeline timeline = new Timeline(kf);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
	
	private void bounceCamera(){
//		double height = _camera.getTranslateY();
//		if (_camera.getTranslateX() > _size - 10/height){
//			_camera.setTranslateX(_size -11/height);
//		} else if (_camera.getTranslateX()< 10 /height ){
//			_cameraXVelocity = 0;
//		} else if (_camera.getTranslateZ() > _size- 100/height ){
//			_cameraZVelocity = 0;
//		} else if (_camera.getTranslateZ() < 0 - height ){
//			_cameraZVelocity = 0;
//		}
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
		_root3d.getChildren().addAll( pyramid, towns, water.getWater(), terrain.populateDiamond(), _clouds.cloudSetUp());
    }

   

}
