package Indy;

import java.util.Comparator;

import javafx.geometry.Point3D;

public class Point3DComparator implements Comparator<Point3D>{
		@Override
		public int compare(Point3D x, Point3D y)
		{
			if (x.getZ() < y.getZ()){
				return 1;
			}
			if (x.getZ() > y.getZ()){
				return -1;
			}
			return 0;
		}
		}