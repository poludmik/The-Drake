package myfolder.demo;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import thedrake.*;
import thedrake.ui.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;


public class ApplicationController {

    @FXML
    private Label welcomeText;

    @FXML private javafx.scene.control.Button closeButton;

    @FXML
    private Label welcumText;

    public GameState gameState;
    public Board board;


    @FXML
    public void playerVSplayerClick() {
        BoardView boardView = new BoardView(gameState);

        Group root = returnStandardGroup(boardView, gameState, 7, 7, new ArrayList<Troop>(), new ArrayList<Troop>());

        Stage stage = (Stage) closeButton.getScene().getWindow();

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);

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

    public static Group returnStandardGroup(BoardView boardView,
                                            GameState gameState,
                                            int stackSizeB,
                                            int stackSizeO,
                                            List<Troop> killedByB,
                                            List<Troop> killedByO){

        final Text text = new Text(25, 25, "Playing side:");
        text.setFill(Color.BLACK);
//        text.setUnderline(true);
        text.setFont(Font.font(java.awt.Font.SERIF, 25));

        final Text text_side = new Text(160, 25, gameState.armyOnTurn().side().toString());
        text_side.setFill(Color.BLACK);
        text_side.setUnderline(true);
        text_side.setFont(Font.font(java.awt.Font.SERIF, 25));

        boardView.setLayoutX(340);
        boardView.setLayoutY(80);

        Image img = new Image("file:src/assets/all_blue.png");
        ImageView view = new ImageView(img);
        view.setX(390);
        view.setY(545);
        view.setFitHeight(50);
        view.setPreserveRatio(true);

        Image img2 = new Image("file:src/assets/all_orange.png");
        ImageView view2 = new ImageView(img2);
        view2.setX(390);
        view2.setY(10);
        view2.setFitHeight(50);
        view2.setPreserveRatio(true);

        Button button = new Button();
        button.setTranslateX(80);
        button.setTranslateY(275);
        button.setPrefSize(200, 50);
        button.setText("Place from stack");
        button.setOnAction(event -> boardView.onPress());
        button.setFont(Font.font(java.awt.Font.SERIF, 20));

        int width = 50;
        int height = 50;

        Rectangle rectangleO = new Rectangle(390, 10, width * (7 - stackSizeO) + 1, height);
        Rectangle rectangleB = new Rectangle(390, 545, width * (7 - stackSizeB), height);
        rectangleB.setFill(Color.WHITE);
        rectangleO.setFill(Color.WHITE);

        final Text text_killed_o = new Text(30, 70, "Orange army killed:");
        text_killed_o.setFill(Color.BLACK);
        text_killed_o.setUnderline(true);
        text_killed_o.setFont(Font.font(java.awt.Font.SERIF, 20));

        final Text text_killed_b = new Text(30, 360, "Blue army murdered:");
        text_killed_b.setFill(Color.BLACK);
        text_killed_b.setUnderline(true);
        text_killed_b.setFont(Font.font(java.awt.Font.SERIF, 20));

        StringBuilder strKilledByO = new StringBuilder();
        for (Troop troop : killedByO) {
            strKilledByO.append(troop.name()).append("\n");
        }
        final Text killed_string_o = new Text(30, 92, strKilledByO.toString());
        killed_string_o.setFill(Color.BLACK);
        killed_string_o.setFont(Font.font(java.awt.Font.SERIF, 20));

        int shift = 0;
        StringBuilder strKilledByB = new StringBuilder();
        for (Troop troop : killedByB) {
            strKilledByB.append(troop.name()).append("\n");
        }
        final Text killed_string_b = new Text(30, 382, strKilledByB.toString());
        killed_string_b.setFill(Color.BLACK);
        killed_string_b.setFont(Font.font(java.awt.Font.SERIF, 20));

        return new Group(boardView, text, text_side, view, view2, button, rectangleO, rectangleB, text_killed_o, text_killed_b, killed_string_b, killed_string_o);
    }

}