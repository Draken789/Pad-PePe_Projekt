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
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("");
        stage.setScene(scene);
        stage.setMinWidth(0);
        stage.setMinHeight(540);
        stage.show();
        stage.setMaximized(true);
    }

    @Override
    public void init() throws Exception {

    }

    public static void main(String[] args) {
        launch();
    }
}