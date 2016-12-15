package edu.indy;

import java.util.concurrent.ThreadLocalRandom;

import javafx.collections.FXCollections;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;

public class Terrain {
		private ObservableFloatArray _points;
		private ObservableIntegerArray _faces;
		private TriangleMesh _terrainMesh;
		private MeshView _terrainView;
		private float[][] _heightmap;
		private int _size;
		private float _scale;
	
	public Terrain(int size, int detail, float scale, float smooth){
		_size = size;
		_scale = scale;
		//collection of points
    	 _points = FXCollections.observableFloatArray();
    	 //collection of faces
    	 _faces = FXCollections.observableIntegerArray();
		DiamondSquare diamond = new DiamondSquare(_size, detail, _scale, smooth);
		_heightmap = diamond.generate();
		_terrainMesh = this.createMesh(_size, _heightmap);
		this.setTexture();
	}
	
    //This is for creating a Triangle Mesh from the heightmap
    private TriangleMesh createMesh(int size, float[][] heightmap){
    	 
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
    					_points.addAll(tempX, tempY, tempZ);
    					vMark[x][z] = vertexCounter++;
    					vCurrent = vMark[x][z];
    				}
    				if (vDown == null){
    					//The point above
    					_points.addAll(tempX);
    					_points.addAll(heightmap[x][z + 1]);
    					_points.addAll(tempZ);
    					vMark[x][z+1] = vertexCounter ++;
    					vDown = vMark[x][z + 1];
    				}
    				if (vRight == null){
    					//The point to the right
    					_points.addAll(tempX);
    					_points.addAll(heightmap[x+1][z]);
    					_points.addAll(tempZ);
    					vMark[x+1][z] = vertexCounter ++;
    					vRight = vMark[x+1][z];
    				}
    				_faces.addAll(vCurrent, 0, vDown, 0, vRight, 0);
    			}
    			if (z - 1 >= 0 && x - 1 >= 0){
    				Integer vCurrent = vMark[x][z];
    				Integer vUp = vMark[x][z - 1];
    				Integer vLeft = vMark[x-1][z];
    				if (vCurrent == null){
    					_points.addAll(tempX, tempY, tempZ);
    					vMark[x][z] = vertexCounter++;
    					vCurrent = vMark[x][z];
    				}
    				if (vUp == null) {
    					_points.addAll(tempX);
    					_points.addAll(heightmap[x-1][z]);
    					_points.addAll(tempZ);
    					vMark[x][z-1] = vertexCounter++;
    					vUp = vMark[x][z-1];
    				}
    				if (vLeft == null) {
    					_points.addAll(tempX);
    					_points.addAll(heightmap[x][z-1]);
    					_points.addAll(tempZ);
    					vMark[x-1][z] = vertexCounter++;
    				}
    				_faces.addAll(vCurrent, 0, vUp, 0, vLeft, 0);
    			}
    		}
    	}
    	TriangleMesh mesh = new TriangleMesh();
    	//Maybe change this in the future to add texture
    	mesh.getTexCoords().addAll(0,0);
    	mesh.getPoints().addAll(_points);
    	mesh.getFaces().addAll(_faces);
    	return mesh;
    }
    
    public MeshView getView(){
    	return _terrainView;
    }
    
    public float[][] getMap(){
    	return _heightmap;
    }
    
    private void setTexture(){
    	Image gradient = new Image("/assets/alpine.jpg");
    	PhongMaterial texture = new PhongMaterial(Color.PALEGREEN);
    	texture.setDiffuseMap(gradient);
    	PixelReader pixel = gradient.getPixelReader();
    	//Trying to get the maximum and minimum height of the mesh
//    	DoubleStream ds = IntStream.range(0, _points.size())
//                .mapToDouble(i -> _points.get(i));
////    	double min = ds.min().getAsDouble();
////    	double max = ds.max().getAsDouble();
    	double min = -10;
    	double max = 40;
    	//turned off for now cuz its fucking other shit up
//    	double imageMax = gradient.getHeight();
//    	for (int i=0; i < _points.size(); i++){
//    		float point = _points.get(i);
//    		float coord = (float) scale(point, min, max, 0, imageMax);
//    		_terrainMesh.getTexCoords().addAll(coord);
//    		}
//    	_terrainMesh.getFaces().addAll(_faces);
    	_terrainView = new MeshView(_terrainMesh);
    	_terrainView.setMaterial(texture);
    	}
    
    private double scale(double valueIn, double baseMin,  double baseMax, double newMin, double newMax) {
        return ((newMax - newMin) * (valueIn - baseMin) / (baseMax - baseMin)) + newMin;
    }
    
    public Group populateDiamond(){
    	Tree tree = new Tree();
    	Group objects = new Group();
    	DiamondSquare populate = new DiamondSquare(_size, 4, 1, 1);
    	float[][] placemap = populate.generate();
    	for (int x=5; x<_size-5; x+=1){
			for (int y=5; y<_size-5; y+=1){
			float placeValue = placemap[x][y];
			Point3D terrainValue = new Point3D(x, y, (_heightmap[x][y]-4));
			if (placeValue>.9 && terrainValue.getZ() < 0 && terrainValue.getZ() > -_scale){
				//Temporary specific to trees
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
    
    

