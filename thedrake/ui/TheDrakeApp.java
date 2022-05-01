package thedrake.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import myfolder.demo.ApplicationMain;
import thedrake.Board;
import thedrake.BoardTile;
import thedrake.GameState;
import thedrake.PositionFactory;
import thedrake.StandardDrakeSetup;

public class TheDrakeApp extends Application {

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
    }



}
