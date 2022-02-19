package application;

import controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class Main extends Application {
    Stage window;
    Button button;

    public static void main(String[] args) {
        // Starting point, invokes start when ready
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Create the FXMLLoader
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/mainView" +
                ".fxml"));
        Parent root = loader.load();

        MainController controller = loader.getController();
        controller.setStageAndSetupListeners(primaryStage);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/download" +
                ".png")));

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        // import BootstrapFX
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        // Set the Scene to the Stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Medialab Hangman");
        primaryStage.setMaximized(true);

        primaryStage.show();
    }
}