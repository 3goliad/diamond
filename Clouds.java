package Indy;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Clouds {
	private int _size;
	
	public Clouds (int size){
	_size = size;
	}
	
	public Image generate(){
		DiamondSquare transparency = new DiamondSquare(_size, 4, 1, 3);
 		float[][] transparencyArray = transparency.generate();
		WritableImage clouds = new WritableImage(_size, _size);
		PixelWriter pixelWriter = clouds.getPixelWriter();
		float lastvalue = 0;
		for (int x=0; x<_size; x++){
			for (int y=0; y<_size; y++){
				//The color of each pixel is white, with a transparency value
				//generated from the diamondsquare
				float value = transparencyArray[x][y];
				double alpha = (float)(2%value)*.5;
				if (value > lastvalue){
				 lastvalue = value;
				}
				Color color = Color.rgb(255,255,255,alpha);
				pixelWriter.setColor(x, y, color);
			}
		}
		return clouds;
	}
	
}