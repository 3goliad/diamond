package edu.indy;

import java.util.concurrent.ThreadLocalRandom;

import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Tree extends Placeable{
	private Image _tree;
	
	public Tree(){
		_tree = new Image("/assets/tree.png", 5, 9, true, false);
	}
	 
	private ColorAdjust ColorEffect(){
		ColorAdjust colorAdjust = new ColorAdjust();
		double tint = ThreadLocalRandom.current().nextDouble(-.1, .1);
		colorAdjust.setHue(tint);
		return colorAdjust;
	}
	
	public ImageView getView(){
		ImageView _treeview = new ImageView(_tree);
		//_treeview.setEffect(ColorEffect());
		Rectangle2D viewport = new Rectangle2D(0, 0, 5, 9 );
		_treeview.setViewport(viewport);
		_treeview.setScaleX(.25);
		_treeview.setScaleY(.25);
		_treeview.setSmooth(false);
		_treeview.setPreserveRatio(true);
		_treeview.setOpacity(.6);
		return _treeview;
	}
	
	void Place(ImageView tree, Point3D loc){
		super.Place(tree, loc);
	}
	
}

