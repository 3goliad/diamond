package Indy;

import javafx.geometry.Point3D;
import javafx.scene.Node;

public abstract class Placeable {

	/*This is a simple abstract class that provides one method,
	 * place. Place translates the coordinates of a node to a 
	 * Point3D location. It's only used for trees right now, but
	 * I plan to employ it when populating the terrain with other
	 * objects (didn't get that far before the deadline, but I want 
	 * to keep working on this myself.)
	 */
 void Place(Node node, Point3D loc){
	node.setTranslateX(loc.getX());
	node.setTranslateZ(loc.getY());
	node.setTranslateY(loc.getZ());
}
	
}
