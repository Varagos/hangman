package ui;

import application.HangmanGame;
import ui.GameResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.util.LinkedList;

public class RoundsDialog {
    public static void display(HangmanGame hangmanGame) {
        LinkedList<GameResult> lastGames = hangmanGame.getLast5Games();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Last 5 Games");

        Label col1 = new Label("word");
        Label col2 = new Label("number of errors");
        Label col3 = new Label("winner");
        col1.getStyleClass().setAll("lbl", "lbl-default", "h1");
        col2.getStyleClass().setAll("lbl", "lbl-default", "h1");
        col3.getStyleClass().setAll("lbl", "lbl-default", "h1");

        //Creating a Grid Pane
        GridPane gridPane = new GridPane();

        //Setting size for the pane
        gridPane.setMinSize(400, 200);

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        //Arranging all the nodes in the grid
        gridPane.add(col1, 0, 0);
        gridPane.add(col2, 1, 0);
        gridPane.add(col3, 2, 0);

        for (int num = 0; num < lastGames.size(); num++) {
            GameResult game = lastGames.get(num);
            Label word = new Label(game.selectedWord);
            Label numberOfTries = new Label(String.valueOf(game.numberOfTries));
            Label winner = new Label(game.winner.toString());
            word.getStyleClass().setAll("lbl", "lbl-info", "h3");
            numberOfTries.getStyleClass().setAll("lbl", "lbl-info", "h3");
            winner.getStyleClass().setAll("lbl", "lbl-info", "h3");
            gridPane.add(word, 0, num + 1);
            gridPane.add(numberOfTries, 1, num + 1);
            gridPane.add(winner, 2, num + 1);
        }

        alert.getDialogPane().setContent(gridPane);

        alert.getDialogPane().getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        alert.showAndWait();
    }
}
