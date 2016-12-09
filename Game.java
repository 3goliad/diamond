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
import javafx.scene.Ambient Light;
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

public class Game{

	private BorderPane _game;
	private Camera _camera;
	private Group _root3d;
	private int _size;
	private ImageView _cloudsView;
	private ImageView _cloudsView2;

	public Game(){
      /* code handling the two cloudsView is scattered across the file, create a class? */
		_cloudsView = new ImageView();
		_cloudsView2 = new ImageView();
		//controls the terrain dimensions
		_size = 250;
		//Pane and Scene graph, 3d Group
		_camera = new PerspectiveCamera(true);
		_game = new BorderPane();
		_root3d = new Group(_camera);
		//Lighting set up
				PointLight light = new PointLight();
				light.setTranslateY(100);
				light.setColor(Color.ALICEBLUE);
				PointLight light2 = new PointLight();
				light2.setTranslateY(100);
				light2.setColor(Color.BEIGE);
				light2.setTranslateY(100);
				light2.setTranslateX(_size);
				light2.setTranslateZ(_size);
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
	    _game.addEventHandler(KeyEvent.KEY_PRESSED, new KeyHandler());
      /* KeyHandler could be replaced with a lambda, but it's up to you if you want the key logic here */
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
      /* Framerate shouldn't update faster than the screen refresh rate, wastes resources. Set to 60Hz? */
        KeyFrame kf = new KeyFrame(Duration.millis(1),
                                   /* (ActionEvent e) -> {
                                      scrollClouds();
                                      });
                                      This lambda function can take the place of the TimeHandler class
                                   */
        new TimeHandler());
        Timeline timeline = new Timeline(kf);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /* this class name says more about the interface the class implements than the function of the class, change? */
	 private class TimeHandler implements EventHandler<ActionEvent>{
	        public void handle(ActionEvent event){
	        	scrollClouds();
	        	//System.out.println(_cloudsView.getTranslateZ());
	        }
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
	
	 /*Controls*/
    /* this class name is a paraphrase of the interface, consider using a name that describes its function (CameraPanner?) */
    private class KeyHandler implements EventHandler<KeyEvent>{
        @Override
        public void handle(KeyEvent keyEvent){
            /*
              int code = keyEvent.getCode();
              if(code == UP || code == DOWN) {
              _camera.setTranslateZ(_camera.getTranslateZ() + (code == UP ? 10 : -10));
              System.out.println(_camera.getTranslateZ());
              } else if(code == LEFT || code == RIGHT) {
              _camera.setTranslateX(_camera.getTranslateX() + (code == RIGHT ? 5 : -5));
              System.out.println(_camera.getTranslateX());
              } else if(code == Q || code == A) {
              _camera.setTranslateY(_camera.getTranslateY() + (code == Q ? 5 : -10));
              System.out.println(_camera.getTranslateY());
              }
              keyEvent.consume();

              By abusing the ternary operator, this logic can be cut from 36 to 12 lines
              In addition, this uses fewer compares, though I suspect the performance bottleneck is not in the key handling code
            */
            switch (keyEvent.getCode()){

            case UP:
            _camera.setTranslateZ(_camera.getTranslateZ()+10);
            System.out.println(_camera.getTranslateZ());
            break;
            
            case DOWN:
            _camera.setTranslateZ(_camera.getTranslateZ()-10);
            System.out.println(_camera.getTranslateZ());
            break;
            
            case LEFT:
            _camera.setTranslateX(_camera.getTranslateX()-5);
            System.out.println(_camera.getTranslateX());
            break;
            
            case RIGHT:
            _camera.setTranslateX(_camera.getTranslateX()+5);
            System.out.println(_camera.getTranslateX());
            break;
            
            case Q:
            _camera.setTranslateY(_camera.getTranslateY()+5);
            System.out.println(_camera.getTranslateY());
             break;
             
            case A:
                _camera.setTranslateY(_camera.getTranslateY()-10);
                System.out.println(_camera.getTranslateY());
                 break;

            default:
                break;
        }
        keyEvent.consume();
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
        // dedicated Terrain class can manage individual Vertex objects with heightmap data
    	float[][] heightmap = generateHeightMap();
      // Terrain has set of objects implementing Structure that contain the ways that they change the base terrain
    	Buildings buildings = new Buildings(heightmap, _size);
      // possible to change mesh coords with low overhead?
      // possible to place structures as independent meshes?
    	Group towns = buildings.spawnBuildings();
    	TriangleMesh mesh = createMesh(_size, heightmap);
    	MeshView pyramid = new MeshView(mesh);
		pyramid.setDrawMode(DrawMode.FILL);
		pyramid.setCullFace(CullFace.FRONT);
		PhongMaterial bluestuff = new PhongMaterial(Color.BURLYWOOD);
		pyramid.setMaterial(bluestuff);
//		Box buildingtest = new Box(2,2,2);
//		buildingtest.setTranslateX(20);
//		PhongMaterial redstuff = new PhongMaterial(Color.RED);
//		buildingtest.setMaterial(redstuff);
		_root3d.getChildren().addAll(pyramid, towns);
    }

    private float[][] generateHeightMap(){
    	DiamondSquare diamond = new DiamondSquare(_size, 6, 10, 2);
    	return diamond.generate();
    }
    //This is for creating a Triangle Mesh from the heightmap
    private TriangleMesh createMesh(int size, float[][] heightmap){
    	//Observable arrays allow us to be flexible with the number
    	//of points and vertexes.
    	//collection of points
    	ObservableFloatArray p = FXCollections.observableFloatArray();
    	//collection of faces
    	ObservableIntegerArray f = FXCollections.observableIntegerArray();
    	//vMark keeps track of which vertex we're working on
    	Integer[][] vMark = new Integer[size][size];
    	int vertexCounter = 0;
    	for (int x = 0; x < size; x++){
    		for (int z = 0; z < size; z++){
    			float tempX = x;
    			float tempY = heightmap[x][z];
    			float tempZ = z;
    			if (z + 1 < size && x + 1 < size){
    				Integer vCurrent = vMark[x][z];
    				Integer vDown = vMark[x][z + 1];
    				Integer vRight = vMark[x+1][z];
    				//When it reaches a new vertex point, add the temp values
    				if (vCurrent == null){
    					p.addAll(tempX, tempY, tempZ);
    					vMark[x][z] = vertexCounter++;
    					vCurrent = vMark[x][z];
    				}
    				if (vDown == null){
    					//The point above
    					p.addAll(tempX);
    					p.addAll(heightmap[x][z + 1]);
    					p.addAll(tempZ);
    					vMark[x][z+1] = vertexCounter ++;
    					vDown = vMark[x][z + 1];
    				}
    				if (vRight == null){
    					//The point to the right
    					p.addAll(tempX);
    					p.addAll(heightmap[x+1][z]);
    					p.addAll(tempZ);
    					vMark[x+1][z] = vertexCounter ++;
    					vRight = vMark[x+1][z];
    				}
    				f.addAll(vCurrent, 0, vDown, 0, vRight, 0);
    			}
    			if (z - 1 >= 0 && x - 1 >= 0){
    				Integer vCurrent = vMark[x][z];
    				Integer vUp = vMark[x][z - 1];
    				Integer vLeft = vMark[x-1][z];
    				if (vCurrent == null){
    					p.addAll(tempX, tempY, tempZ);
    					vMark[x][z] = vertexCounter++;
    					vCurrent = vMark[x][z];
    				}
    				if (vUp == null) {
    					p.addAll(tempX);
    					p.addAll(heightmap[x-1][z]);
    					p.addAll(tempZ);
    					vMark[x][z-1] = vertexCounter++;
    					vUp = vMark[x][z-1];
    				}
    				if (vLeft == null) {
    					p.addAll(tempX);
    					p.addAll(heightmap[x][z-1]);
    					p.addAll(tempZ);
    					vMark[x-1][z] = vertexCounter++;
    				}
    				f.addAll(vCurrent, 0, vUp, 0, vLeft, 0);
    			}
    		}
    	}
    	TriangleMesh mesh = new TriangleMesh();
    	//Maybe change this in the future to add texture
    	mesh.getTexCoords().addAll(0,0);
    	mesh.getPoints().addAll(p);
    	mesh.getFaces().addAll(f);
    	return mesh;
    }

}
