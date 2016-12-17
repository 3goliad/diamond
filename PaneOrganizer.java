package Indy;

import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.event.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.DepthTest;

/* This class provides overall organization to the application through a BorderPane.
* and creates the main menu. 
* When the start button is clicked, It instantiates Game with the menu's parameters
* and adds it to the BorderPane. It also adds an exit Button
* to quit the game.
*/

public class PaneOrganizer {
	private BorderPane _pane;
	private HBox _bottomPane;
	private ChoiceBox<String> _terrainList, _timeList;
	private Slider _slider;
	private VBox _mainMenu;

	public PaneOrganizer() {
		_mainMenu = new VBox(150);
		_terrainList = new ChoiceBox<String>();
		_timeList = new ChoiceBox<String>();
		_slider = new Slider();
		_pane = new BorderPane();
		this.createMainMenu();
		this.createbottomPane();
	}

	public Pane getRoot() {
		return _pane;
	}

	/*
	 * This method is used to create a bottom Hbox pane consisting of an exit
	 * button.
	 */
	private void createbottomPane() {
		_bottomPane = new HBox(50);
		_bottomPane.setDepthTest(DepthTest.DISABLE);
		_bottomPane.setAlignment(Pos.BOTTOM_LEFT);
		Button exitBtn = new Button("Exit");
		_bottomPane.getChildren().addAll(exitBtn);
		exitBtn.setOnAction(new ExitHandler());
		_pane.setBottom(_bottomPane);
	}

	/* This method creates the main menu */
	private void createMainMenu() {
		_mainMenu.setMaxWidth(300);
		_mainMenu.setSpacing(10);
		_mainMenu.setAlignment(Pos.CENTER);
		// Control Info
		Text controls = new Text("Press Start to generate a terrain!\nControls:\nArrow Keys to move Camera"
				+ "\nA and Q to zoom in and out\nClick to drop munitions on terrain");
		controls.setLineSpacing(2.5);
		controls.setTextAlignment(TextAlignment.CENTER);
		// Label
		Label terrain = new Label("Terrain Type:");
		// Terrain Choice Box
		_terrainList = new ChoiceBox<String>(
				FXCollections.observableArrayList("Alpine", "Desert", "Plains", "Islands", "Arctic", "Himalaya"));
		_terrainList.setPrefWidth(100);
		_terrainList.setPrefHeight(20);
		_terrainList.getSelectionModel().selectFirst();
		// Time Choice Box
		Label time = new Label("Time of day:");
		_timeList = new ChoiceBox<String>(FXCollections.observableArrayList("Noon", "Midnight", "Sunset"));
		_timeList.setPrefWidth(100);
		_timeList.setPrefHeight(20);
		_timeList.getSelectionModel().selectFirst();
		// Size Slider
		Label size = new Label("Terrain Size:");
		_slider = new Slider(100, 400, 200);
		_slider.setShowTickLabels(true);
		_slider.setShowTickMarks(true);
		_slider.setMajorTickUnit(50);
		_slider.setBlockIncrement(5);
		_slider.setSnapToTicks(true);
		// Start Button
		Button startBtn = new Button("Start");
		startBtn.setOnAction(new StartHandler());
		_mainMenu.getChildren().addAll(controls, terrain, _terrainList, time, _timeList, size, _slider, startBtn);
		_pane.setCenter(_mainMenu);
	}

	/* Adding click functionality to the button */
	private class ExitHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			Platform.exit();
			e.consume();
		}
	}

	/* Start button should begin the game */
	private class StartHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			Game game = new Game(_terrainList.getSelectionModel().getSelectedIndex(), _slider.getValue(),
					_timeList.getSelectionModel().getSelectedIndex());
			_pane.getChildren().removeAll(_bottomPane, _mainMenu);
			_pane.getChildren().addAll(game.getRoot(), _bottomPane);
		}
	}

}
