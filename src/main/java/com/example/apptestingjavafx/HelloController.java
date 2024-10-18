package com.example.apptestingjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    public void initialize() {
        System.out.println();
    }

    @FXML
    protected void onSelectImage() {
        System.out.println(45);
    }
}