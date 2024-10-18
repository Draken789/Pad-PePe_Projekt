package com.example.apptestingjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class HelloController {
    @FXML
    private Label welcomeText;

    public void initialize() {
        System.out.println();
    }

    @FXML
    protected void onSelectImage() {
        OpenFileViaExplorer();
    }

    public static boolean OpenFileViaExplorer() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "png", "jpg");
            fileChooser.setFileFilter(filter);
            fileChooser.setCurrentDirectory(new File("."));
            int result = fileChooser.showOpenDialog(null);
            System.out.println("Result: " + result);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println("Filepath: " + selectedFile);
            }
        }
        catch (Exception e) {
            System.out.println(e);
            return false;
        }

        return false;
    }
}