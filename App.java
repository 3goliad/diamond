package Indy;

import javafx.application.Application;
import javafx.scene.Camera;
import javafx.scene.DepthTest;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.BorderPane;

/**
 * It's time for Indy! This is the  main class to get things started.
 *
 * The main method of this application calls the App constructor. You
 * will need to fill in the constructor to instantiate your Indy project.
 *
 * Class comments here...
 *
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
