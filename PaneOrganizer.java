package Indy;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Camera;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.event.*;
import javafx.application.Platform;
import javafx.scene.DepthTest;

/* This class provides overall organization to the application through a BorderPane.
* It instantiates Game and adds it to the BorderPane. It also adds an exit Button
* to quit the game.
*/

public class PaneOrganizer {
    private BorderPane _pane;
    
    public PaneOrganizer(){
    	 Game game = new Game();
        _pane = new BorderPane();
        _pane.setStyle("-fx-background-color: #8342ec;");
        _pane.getChildren().addAll(game.getRoot());
        _pane.setDepthTest(DepthTest.DISABLE);
        this.createbottomPane();
    }

    public Pane getRoot() {
        return _pane;
    }

    /* This method is used to create a bottom Hbox pane consisting of an
    exit button.*/
    private void createbottomPane(){
        HBox bottomPane = new HBox(50);
        bottomPane.setDepthTest(DepthTest.DISABLE);
        bottomPane.setAlignment(Pos.BOTTOM_LEFT);
        _pane.setBottom(bottomPane);
        Button btn = new Button("Exit");
        bottomPane.getChildren().addAll(btn);
        btn.setOnAction(new ExitHandler());
    }

    /*Adding click functionality to the button*/
    private class ExitHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e){
            Platform.exit();
            e.consume();
        }
    }

}
