package edu.indy;

import java.util.HashSet;

import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.scene.shape.TriangleMesh;

import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class TerrainGraph {
	// contains the centers of the polygonal terrain zones and links to the
	// neighboring centers
	private UndirectedSparseGraph<Center, Edge> _polys;
	// contains the corners of the polygonal terrain zones and the actual
	// polygon edges
	private UndirectedSparseGraph<Corner, Edge> _borders;
	// points and faces are generated from the internal graph
	private ObservableFloatArray _points;
	private ObservableIntegerArray _faces;

	// generates a new terrain graph, no parameters as of yet
	public TerrainGraph(int size, int detail, float scale, float smooth) {
		_polys = new UndirectedSparseGraph<Center, Edge>();
		_borders = new UndirectedSparseGraph<Corner, Edge>();
		float[][] heights = DiamondSquare.generate(size, detail, scale, smooth);

		//a square of polygons (squares) size by size
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				_polys.addVertex(new Center(heights[i][j]));
			}
		}
	}

	// provides the mesh representation of the graph
	public void toMesh() {
	}

	class Center {
		private float _height;

		public Center(float height) {
			_height = height;
		}

		/**
		 * @return the _height
		 */
		public float get_height() {
			return _height;
		}

	}

	class Edge {
		private int _length;
		private Pair<Center> centers;
		private Pair<Corner> corners;
	}

	class Corner {
		private float _height;

		/**
		 * @return the _height
		 */
		public float get_height() {
			return _height;
		}
	}
}
