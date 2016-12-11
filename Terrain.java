package Indy;

import java.util.Collections;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import java.util.Arrays;

public class Terrain {
		private ObservableFloatArray _points;
		private Mesh _terrainMesh;
		private MeshView _terrainView;
		private float[][] _heightmap;
	
	public Terrain(int size, int detail, float scale, float smooth){
		DiamondSquare diamond = new DiamondSquare(size, detail, scale, smooth);
		_heightmap = diamond.generate();
		_terrainMesh = this.createMesh(size, _heightmap);
		_terrainView = new MeshView(_terrainMesh);
		
	}
	
    //This is for creating a Triangle Mesh from the heightmap
    private TriangleMesh createMesh(int size, float[][] heightmap){
    	//Observable arrays allow us to be flexible with the number
    	//of points and vertexes.
    	//collection of points
    	ObservableFloatArray _points = FXCollections.observableFloatArray();
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
    				f.addAll(vCurrent, 0, vDown, 0, vRight, 0);
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
    				f.addAll(vCurrent, 0, vUp, 0, vLeft, 0);
    			}
    		}
    	}
    	TriangleMesh mesh = new TriangleMesh();
    	//Maybe change this in the future to add texture
    	mesh.getTexCoords().addAll(0,0);
    	mesh.getPoints().addAll(_points);
    	mesh.getFaces().addAll(f);
    	return mesh;
    }
    
    public MeshView getView(){
    	return _terrainView;
    }
    
    public float[][] getMap(){
    	return _heightmap;
    }
    
    private void setTexture(){
    	Image gradient = new Image("alpine.jpg");
    	PixelReader pixel = gradient.getPixelReader();
    	DoubleStream ds = IntStream.range(0, _points.size())
                .mapToDouble(i -> _points.get(i));
    	double min = ds.min().getAsDouble();
    	double max = ds.max().getAsDouble();
    	double imageMax = gradient.getHeight();
    	for (int i=0; i < _points.size(); i++){
    		
    	}
    }
    
    
}
