package myfolder.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ApplicationController {
    @FXML
    private Label welcomeText;

    @FXML
    private Label welcumText;

    private boolean pressed = false;

    @FXML private javafx.scene.control.Button closeButton;

    @FXML
    protected void playerVSplayerClick() {
        welcomeText.setText("Let's roll.");
    }

    @FXML
    protected void internetButtonClick() {
        welcomeText.setText("no internet for today, sorry.");
    }

    @FXML
    protected void pcButton() {
        if (!pressed) {
            welcumText.setText("Good day to die!");
            pressed = true;
        }
        else {
            welcumText.setText("Good day to live!");
            pressed = false;
        }
    }

    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}