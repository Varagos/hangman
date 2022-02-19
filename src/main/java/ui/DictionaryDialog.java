package ui;

import application.DictionaryManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.util.Iterator;
import java.util.Set;

public class DictionaryDialog {
    public static void display(DictionaryManager dictionaryManager) {
        if (dictionaryManager.getActiveDictionary() == null) {
            DictionaryDialog.displayError();
            return;
        }
        Set<String> activeWords = dictionaryManager.getActiveDictWords();
        // percentage of words based on number of letters
        double sixPerc = DictionaryDialog.sixLetterWordsPercentage(activeWords);
        double sevenToNinePerc = DictionaryDialog.sevenToNineLetterWordsPercentage(activeWords);
        double tenPlusPerc = DictionaryDialog.tenPlusLetterWordsPercentage(activeWords);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Percentage of words by letter length");

        Label label1 = new Label("6 letters words");
        Label label2 = new Label("7-9 letters words");
        Label label3 = new Label("10+ letters words");
        label1.getStyleClass().setAll("lbl", "lbl-default", "h3");
        label2.getStyleClass().setAll("lbl", "lbl-default", "h3");
        label3.getStyleClass().setAll("lbl", "lbl-default", "h3");

        Label perc1 = new Label(String.format("%.2f", sixPerc * 100) + " %");
        Label perc2 = new Label(String.format("%.2f", sevenToNinePerc * 100) + " %");
        Label perc3 = new Label(String.format("%.2f", tenPlusPerc * 100) + " %");
        perc1.getStyleClass().setAll("lbl", "lbl-info", "h1");
        perc2.getStyleClass().setAll("lbl", "lbl-info", "h1");
        perc3.getStyleClass().setAll("lbl", "lbl-info", "h1");

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
        gridPane.add(label1, 0, 0);
        gridPane.add(perc1, 1, 0);
        gridPane.add(label2, 0, 1);
        gridPane.add(perc2, 1, 1);
        gridPane.add(label3, 0, 2);
        gridPane.add(perc3, 1, 2);

        alert.getDialogPane().setContent(gridPane);

        alert.getDialogPane().getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        alert.showAndWait();
    }

    static void displayError() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("No Active dictionary");
        alert.setContentText("Ooops, you need to load a dictionary first!");

        alert.showAndWait();
    }

    /**
     * Returns percentage of six-letter words
     *
     * @param words
     * @return
     */
    static double sixLetterWordsPercentage(Set<String> words) {
        Iterator<String> iterator = words.iterator();
        int instances = 0;
        while (iterator.hasNext()) {
            if (iterator.next().length() == 6) instances++;
        }
        return (double) instances / words.size();
    }

    static double sevenToNineLetterWordsPercentage(Set<String> words) {
        Iterator<String> iterator = words.iterator();
        int instances = 0;
        while (iterator.hasNext()) {
            String word = iterator.next();
            if (word.length() >= 7 && word.length() <= 9) instances++;
        }
        return (double) instances / words.size();
    }

    static double tenPlusLetterWordsPercentage(Set<String> words) {
        Iterator<String> iterator = words.iterator();
        int instances = 0;
        while (iterator.hasNext()) {
            if (iterator.next().length() >= 10) instances++;
        }
        return (double) instances / words.size();
    }
}
