package com.example.hangman;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

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
        alert.setHeaderText("Game over, the word was: " + hangmanGame.getWordToFind());
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
        populateRightGridPane();
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

    private void populateRightGridPane() {
        String wordToFind = hangmanGame.getWordToFind();
//        rightGridPane.setPadding(new Insets(10, 10, 10, 10));
//        rightGridPane.setMinSize(300, 300);
//        rightGridPane.setVgap(5);
//        rightGridPane.setHgap(5);
//        for (int i = 0, n = wordToFind.length(); i < n; i++) {
//            char letter = wordToFind.charAt(i);
//            Label letterLabel = new Label(" " + letter + " ");
//            letterLabel.setMinWidth(Region.USE_PREF_SIZE);
//
//            rightGridPane.add(letterLabel, i, 0);
//            System.out.println("Letter" + letter);
//        }

//        ColumnConstraints col1 = new ColumnConstraints();
//        col1.setHgrow(Priority.ALWAYS);
//
//        ColumnConstraints col2 = new ColumnConstraints();
//        col2.setHgrow(Priority.ALWAYS);

//        rightGridPane.getColumnConstraints().addAll(new ColumnConstraints(60), col1,
//                new ColumnConstraints(100), col2);
//
//        rightGridPane.addColumn(0, new Button("col 1"));
//        rightGridPane.addColumn(1, new Button("col 2"));
//        rightGridPane.addColumn(2, new Button("col 3"));
//        rightGridPane.addColumn(3, new Button("col 4"));
        GridPane gridpane = new GridPane();
        int percentage = 100 / wordToFind.length();
        gridpane.setGridLinesVisible(true);
//        ColumnConstraints col1 = new ColumnConstraints();
//        col1.setPercentWidth(25);
//        ColumnConstraints col2 = new ColumnConstraints();
//        col2.setPercentWidth(50);
//        ColumnConstraints col3 = new ColumnConstraints();
//        col3.setPercentWidth(25);

        for (int i = 0, n = wordToFind.length(); i < n; i++) {
            char letter = wordToFind.charAt(i);
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(percentage);

            gridpane.getColumnConstraints().add(col1);

            Button button = new Button(String.valueOf(letter));
            gridpane.addColumn(i, button);
            gridpane.setHalignment(button, HPos.CENTER);

            FlowPane flow = new FlowPane();
            flow.setAlignment(Pos.CENTER);
            flow.setVgap(8);
            flow.setHgap(4);
            flow.setPrefWrapLength(300); // preferred width = 300
            for (int letterAsci = 65; letterAsci <= 90; letterAsci++) {
                Button letterButton = new Button(" " + (char) letterAsci + "");
                flow.getChildren().add(letterButton);
                int finalI = i;
                int finalLetterAsci = letterAsci;
                letterButton.setOnAction(e -> {
                    System.out.println("Clicked column: " + finalI + ", and letter: " + (char) finalLetterAsci);
                });
            }
            gridpane.add(flow, i, 1);
        }
//        gridpane.getColumnConstraints().addAll(col1, col2, col3);

//        gridpane.addColumn(0, new Button("col 1"));
//        gridpane.addColumn(1, new Button("col 2"));
//        gridpane.addColumn(2, new Button("col 3"));

        Insets insets = new Insets(0, 20, 0, 20);
        BorderPane.setMargin(gridpane, insets);
        gridpane.setStyle("-fx-background-color: #D8BFD8;");
        mainBorderPane.setRight(gridpane);

        gridpane.setPrefSize(2000, 2000); // Default width and height
//        gridpane.setPrefWidth(mainBorderPane.widthProperty().getValue());
        gridpane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
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