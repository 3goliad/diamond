package edu.indy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
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
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
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
	private Clouds _clouds;
	private float _scale, _smooth, _waterline;
	private double _cameraXVelocity, _cameraYVelocity, _cameraZVelocity;
	private MeshView _terrainView;
	private Point3D _mousePoint;
	private Group _explosions;
	private Group _towns;
	private Group _trees;
	private Image _boomMap;
	
	/*Game class is tasked with controlling the core mechanics of the program
	 * and instantiating other classes. Handles controls, interaction (explosions
	 * and exploding other objects), setting up lighting, and setting up the landscape. 
	 */
	public Game(int terrainValue, double size, int time){
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
		_explosions = new Group();
		//Making these Groups into instance variables for destruction purposes
		_towns = new Group();
		_trees = new Group();
		_boomMap = new Image("/Indy/Boom.jpg");
		_terrainView = new MeshView();
		this.terrainSetUp();
		_clouds = new Clouds(_size);
		Group cloudGroup = new Group(_camera);
		cloudGroup.getChildren().add(_clouds.cloudSetUp());
		cloudGroup.setMouseTransparent(true);
		_root3d.getChildren().addAll(_explosions, cloudGroup);
		//Lighting set up
		this.lightSetUp(time);
		  SubScene subScene = new SubScene(_root3d, Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT, true,
	                SceneAntialiasing.BALANCED);
	       subScene.setCamera(_camera);
	       subScene.setFill(Color.BLACK);
		_game.setCenter(subScene);
		//Camera set up
		_camera.setNearClip(.1);
		_camera.setFarClip(1000000);
		_camera.getTransforms().add(new Rotate(-70, Rotate.X_AXIS));
		_camera.setTranslateY(-100);
		_camera.setTranslateX(50);
		_camera.setTranslateZ(0);
		//Key/Mouse Input
		_mousePoint = new Point3D(0, 0, 0);
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
	
	private void lightSetUp(int time){
		//Lighting set up
		if (time == 0){
		AmbientLight light = new AmbientLight();
		light.setColor(Color.rgb(80, 80, 100, .1));
		PointLight light2 = new PointLight();
		light2.setColor(Color.BEIGE);
		light2.setTranslateY(200);
		light2.setTranslateX(_size/2);
		light2.setTranslateZ(_size/2);
		_root3d.getChildren().addAll(light, light2);
		} else if (time == 1) {
			AmbientLight light = new AmbientLight();
			light.setColor(Color.rgb(20, 20, 20, .1));
			PointLight light2 = new PointLight();
			light2.setColor(Color.rgb(40, 40, 55, .1));
			light2.setTranslateY(200);
			light2.setTranslateX(_size/2);
			light2.setTranslateZ(_size/2);
			_root3d.getChildren().addAll(light, light2);	
			_clouds.setOpacity(.3);
		} else {
			AmbientLight light = new AmbientLight();
			light.setColor(Color.rgb(60, 60, 132, .1));
			PointLight light2 = new PointLight();
			light2.setColor(Color.rgb(255, 160, 0));
			light2.setTranslateY(100);
			light2.setTranslateX(_size/2);
			light2.setTranslateZ(_size/2);
			_root3d.getChildren().addAll(light, light2);	
		}
	}

	/*Timeline*/
	public void setupTimeline(){
        KeyFrame kf = new KeyFrame(Duration.millis(15),
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
	
	private void Explode(Point3D point){
		//jitter to make the explosions vary more;
		double j = ThreadLocalRandom.current().nextDouble(0, 4);
		Sphere sphere = new Sphere();
		//Temporarily small so the delay works
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
		//First explosion
		ScaleTransition st = new ScaleTransition(Duration.millis(4000), sphere);
		st.setInterpolator(Interpolator.EASE_IN);
		st.setByX(j*200f);
	     st.setByY(j*200f);
	     st.setByZ(j*200f);
	     st.setDelay(Duration.millis(50*j));
	    //Retraction
	     ScaleTransition rt = new ScaleTransition(Duration.millis(1500*j), sphere);
			rt.setInterpolator(Interpolator.EASE_OUT);
			rt.setToX(j*.7f);
		     rt.setToY(j*.7f);
		     rt.setToZ(j*.7f);

		final Animation lightOn = new Transition(){
			 {
		         setCycleDuration(Duration.millis(1500));
		     }
			protected void interpolate(double frac) {
				int n = (int) (255 * frac);
		       light.setColor(Color.rgb(n, n/2, 0));
		         
		     }
		};
		
		lightOn.setDelay(Duration.millis(500*j));
		final Animation lightOff = new Transition(){
			 {
		         setCycleDuration(Duration.millis(1500*j));
		     }
			protected void interpolate(double frac) {
				int n = (int) (255 * frac);
		       light.setColor(Color.rgb(255-n, (255/2)-n/2, 0));
		         
		     }
		};
		final Animation SphereOff = new Transition(){
			 {
		         setCycleDuration(Duration.millis(1500*j));
		     }
			protected void interpolate(double frac) {
				double n = 1 * frac;
				int c = (int)frac*255;
				material.setDiffuseColor(Color.rgb(255-c, 255-c, 255-c, 1-n));
				sphere.setMaterial(material);
		     }
		};
		ParallelTransition boom = new ParallelTransition(st, lightOn);
		ParallelTransition fade = new ParallelTransition(rt, SphereOff, lightOff);
		SequentialTransition seqT = new SequentialTransition (boom, fade);
	    seqT.play();
	    buildingExplode(point);
	    treeBurn(point);
	}
	
	private void buildingExplode(Point3D boom){
		//List<Node> nodesToRemove = new ArrayList<>();
		for(int i=0; i< _towns.getChildren().size(); i++){
			Node town = _towns.getChildren().get(i);
			if(town instanceof Group){
				for(Node nodeIn:((Group)town).getChildren()){
					Point3D point = new Point3D(nodeIn.getTranslateX(),
							nodeIn.getTranslateY(), nodeIn.getTranslateZ());
					if (point.distance(boom)<10){
						nodeIn.setTranslateY(500);
						Explode(point);
						//nodesToRemove.add(nodeIn);
					}
				}
			}
			
			
			}
		}
	
	private void treeBurn(Point3D boom){
		for(int i=0; i< _trees.getChildren().size(); i++){
			Node tree = _trees.getChildren().get(i);
			Point3D point = new Point3D(tree.getTranslateX(),
					tree.getTranslateY(), tree.getTranslateZ());
			if (point.distance(boom)<10){
				Image burn = new Image("/Indy/burnttree.png");
				ImageView _treeview = new ImageView(burn);
				Rectangle2D viewport = new Rectangle2D(0, 0, 5, 9);
				_treeview.setScaleX(.25);
				_treeview.setScaleY(.25);
				_treeview.setViewport(viewport);
				_treeview.setTranslateX(tree.getTranslateX());
				_treeview.setTranslateY(tree.getTranslateY());
				_treeview.setTranslateZ(tree.getTranslateZ());
				_explosions.getChildren().add(_treeview);
				tree.setTranslateY(500);
			}
		}
	}

//	private void Burn(ImageView burn){
//	Image burn = new Image("/Indy/fire.gif");
//	_treeview = new ImageView(burn);
//	PointLight fire = new PointLight();
//	fire.setTranslateX(_treeview.getTranslateX());
//	fire.setTranslateY(_treeview.getTranslateY());
//	fire.setTranslateZ(_treeview.getTranslateZ());
//	}

	/*Keeps the camera from going outside the terrain bounds*/
	private void bounceCamera(){
		//Keep track of height so that the bounding box can adjust accordingly
		double height = _camera.getTranslateY()/4;
		if (_camera.getTranslateX() >= _size + height){
			_camera.setTranslateX(_size - 1 + height);
		} else if (_camera.getTranslateX() <= 1 - height){
			_camera.setTranslateX(2 - height);
		} else if (_camera.getTranslateZ() >= _size - 45 + height*2){
			_camera.setTranslateZ(_size - 46 + height*2);
		} else if (_camera.getTranslateZ() <= 10 + height){
			_camera.setTranslateZ(10 + height);
		} else if (_camera.getTranslateY() <= -450){
			_camera.setTranslateY(-449);
		} else if (_camera.getTranslateY() >= -50){
			_camera.setTranslateY(-51);
		}
	}

  
 

    private void terrainSetUp(){
    	//Terrain
    	Terrain terrain = new Terrain(_size, _detail, _scale, _smooth);
    	_terrainView = terrain.getView();
		_terrainView.setDrawMode(DrawMode.FILL);
		_terrainView.setCullFace(CullFace.FRONT);
	    // Terrain has set of objects implementing Structure that contain the ways that they change the base terrain
    	Buildings buildings = new Buildings(terrain.getMap(), _size, _waterline);
      // possible to change mesh coords with low overhead?
      // possible to place structures as independent meshes?
    	_towns = buildings.spawnBuildings();
//		Box buildingtest = new Box(2,2,2);
//		buildingtest.setTranslateX(20);
//		PhongMaterial redstuff = new PhongMaterial(Color.RED);
//		buildingtest.setMaterial(redstuff);
    	//water
    	Water water = new Water(_size, _waterline);
    	//Trees
    	_trees = terrain.populateDiamond();
		_root3d.getChildren().addAll(_terrainView, _towns, water.getWater(), _trees);
    }

   

}

