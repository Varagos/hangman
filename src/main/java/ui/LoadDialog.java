package ui;

import javafx.scene.control.ChoiceDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoadDialog {
    public static Optional<String> display(List<String> choices) {

        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, choices);
        dialog.setTitle("Choice Dialog");
        dialog.setHeaderText("Look, a Choice Dialog");
        dialog.setContentText("Choose your letter:");

// Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
//        if (result.isPresent()) {
//            System.out.println("Your choice: " + result.get());
//        }
        return result;
    }
}
