package ui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Optional;

public class CreateDialog {

    public static Optional<Pair<String, String>> display(Stage parent) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();

        // Set the button types.
        ButtonType createButtonType = new ButtonType("Create", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField dictionaryId = new TextField();
        dictionaryId.setPromptText("Dictionary id");
        TextField openLibraryId = new TextField();
        openLibraryId.setPromptText("Open Library ID");

        grid.add(new Label("DICTIONARY-ID:"), 0, 0);
        grid.add(dictionaryId, 1, 0);
        grid.add(new Label("Open Library ID:"), 0, 1);
        grid.add(openLibraryId, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);

        // Do some validation
        createButton.disableProperty().bind(Bindings.or(dictionaryId.textProperty().isEmpty(),
                openLibraryId.textProperty().isEmpty()));
        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> dictionaryId.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new Pair<>(dictionaryId.getText(), openLibraryId.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        return result;

    }
}
