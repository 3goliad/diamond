package Indy;

import java.util.Comparator;

import javafx.geometry.Point3D;

public class Point3DComparator implements Comparator<Point3D> {

	/*
	 * This is a small class used only by the priority queue in the buildings
	 * algorithm. It simply compares two Point3Ds by their z value. Its purpose
	 * is to sort spawning locations by flatness and elevation.
	 */
	@Override
	public int compare(Point3D x, Point3D y) {
		if (x.getZ() < y.getZ()) {
			return 1;
		}
		if (x.getZ() > y.getZ()) {
			return -1;
		}
		return 0;
	}
}