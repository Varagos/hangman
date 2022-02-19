module com.example.hangman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.net.http;
    requires org.json;

    exports controllers;
    opens controllers to javafx.fxml;
    exports application;
    opens application to javafx.fxml;
    exports ui;
    opens ui to javafx.fxml;
}