package edu.indy;

import javafx.geometry.Point3D;
import javafx.scene.Node;

public abstract class Placeable {

 void Place(Node node, Point3D loc){
	node.setTranslateX(loc.getX());
	node.setTranslateZ(loc.getY());
	node.setTranslateY(loc.getZ());
}
	
}
