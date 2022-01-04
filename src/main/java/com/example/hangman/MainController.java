package com.example.hangman;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    // variable has same name as id of button, so they're linked
//    public Button button;

    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private Label successPercentage;
    @FXML
    private Label wordFound;
    public TextField letterTextField;
    public ImageView myImageView;
    public MenuItem createMenuItem;

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

    /**
     * Start application based on loaded dict
     * if there is ongoing round clean its state
     */
    @FXML
    private void startApp() {

    }

    /*
    User gives dict-id
    look inside media folder for it
    if found initialize app,
    else error message
     */
    @FXML
    private void loadDictionary() {
        System.out.println("hi");
    }

    /**
     * User give dict-id for local file and openId
     * for work to be found, if desc is fine create dict
     * else show error message
     */
    @FXML
    private void createDictionary() {
        Stage root = (Stage) mainBorderPane.getScene().getWindow();
        System.out.println("hi");
        CreateDialog.display(root);
    }

    @FXML
    private void exitApplication() {
        System.out.println("Exiting application...");
        Platform.exit();
        System.exit(0);
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