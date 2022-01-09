package com.example.hangman;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Optional;

public class ErrorDialog {

    public static void display(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(title);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
