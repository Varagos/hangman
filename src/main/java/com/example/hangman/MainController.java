package com.example.hangman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    // variable has same name as id of button, so they're linked
//    public Button button;

    @FXML
    public Label successPercentage;
    public Label wordFound;
    public TextField letterTextField;
    public ImageView myImageView;

    Image[] myImages = {
            new Image(getClass().getResourceAsStream("hangman1.png")),
            new Image(getClass().getResourceAsStream("hangman2.png")),
            new Image(getClass().getResourceAsStream("hangman3.png")),
            new Image(getClass().getResourceAsStream("hangman4.png")),
            new Image(getClass().getResourceAsStream("hangman5.png")),
            new Image(getClass().getResourceAsStream("hangman6.png")),
    };

    private HangmanGame hangmanGame;

    public void handleButtonClick(ActionEvent event) {
        String str = letterTextField.getText();
        letterTextField.clear();
        hangmanGame.handleNewLetter(str.charAt(0));
    }

    private void endGameAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Oops");
        alert.setContentText("You have made 6 mistakes");
        alert.setHeaderText("Game over");
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // runs after view loads,
        System.out.println("Loading user data...");
        hangmanGame = new HangmanGame();
        hangmanGame.newGame();
        wordFound.textProperty().bind(hangmanGame.wordFoundProperty());
//        hangmanGame.play();
        successPercentage.setText("dummy");
        successPercentage.textProperty().bind(hangmanGame.successPercentageProperty().asString());
        hangmanGame.nbErrorsProperty().addListener((v, oldValue, newValue) -> {
            System.out.println("newValue " + newValue.toString());
            int index = newValue.intValue() - 1;
            myImageView.setImage(myImages[index]);
            if (newValue.intValue() == 6) {
                endGameAlert();
            }
        });
    }
}