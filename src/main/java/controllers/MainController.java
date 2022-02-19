package controllers;

import application.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.kordamp.bootstrapfx.scene.layout.Panel;
import ui.*;

import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    // variable has the same name as id, so they're linked

    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private VBox leftVBox;
    @FXML
    private GridPane lowerGridPane;
    @FXML
    private HBox wordContainer;
    @FXML
    private VBox rightVBox;
    @FXML
    private Label activeDictionaryLength;
    @FXML
    private Label gameTotalPoints;
    @FXML
    private Label successPercentage;
    @FXML
    private Label triesRemaining;
    @FXML
    private ImageView myImageView;

    Image[] myImages = {
            new Image(getClass().getResourceAsStream("/images/hangman1.png")),
            new Image(getClass().getResourceAsStream("/images/hangman2.png")),
            new Image(getClass().getResourceAsStream("/images/hangman3.png")),
            new Image(getClass().getResourceAsStream("/images/hangman4.png")),
            new Image(getClass().getResourceAsStream("/images/hangman5.png")),
            new Image(getClass().getResourceAsStream("/images/hangman6.png")),
    };

    private HangmanGame hangmanGame;
    private DictionaryManager dictionaryManager;

    public void setStageAndSetupListeners(Stage stage) {
        lowerGridPane.prefWidthProperty().bind(stage.widthProperty());
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
//        populateRightGridPane();
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

    /**
     * A popup shows for active dictionary,
     * percentage of words with 6, 7-9 and 10+ words
     */
    @FXML
    private void showDictionaryDetails() {
        System.out.println("showDictionaryDetails");
        DictionaryDialog.display(dictionaryManager);
    }

    /**
     * A popup shows for last 5 finished games,
     * selected word, number of tries,
     * and winner (computer or human)
     */
    @FXML
    private void showLastRoundsInfo() {
        System.out.println("showLastRoundsInfo");
        RoundsDialog.display(this.hangmanGame);
    }

    /**
     * Game is registered as lost, and word is reveled
     */
    @FXML
    private void solveGame() {
        System.out.println("solveGame");
    }

    private void initializeTopSection() {
        activeDictionaryLength.textProperty().bind(
                Bindings.concat("Words in active dictionary ")
                        .concat(dictionaryManager.activeDictionaryLengthProperty().asString())
        );
        gameTotalPoints.textProperty().bind(
                Bindings.concat("Total points ")
                        .concat(hangmanGame.totalPointsProperty().asString())
        );

        successPercentage.textProperty().bind(
                Bindings.concat("Success Percentage ")
                        .concat(Bindings.format("%.2f", hangmanGame.successPercentageProperty())));

    }

    private void populateRightGridPane(int letterIndex) {
        String wordToFind = hangmanGame.getWordToFind();
        Label letter = new Label("letter index" + letterIndex);
        rightVBox.getChildren().setAll(letter);

//        Panel panel = new Panel("This is the title");
//        panel.getStyleClass().add("panel-primary");                            //(2)
        Stage root = (Stage) mainBorderPane.getScene().getWindow();
        rightVBox.setPrefWidth(root.getWidth() / 2);
        rightVBox.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        rightVBox.setStyle("-fx-border-color: black");

        // TODO Calculate view lists on word change event and not on letter click listener
        // (pre-calculate)
        List<Map<Integer, ArrayList<Character>>> indexLetterProbs =
                hangmanGame.getPositionLetterProbs();
        Map<Integer, ArrayList<Character>> currentIndexLetterProbs =
                indexLetterProbs.get(letterIndex);
        if (currentIndexLetterProbs == null) {
            System.out.println("ERROR null map for index:" + letterIndex);
            return;
        }
        List<Integer> pointKeys =
                new ArrayList<>(currentIndexLetterProbs.keySet());
        Collections.sort(pointKeys);

        for (Integer points : pointKeys) {
            List<Character> pointChars = currentIndexLetterProbs.get(points);
//            System.out.println("Key = " + entry.getKey() +
//                    ", Value = " + entry.getValue());
            Panel panel = new Panel(points + " Points letters");
            panel.getStyleClass().add("panel-primary");                            //(2)
            FlowPane panelBody = new FlowPane();
            for (Character pointsLetter : pointChars) {
                Label letterLabel = new Label(String.valueOf(pointsLetter));
                letterLabel.getStyleClass().setAll("lbl", "lbl-info", "p");
                panelBody.getChildren().add(letterLabel);
            }
            panel.setBody(panelBody);
            rightVBox.getChildren().add(panel);
        }


//        mainBorderPane.setRight(panel);

//        gridpane.setPrefSize(2000, 2000); // Default width and height
//        gridpane.setPrefWidth(mainBorderPane.widthProperty().getValue());
//        gridpane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
    }

    private void updateWordToggleButtons(String newWordFound) {
        final ToggleGroup group = new ToggleGroup();
        ToggleButton[] letterButtons = new ToggleButton[newWordFound.length()];
        for (int i = 0; i < newWordFound.length(); i++) {
            letterButtons[i] = new ToggleButton(newWordFound.substring(i, i + 1));
            letterButtons[i].setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
            letterButtons[i].getStyleClass().setAll("btn", "btn-warning");
            //btn, btn-primary, first
            letterButtons[i].setToggleGroup(group);
            if (newWordFound.charAt(i) != '⏡') {
                letterButtons[i].setDisable(true);
            }
        }
        wordContainer.getChildren().setAll(letterButtons);
        wordContainer.getStyleClass().setAll("btn-group-horizontal");
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            int index = group.getToggles().indexOf(group.getSelectedToggle());
            System.out.println("index clicked!: " + index);
            if (index == -1) {
                lowerGridPane.getChildren().clear();
                System.out.println("TOGGLE GROUP UNSELECTED!!");
            } else {
                populateButtonForm(index);
                populateRightGridPane(index);
            }
        });

    }

    private void populateButtonForm(final int letterIndex) {
        System.out.println("Populating Button form");
        List<Set<Character>> wrongLettersForPositions = hangmanGame.getPositionEnteredLetters();
        Set currentPositionWrongLetters = wrongLettersForPositions.get(letterIndex);

        lowerGridPane.getChildren().clear();
        lowerGridPane.setHgap(10);
        lowerGridPane.setVgap(10);

        for (int letterAscii = 65; letterAscii <= 90; letterAscii++) {
            // 26 letters, 13 per row
            int row = letterAscii >= 78 ? 1 : 0;
            int col = (letterAscii - 65) % 13;
            final char finalLetterAscii = (char) letterAscii;
            Button letterButton = new Button(" " + finalLetterAscii + " ");
            letterButton.getStyleClass().setAll("btn", "btn-danger", "btn-sm");                     //(2)
            lowerGridPane.add(letterButton, col, row);

            GridPane.setHalignment(letterButton, HPos.CENTER); // To align horizontally in the cell
            GridPane.setValignment(letterButton, VPos.CENTER); // To align vertically in the cell
            letterButton.setAlignment(Pos.CENTER);
            if (currentPositionWrongLetters.contains(finalLetterAscii)) {
                // TODO fix it's not disabled
                letterButton.setDisable(true);
            } else {
                setFormLetterListener(letterButton, letterIndex, finalLetterAscii);
            }
        }
    }

    private void setFormLetterListener(Button letterButton, int letterIndex, char letterValue) {
        letterButton.setOnAction(e -> {
            System.out.println("Clicked column: " + letterIndex + ", and letter: " + letterValue);
            boolean result = hangmanGame.handleNewLetter(letterIndex, letterValue);
            if (result) lowerGridPane.getChildren().clear();
            // disable buutton based on result instead of rerender all buttons?
//          myButton.setEnabled(false);
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // runs after view loads,
        System.out.println("Loading user data...");
        System.out.println("Initializing game...");

        dictionaryManager = new DictionaryManager();
        hangmanGame = new HangmanGame(dictionaryManager);

        this.initializeTopSection();

        triesRemaining.textProperty().bind(
                Bindings.concat("Guesses remaining: ")
                        .concat(Bindings.createIntegerBinding(() -> HangmanGame.MAX_ERRORS).subtract(hangmanGame.nbErrorsProperty()))
        );

        hangmanGame.nbErrorsProperty().addListener((v, oldValue, newValue) -> {
            System.out.println("newValue " + newValue.toString());
            if (newValue.intValue() == 0) {
                // game has restarted
                System.out.println("0 errors, setting image...");
                myImageView.setImage(myImages[0]);
                return;
            }
            int index = newValue.intValue() - 1;
            System.out.println(newValue.intValue() - 1 + " errors, setting image...");
            myImageView.setImage(myImages[index]);
            if (newValue.intValue() == 6) {
                endGameAlert();
            }
        });

        hangmanGame.wordFoundProperty().addListener((o, oldValue, newValue) -> {
            System.out.println("wordFound event listener triggered");
            System.out.println("newValue " + newValue);
            updateWordToggleButtons(newValue);
        });

    }
    /**
     * TODO add change listener on letter string property
     */
}
