package com.example.hangman;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import javafx.util.Pair;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    // variable has same name as id of button, so they're linked
//    public Button button;

    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private Label activeDictionaryLength;
    @FXML
    private Label successPercentage;
    @FXML
    private Label wordFound;
    @FXML
    private TextField letterTextField;
    @FXML
    private ImageView myImageView;
    @FXML
    private MenuItem createMenuItem;

    Image[] myImages = {
            new Image(getClass().getResourceAsStream("hangman1.png")),
            new Image(getClass().getResourceAsStream("hangman2.png")),
            new Image(getClass().getResourceAsStream("hangman3.png")),
            new Image(getClass().getResourceAsStream("hangman4.png")),
            new Image(getClass().getResourceAsStream("hangman5.png")),
            new Image(getClass().getResourceAsStream("hangman6.png")),
    };

    private HangmanGame hangmanGame;
    private DictionaryManager dictionaryManager;

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
        System.out.println("Starting new game...");
        if (dictionaryManager.getActiveDictionary() == null) {
            ErrorDialog.display("Failed to start new game", "Please load a dictionary first");
            return;
        }
        hangmanGame.newGame();
    }

    /*
    User gives dict-id
    look inside media folder for it
    if found initialize app,
    else error message
     */
    @FXML
    private void loadDictionary() {
        List<String> choices = dictionaryManager.getDictionaryChoices();
        Optional<String> result = LoadDialog.display(choices);

        result.ifPresent(letter -> {
            System.out.println("Your choice: " + letter);
            dictionaryManager.setActiveDictionary(letter);
        });
    }

    /**
     * User give dict-id for local file and openId
     * for work to be found, if desc is fine create dict
     * else show error message
     */
    @FXML
    private void createDictionary() {
        Stage root = (Stage) mainBorderPane.getScene().getWindow();
        Optional<Pair<String, String>> result = CreateDialog.display(root);

        result.ifPresent(pairValues -> {
            System.out.println("Dictionary ID=" + pairValues.getKey() + ", Open Library Id=" + pairValues.getValue());
            try {
                dictionaryManager.createDictionary(pairValues.getKey(), pairValues.getValue());
            } catch (UndersizeException e) {
                ErrorDialog.display("Undersize dictionary", e.getMessage());
            } catch (UnbalancedException e) {
                ErrorDialog.display("Unbalanced dictionary", e.getMessage());
            } catch (Exception ex) {
                System.out.println(ex);
                ex.printStackTrace();
                ExceptionDialog.display(ex);
            }
        });
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
        System.out.println("Initializing game...");
        dictionaryManager = new DictionaryManager();
        hangmanGame = new HangmanGame(dictionaryManager);
//        hangmanGame.newGame();
//        Remaining words in dictionary:3
        activeDictionaryLength.textProperty().bind(
                Bindings.concat("Words in active dictionary ")
                        .concat(dictionaryManager.activeDictionaryLengthProperty().asString())
        );
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