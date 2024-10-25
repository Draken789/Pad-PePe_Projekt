package com.example.apptestingjavafx;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.awt.image.BufferedImage;
import java.io.Console;
import java.io.File;
import java.io.IOException;

public class HelloController {
    private double _dragX, _dragOX, _dragY, _dragOY;
    private boolean _drag;

    @FXML
    private ImageView imageView;

    public void initialize() {
        System.out.println();
    }

    @FXML
    protected void onSelectImage() {
        OpenFileViaExplorer(imageView);
    }

    @FXML
    protected void onImageDrag(MouseEvent ev) {
        if (!_drag) return;
        imageView.setTranslateX(_dragOX + ev.getScreenX() - _dragX);
        imageView.setTranslateY(_dragOY + ev.getScreenY() - _dragY);
    }

    @FXML
    protected void onImageDragStart(MouseEvent ev) {
        if (!ev.isPrimaryButtonDown()) return;
        _dragX = ev.getScreenX();
        _dragY = ev.getScreenY();
        _dragOX = imageView.getTranslateX();
        _dragOY = imageView.getTranslateY();
        _drag = true;
    }

    @FXML
    protected void onImageDragStop(MouseEvent ev) {
        _drag = false;
    }

    @FXML
    protected void onSaveImage() {
        System.out.println("save image");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save image");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                Image imageToBeSaved = imageView.getImage();
                WritableImage writableImage = new WritableImage((int) imageToBeSaved.getWidth(), (int) imageToBeSaved.getHeight());
                imageView.snapshot(null, writableImage);
                File outputFile = new File(file.getAbsolutePath());
                // finish this part
            }
            catch (Exception ex) {
                System.out.println("Chyba při ukládání obrázku: " + ex.getMessage());
            }
        }
    }

    public static boolean OpenFileViaExplorer(ImageView imageView) {
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
                Image image = new Image(selectedFile.toURI().toString());
                imageView.setImage(image);
                return true;
            }
        }
        catch (Exception e) {
            System.out.println(e);
            return false;
        }

        return false;
    }
}