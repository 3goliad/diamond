package edu.indy;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;


public class Water {
	private Box _water;
	public Water(int width, float waterline){
	 _water = new Box(width,1,width);
	 PhongMaterial mat = new PhongMaterial(Color.DODGERBLUE);
	 //mat.setColor(Color.DODGERBLUE);
	 mat.setDiffuseColor(Color.rgb(0,0,200, .5));
	 _water.setMaterial(mat);
	 _water.setTranslateY(waterline);
	 _water.setTranslateZ(width/2);
	 _water.setTranslateX(width/2);
	 //_water.setLayoutX(value);
	}
	
	public Box getWater(){
		return _water;
	}
	
	
}
