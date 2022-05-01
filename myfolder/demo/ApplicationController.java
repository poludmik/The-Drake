package myfolder.demo;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import thedrake.*;
import thedrake.ui.*;


public class ApplicationController {
    @FXML
    private Label welcomeText;

    @FXML private javafx.scene.control.Button closeButton;

    @FXML
    private Label welcumText;




    @FXML
    protected void playerVSplayerClick() {
//        welcomeText.setText("Let's roll.");
        Stage stage = (Stage) closeButton.getScene().getWindow();
        BoardView boardView = new BoardView(createStartGameState());
        stage.setScene(new Scene(boardView, 800, 600));
        stage.setTitle("The Drake");
        stage.show();
    }

    @FXML
    protected void internetButtonClick() {
//        welcomeText.setText("no internet for today, sorry.");
    }
    @FXML
    protected void pcButton() {
//        welcumText.setText("Good day to die!");
    }

    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    private static GameState createStartGameState() {
        Board board = new Board(4);
        PositionFactory positionFactory = board.positionFactory();
        board = board.withTiles(new Board.TileAt(positionFactory.pos(1, 1), BoardTile.MOUNTAIN));
        return new StandardDrakeSetup().startState(board)
                .placeFromStack(positionFactory.pos(0, 0))
                .placeFromStack(positionFactory.pos(3, 3))
                .placeFromStack(positionFactory.pos(0, 1))
                .placeFromStack(positionFactory.pos(3, 2))
                .placeFromStack(positionFactory.pos(1, 0))
                .placeFromStack(positionFactory.pos(2, 3));
    }
}