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

        // Calculate new position
        double newTranslateX = _dragOX + ev.getScreenX() - _dragX;
        double newTranslateY = _dragOY + ev.getScreenY() - _dragY;

        // Get window boundaries
        double windowWidth = imageView.getScene().getWidth();
        double windowHeight = imageView.getScene().getHeight();

        // Get image dimensions
        double imageWidth = imageView.getBoundsInParent().getWidth();
        double imageHeight = imageView.getBoundsInParent().getHeight();

        // Clamp the new position to prevent moving outside the window
        if (newTranslateX < 0) {
            newTranslateX = 0;
        } else if (newTranslateX + imageWidth > windowWidth) {
            newTranslateX = windowWidth - imageWidth;
        }

        if (newTranslateY < 0) {
            newTranslateY = 0;
        } else if (newTranslateY + imageHeight > windowHeight) {
            newTranslateY = windowHeight - imageHeight;
        }

        // Set the new constrained position
        imageView.setTranslateX(newTranslateX);
        imageView.setTranslateY(newTranslateY);
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
                // Get the current image from the ImageView
                Image imageToBeSaved = imageView.getImage();

                // Create a WritableImage matching the size of the original image
                WritableImage writableImage = new WritableImage((int) imageToBeSaved.getWidth(), (int) imageToBeSaved.getHeight());

                // Snapshot the ImageView content to the WritableImage
                imageView.snapshot(null, writableImage);

                // Convert WritableImage to BufferedImage
                BufferedImage bufferedImage = javafx.embed.swing.SwingFXUtils.fromFXImage(writableImage, null);

                // Write the BufferedImage to the chosen file
                ImageIO.write(bufferedImage, "png", file);

                System.out.println("Image saved successfully to " + file.getAbsolutePath());
            } catch (Exception ex) {
                System.out.println("Error while saving the image: " + ex.getMessage());
                ex.printStackTrace();
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
                imageView.setFitWidth(image.getWidth());
                if (imageView.getFitWidth() < image.getWidth() ) {
                    imageView.setFitHeight(image.getHeight())
                }
                imageView.setFitHeight(image.getHeight());
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