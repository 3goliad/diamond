package Indy;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This program is a customizable terrain generator. It allows you to set some
 * parameters in the main menu (size, biome, time of day) and generate a terrain
 * based on them. The terrain is viewable through moving the camera, and the
 * user can interact with the terrain by dropping explosives on it. The terrain
 * is generated through a Diamond Square algorithm (stored in the DiamondSquare
 * class) that populates a 2 dimensional array of floats with different height
 * values. Those height values are then used the generate a mesh and texture.
 * The Diamond Square algorithm is also used to populate the terrain with trees
 * and clouds. The terrain also features villages, which are randomized rows of
 * building boxes that are seeded at favorable tiles of the map. Overall, the
 * design of the program models aspects of the terrain through a few different
 * classes that are instantiated in the game class. There is one abstract class
 * (placeable) which is intended to allow for the polymorphic construction of
 * objects that populate the map, but at this point it is only extended by the
 * Tree class.
 */

public class App extends Application {

	public App() {
		// Constructor code goes here.
	}

	@Override
	public void start(Stage stage) throws Exception {
		PaneOrganizer organizer = new PaneOrganizer();
		Scene scene = new Scene(organizer.getRoot(), Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args); // launch is a method inherited from Application
	}
}
