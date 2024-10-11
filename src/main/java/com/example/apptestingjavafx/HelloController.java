package com.example.apptestingjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class HelloController {
    @FXML
    private TextArea log;

    @FXML
    private ImageView matrixImg;

    public void initialize() {
        log.setText("KOPJWMDKOLDW");
        // matrixImg.setImage(new Image("C:\\Users\\Draken\\Pictures\\Screenshots\\nvidia-studio-canvas-app-icon-120-d.png"));
    }
}