package com.example.apptestingjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720, false);
        stage.setTitle("PEPE Projekt");
        stage.setScene(scene);
        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.show();
        stage.setMaximized(true);
    }

    @Override
    public void init() {

    }

    public static void main(String[] args) {
        System.out.println("application instantiated");
        launch();
    }
}