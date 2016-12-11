package Indy;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;



public class Buildings{
	/*Plan
	 * Take the array of floats
	 * Split it into 25 sections (less for smaller maps..)
	 * Find the flattest parts (sections with the least variation?
	 * possibly just the smallest difference between  largest and smallest float)
	 * Generate a random number of rectangles in a  grid pattern
	 * set their -y to the float value (might have to tweak this)
	 */
	private float[][] _heightmap;	
	private int _size;
	private int _s;
	private float _waterline;
	private PriorityQueue<Point3D> _sortedCorners;
	
	public Buildings(float[][] heightmap, int size, float waterline){
		_waterline = waterline;
		_heightmap = heightmap;
		_size = size;
	    _s = _size / Constants.GRID_SQUARES;
		this.createRegions();
	}
	
	public void createRegions(){
		//_size values should end in 0 or 5 to avoid problems with this
		
		//Using arraylist to keep track of each square. 
		//Point3D in order to store the deviation of the region in the Z value
		ArrayList<Point3D> corners = new ArrayList<Point3D>();
		//Recording the lower right corner of each square
		//Starting at S because we don't need the upper/right edges
		for (int x=_s; x<=_size; x+=_s){
			for (int y=_s; y<=_size; y+=_s){
				Point3D point = new Point3D(x,y,0);
				corners.add(point);
			}
			}
		//Now go through each square and find the best candidates
		for (int i = 0; i < corners.size(); i++){
			Point3D point = corners.get(i);
			float avg = 0;
			int counter = 0;
			float high = _heightmap[(int)point.getX()][(int)point.getY()];
			float low = _heightmap[(int)point.getX()][(int)point.getY()];
			for (int x = ((int)point.getX()) - _s; x<point.getX(); x++){
				for (int y = ((int)point.getY()) - _s; y<point.getY(); y++){
					//find the highest and lowest values 
					float height = _heightmap[x][y];
					if (height>high){
						high = height;
					} else if (height<low){
						low = height;
					}
					//add it to average
					avg = avg+height;
					counter++;
				}
	
			}
			avg = avg / (counter);
			//weigh according to mean
			//double difference = (high - low)+avg*8;
			double difference = (high - low)-avg;
			//no underwater towns
			if (avg< _waterline){
				difference = difference*4;
			}
			
			//if 
			//remove and reinsert
			Point3D newpoint = new Point3D(point.getX(), point.getY(), difference);
			corners.set(i, newpoint);
			
		}
		//Now we transfer the arraylist to a  priority queue 
		Comparator<Point3D> comparator = new Point3DComparator();
		PriorityQueue<Point3D> sortedCorners = new PriorityQueue<Point3D>(corners.size(), comparator);
		for (Point3D point : corners){
			sortedCorners.add(point);
		}
		//Remove everything but the lowest fifth using the Comparator
		for (int i=0; i < (int)corners.size()*.8; i++){
		sortedCorners.remove();
		}
		//
		_sortedCorners = sortedCorners;
		}
	
	public Group spawnBuildings(){
		Group towns = new Group();
		for (Point3D point: _sortedCorners){

			double x = point.getX();
            double y = point.getY();
		 x = x-(_s/2);
			y = y-(_s/2);
			
			Group town = spawnTown(x, y);
			towns.getChildren().add(town);
		}
		return towns;
		
	}
	
	private Group spawnTown(double x, double y){
		double og = x;
		Group town = new Group();
		PhongMaterial buildingColor = new PhongMaterial(Color.GRAY);
		int townsize = ThreadLocalRandom.current().nextInt(2, 12);
		for (int i=0; i <townsize; i++){
			float elevation = _heightmap[(int)x][(int)y];
			if (elevation < _waterline){
			double j = ThreadLocalRandom.current().nextDouble(0, 1);
			Box building = new Box(1+j, 1+j, 1);
			building.setMaterial(buildingColor);
			building.setTranslateX(x);
			building.setTranslateZ(y);
			
			//prevent buildings from spawning on sharp slopes
			//probably a more efficient way to do this  
			//actually this crashes the game
//			float[] neighbors = { _heightmap[(int)x-1][(int)y-1], _heightmap[(int)x+1][(int)y+1],  _heightmap[(int)x+1][(int)y-1], 
//					_heightmap[(int)x-1][(int)y+1],  _heightmap[(int)x][(int)y-1],  _heightmap[(int)x-1][(int)y], 
//					_heightmap[(int)x][(int)y+1], _heightmap[(int)x+1][(int)y], _heightmap[(int)x][(int)y] };
//			for (int z=0; i<neighbors.length; z++){
//				if (neighbors[i]>(elevation+2) | neighbors[i]<(elevation-2)){
//					x = x+5;
//				}
//			}
			building.setTranslateY(elevation);
			town.getChildren().add(building);
			x = x + 2 +j;
			if (x > og+(townsize*.6)){
				x = og;
				y = y + 2;
			}
			}
		}
		return town;
	}
	
}
	
							
		
		
	
	
