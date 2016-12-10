package Indy;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

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
	private PriorityQueue<Point3D> _sortedCorners;
	
	public Buildings(float[][] heightmap, int size){
		_heightmap = heightmap;
		_size = size;
		_s = 0;
		this.createRegions();
	}
	
	public void createRegions(){
		//_size values should end in 0 or 5 to avoid problems with this
		int _s = _size / Constants.GRID_SQUARES;
		
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
					avg =+ height;
				}
				//calculate mean
				avg = avg / x;
			}
			//weigh according to mean
			double difference = (high - low)+avg*2;
			
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
		PhongMaterial buildingColor = new PhongMaterial(Color.RED);
		Group towns = new Group();
		for (Point3D point: _sortedCorners){
			Box building = new Box(3, 3, 1);
			building.setMaterial(buildingColor);
			double x = point.getX()-(_s/2);
			double y = point.getY()-(_s/2);
			building.setTranslateX(x);
			building.setTranslateZ(y);
			float elevation = _heightmap[(int)building.getTranslateX()][(int)building.getTranslateZ()];
			building.setTranslateY(elevation);
			towns.getChildren().add(building);
		}
		return towns;
		
	}
	
}
	
							
		
		
	
	
