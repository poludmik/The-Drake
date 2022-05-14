package thedrake.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import myfolder.demo.ApplicationController;
import myfolder.demo.ApplicationMain;
import thedrake.Board;
import thedrake.BoardTile;
import thedrake.GameState;
import thedrake.PositionFactory;
import thedrake.StandardDrakeSetup;

public class TheDrakeApp extends Application {

    static ApplicationController myControllerHandle;
    private Board board;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationMain.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        scene.getStylesheets().add("test.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("You have an amazing smile!");
        primaryStage.show();

        board = new Board(4);
        myControllerHandle = fxmlLoader.getController();
        myControllerHandle.gameState = createStartGameState();
        myControllerHandle.board = board;
    }

    private GameState createStartGameState() {
        PositionFactory positionFactory = board.positionFactory();
        board = board.withTiles(new Board.TileAt(positionFactory.pos(1, 2), BoardTile.MOUNTAIN));
        return new StandardDrakeSetup().startState(board)
                .placeFromStack(positionFactory.pos(0, 0))
                .placeFromStack(positionFactory.pos(3, 3))
                .placeFromStack(positionFactory.pos(0, 1))
                .placeFromStack(positionFactory.pos(3, 2))
                .placeFromStack(positionFactory.pos(1, 0))
                .placeFromStack(positionFactory.pos(2, 3));
    }

}
