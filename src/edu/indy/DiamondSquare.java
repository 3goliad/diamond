package edu.indy;

import java.util.concurrent.ThreadLocalRandom;

public class DiamondSquare {
	
	private int _dimension;
	private int _detail;
	private float _scale;
	private float _smooth;
	
public DiamondSquare(int dimension, int detail, float scale, float smooth){
	//Dimension controls the size of the map. Only 1 variable since it's always a square. 
	_dimension = dimension;
	//Detail controls the number of corners that are initiated at the start. Keep above 4 and below 8.
	_detail = detail;
	//Scale controls the elevation range
 	_scale = scale;
	//Smooth controls how jagged/smooth the terrain is. Keep above 3 to avoid jagged edges.
	_smooth = smooth;
}

public float[][] generate(){
	//for adding up the corners
		float avg;
		int counter;
	int regions = (int) java.lang.Math.pow(2, _detail);
	int size = _dimension*regions + 1;
	float[][] map = new float[size][size];
	//s controls how long the steps are
	int s = regions/2;
	//Fill the array with initial grid points
	for (int x=0; x<size; x+=2*s){
		for (int y=0; y<size; y+=2*s){
			map[x][y] = (float) Math.random() * _scale;
		}
	}
	//Until all points are filled 
	while (s > 0){
		//The Diamond Step
		for (int x = s; x<size; x+=2*s){
			for (int y = s; y<size; y +=2*s){
				float random = (float) ThreadLocalRandom.current().nextDouble(-(_scale), _scale);
				//Sum of all initial points
				avg = map[x+s][y+s] +
					  map[x-s][y+s] +
					  map[x+s][y-s] +
					  map[x-s][y-s];
				//average, and then add/subtract a random value within the _scale
//				if (random < 0){
//				map[x][y] = avg/4;}
//				else{
				map[x][y] = avg/4 + random;	 
//				}
			}
		}
		//The Square step
		for (int x = 0; x<size; x+=s){
			for (int y = s*(1-(x/s)%2); y<size; y+=2*s){
				counter = 0;
				avg = 0;
				if (x+s < size){
					avg+=map[x+s][y];
					counter++;
				}
			
				if (y+s < size){
					avg+=map[x][y+s];
					counter++;
				}
				if (x-s >= 0){
					avg+=map[x-s][y];
					counter++;
				}
	
				if (counter > 0){
				float random = (float) ThreadLocalRandom.current().nextDouble(-(_scale), _scale);
				map[x][y] = avg/counter + random;
				} else {
				map[x][y] = 0;
				}
			}
		}
		//S gets cut in half after each sequence of steps
		s /= 2;
		//Reduce scale by smoothing factor after each step
		_scale /= _smooth;
	}
	//Now output the filled array.
	return map;
}
}