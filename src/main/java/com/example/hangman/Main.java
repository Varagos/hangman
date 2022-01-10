package com.example.hangman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.controlsfx.control.spreadsheet.Grid;

import java.io.FileInputStream;
import java.io.IOException;

public class Main extends Application {
    Stage window;
    Button button;

    public static void main(String[] args) {
        /**
         * Starting point, it is going into Application
         * it will set everything up
         * and then its gonna call start
         */
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Create the FXMLLoader
//        FXMLLoader loader = new FXMLLoader();
        Parent root = FXMLLoader.load(getClass().getResource("mainView.fxml"));

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("Styles.css").toExternalForm());
        // Set the Scene to the Stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Hangman game");
        primaryStage.setMaximized(true);

        primaryStage.show();
    }
}