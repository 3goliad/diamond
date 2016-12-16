package edu.indy;

import java.util.HashSet;

import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.scene.shape.TriangleMesh;

import edu.uci.ics.jung.graph.Graph;

public class TerrainGraph {
	// contains the centers of the polygonal terrain zones and links to the
	// neighboring centers
	private Graph<Center, Link> _polys;
	// contains the corners of the polygonal terrain zones and the actual
	// polygon edges
	private Graph<Corner, Edge> _borders;
	// points and faces are generated from the internal graph
	private ObservableFloatArray _points;
	private ObservableIntegerArray _faces;

	// generates a new terrain graph, no parameters as of yet
	public TerrainGraph() {
		// generate polys here
	}

	// provides the mesh representation of the graph
	public void toMesh() {
	}

	class Link {
	}

	class Center {
	}

	class Edge {
	}

	class Corner {
		private float height;
	}
}
