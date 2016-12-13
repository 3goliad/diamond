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
    private ChoiceBox _terrainList;
    private Slider _slider;

    public PaneOrganizer(){
    	_terrainList = new ChoiceBox();
    	_slider = new Slider();
        _pane = new BorderPane();
        this.createMainMenu();
        this.createbottomPane();
      //  Game game = new Game();
  	//  _pane.getChildren().add(game.getRoot());
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
    	mainMenu.setMaxWidth(300);
    	mainMenu.setSpacing(10);
    	mainMenu.setAlignment(Pos.CENTER);
    	//Label
    	Label terrain = new Label("Terrain Type:");
    	//Choice Box
        _terrainList = new ChoiceBox<String>(FXCollections.observableArrayList(
    			"Alpine", "Desert", "Plains"));
    	_terrainList.setPrefWidth(100);
    	_terrainList.setPrefHeight(20);
    	_terrainList.getSelectionModel().selectFirst();
    	
    	//Size Slider
    	Label size = new Label("Terrain Size:");
        _slider = new Slider(50, 400, 200);
    	_slider.setShowTickLabels(true);
    	_slider.setShowTickMarks(true);
    	_slider.setMajorTickUnit(50);
    	_slider.setBlockIncrement(.25f);
    	//Start Button
    	Button startBtn = new Button("Start");
    	startBtn.setOnAction(new StartHandler());
    	mainMenu.getChildren().addAll(terrain, _terrainList, size, _slider, startBtn);
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
        	  Game game = new Game(_terrainList.getSelectionModel().getSelectedIndex(), 
        			  _slider.getValue());
        	  _pane.getChildren().addAll(game.getRoot());
        }
    }



}
