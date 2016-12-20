package Indy;

import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Tree extends Placeable{
	private Image _tree;
		
	/*This class models the trees on the map. It loads an image
	 * in the constructor (once, so it isn't loaded several hundred times)
	 * and places it using the place method in its superclass, placeable.
	 */
	public Tree(){
		_tree = new Image("/Indy/tree.png", 5, 9, true, false);
		
	}
	 
	/*This creates and returns an imageview for each tree. Used by
	 * the populateDiamond method in terrain
	 */
	public ImageView getView(){
		ImageView _treeview = new ImageView(_tree);
		Rectangle2D viewport = new Rectangle2D(0, 0, 5, 9 );
		_treeview.setViewport(viewport);
		_treeview.setScaleX(.25);
		_treeview.setScaleY(.25);
		_treeview.setSmooth(false);
		_treeview.setPreserveRatio(true);
		_treeview.setOpacity(.6);
		return _treeview;
	}
	
	/*Extending the place method from placeable*/
	void Place(ImageView tree, Point3D loc){
		super.Place(tree, loc);
	}
	
	
}

