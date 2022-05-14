package thedrake.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import thedrake.*;
import myfolder.demo.*;

import javax.imageio.IIOException;

import static myfolder.demo.ApplicationController.returnStandardGroup;

public class BoardView extends GridPane implements TileViewContext {

    private GameState gameState;

    private ValidMoves validMoves;

    Stage stage;

    private TileView selected;

    public BoardView(GameState gameState) {
        this.gameState = gameState;
        this.validMoves = new ValidMoves(gameState);

        PositionFactory positionFactory = gameState.board().positionFactory();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                BoardPos boardPos = positionFactory.pos(x, 3 - y);
                add(new TileView(boardPos, gameState.tileAt(boardPos), this), x, y);
            }
        }

        setHgap(5);
        setVgap(5);
        setPadding(new Insets(15));
        setAlignment(Pos.CENTER);
    }

    public void onPress(){
        if (selected != null)
            selected.unselect();

        showMoves(validMoves.movesFromStack());
    }

    @Override
    public void tileViewSelected(TileView tileView) {
        if (selected != null && selected != tileView)
            selected.unselect();

        selected = tileView;

        clearMoves();
        showMoves(validMoves.boardMoves(tileView.position()));
    }

    @Override
    public void executeMove(Move move) {
        if (selected != null) {
            selected.unselect();
        }
        selected = null;
        clearMoves();
        gameState = move.execute(gameState);
        validMoves = new ValidMoves(gameState);
        updateTiles();
    }

    private void updateTiles() {
        for (Node node : getChildren()) {
            TileView tileView = (TileView) node;
            tileView.setTile(gameState.tileAt(tileView.position()));
            tileView.update();
        }

        final Text text_side = new Text(160, 25, gameState.armyOnTurn().side().toString());
        text_side.setFill(Color.BLUEVIOLET);
        text_side.setUnderline(true);
        text_side.setFont(Font.font(java.awt.Font.SERIF, 25));
        text_side.setVisible(true);

        stage = (Stage) this.getScene().getWindow();

        Group root = returnStandardGroup(this,
                                                    gameState,
                                                    gameState.army(PlayingSide.BLUE).stack().size(),
                                                    gameState.army(PlayingSide.ORANGE).stack().size(),
                                                    gameState.army(PlayingSide.BLUE).captured(),
                                                    gameState.army(PlayingSide.ORANGE).captured());
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();


        boolean blueWon = false, orangeWon = false;

        for (Troop troopKilledByBlue : gameState.army(PlayingSide.BLUE).captured()) {
            if (troopKilledByBlue.name().contains("Drake")){
                // Blue wins
                blueWon = true;
                break;
            }
        }

        for (Troop troopKilledByOrange : gameState.army(PlayingSide.ORANGE).captured()) {
            if (troopKilledByOrange.name().contains("Drake")){
                // Orange wins
                orangeWon = true;
                break;
            }
        }

        ValidMoves validMoves_test = new ValidMoves(gameState);
        if (validMoves_test.allMoves().isEmpty()){
            PlayingSide lost_side = gameState.sideOnTurn();
            if (lost_side == PlayingSide.BLUE) {
                orangeWon = true;
            } else {
                blueWon = true;
            }
        }

        if (blueWon || orangeWon) {
            Button button = new Button();
            button.setTranslateX(80);
            button.setTranslateY(350);
            button.setPrefSize(200, 50);
            button.setText("New game");
            button.setOnAction(event -> secondStart());
            button.setFont(Font.font(java.awt.Font.SERIF, 20));

            Button button2 = new Button();
            button2.setTranslateX(300);
            button2.setTranslateY(350);
            button2.setPrefSize(200, 50);
            button2.setText("Menu");
            button2.setOnAction(event -> {
                try {
                    goToMenu();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            button2.setFont(Font.font(java.awt.Font.SERIF, 20));


            final Text text;
            Image img;
            if (orangeWon) {
                text = new Text(100, 100, "Orange won.");
                img = new Image("file:src/assets/orange.png");
            } else {
                text = new Text(100, 100, "Blue won.");
                img = new Image("file:src/assets/blue.png");
            }
            text.setFill(Color.BLACK);
            text.setFont(Font.font(java.awt.Font.SERIF, 40));

            ImageView view = new ImageView(img);
            view.setX(450);
            view.setY(45);
            view.setFitHeight(250);
            view.setPreserveRatio(true);

            Board board = new Board(4);

            PositionFactory positionFactory = board.positionFactory();
            board = board.withTiles(new Board.TileAt(positionFactory.pos(TheDrakeApp.mountainI, TheDrakeApp.mountainJ), BoardTile.MOUNTAIN));
            board = board.withTiles(new Board.TileAt(positionFactory.pos(TheDrakeApp.mountainI2, TheDrakeApp.mountainJ2), BoardTile.MOUNTAIN));
            gameState = new StandardDrakeSetup().startState(board);

            Group newRoot = new Group(button, button2, text, view);

            stage = (Stage) this.getScene().getWindow();
            scene = new Scene(newRoot, 800, 600);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void secondStart() {
        BoardView boardView = new BoardView(gameState);
        Group root = returnStandardGroup(boardView, gameState, 7, 7, new ArrayList<Troop>(), new ArrayList<Troop>());

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);

        stage.setTitle("The Drake");
        stage.show();
    }

    public void goToMenu() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationMain.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        scene.getStylesheets().add("test.css");
        stage.setScene(scene);
        stage.setTitle("You have an amazing smile!");
        stage.show();

        Board board = new Board(4);
        ApplicationController cntlr = fxmlLoader.getController();
        cntlr.gameState = createStartGameState(board);
        cntlr.board = board;
    }

    private GameState createStartGameState(Board board) {
        PositionFactory positionFactory = board.positionFactory();
        board = board.withTiles(new Board.TileAt(positionFactory.pos(TheDrakeApp.mountainI, TheDrakeApp.mountainJ), BoardTile.MOUNTAIN));
        board = board.withTiles(new Board.TileAt(positionFactory.pos(TheDrakeApp.mountainI2, TheDrakeApp.mountainJ2), BoardTile.MOUNTAIN));
        return new StandardDrakeSetup().startState(board);
    }

    private void clearMoves() {
        for (Node node : getChildren()) {
            TileView tileView = (TileView) node;
            tileView.clearMove();
        }
    }

    private void showMoves(List<Move> moveList) {
        for (Move move : moveList)
            tileViewAt(move.target()).setMove(move);
    }

    private TileView tileViewAt(BoardPos target) {
        int index = (3 - target.j()) * 4 + target.i();
        return (TileView) getChildren().get(index);
    }
}
