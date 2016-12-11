package Indy;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Camera;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.event.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.DepthTest;
import javafx.collections.ObservableList;

/* This class provides overall organization to the application through a BorderPane.
* It instantiates Game and adds it to the BorderPane. It also adds an exit Button
* to quit the game.
*/

public class PaneOrganizer {
    private BorderPane _pane;
    private HBox _bottomPane;

    public PaneOrganizer(){
        _pane = new BorderPane();
       // _pane.setStyle("-fx-background-color: #000;");
        this.createMainMenu();
        this.createbottomPane();
        Game game = new Game();
  	  _pane.getChildren().add(game.getRoot());
    }

    public Pane getRoot() {
        return _pane;
    }

    /* This method is used to create a bottom Hbox pane consisting of an
    exit button.*/
    private void createbottomPane(){
        HBox _bottomPane = new HBox(50);
        _bottomPane.setDepthTest(DepthTest.DISABLE);
        _bottomPane.setAlignment(Pos.BOTTOM_LEFT);
        Button exitBtn = new Button("Exit");
        _bottomPane.getChildren().addAll(exitBtn);
        exitBtn.setOnAction(new ExitHandler());
        _pane.setBottom(_bottomPane);
    }

    private void createMainMenu(){
    	VBox mainMenu = new VBox(150);
    	mainMenu.setMaxWidth(100);
    	mainMenu.setSpacing(10);
    	mainMenu.setAlignment(Pos.CENTER);
    	//Label
    	Label terrain = new Label("Terrain Type:");
    	//Choice Box
    	ChoiceBox terrainList = new ChoiceBox<String>(FXCollections.observableArrayList(
    			"Alpine", "Desert", "Plains"));
    	terrainList.setPrefWidth(100);
    	terrainList.setPrefHeight(20);
    	terrainList.getSelectionModel().selectFirst();
//    	  terrainList.getSelectionModel().selectedIndexProperty().addListener(new
//    	    		ChangeListener<Number>(){
//
//    	  }
    	//Start Button
    	Button startBtn = new Button("Start");
    	startBtn.setOnAction(new StartHandler());
    	mainMenu.getChildren().addAll(terrain, terrainList, startBtn);
    	_pane.setCenter(mainMenu);
    }


    /*Adding click functionality to the button*/
    private class ExitHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e){
            Platform.exit();
            e.consume();
        }
    }

    /*Start button should begin the game*/
    private class StartHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e){
        	 //Game game = new Game();
        	  //_pane.getChildren().add(game.getRoot());
        }
    }



}
