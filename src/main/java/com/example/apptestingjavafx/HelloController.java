package com.example.apptestingjavafx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;

//about dialog imports
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;


public class HelloController {
    private double dragStartX, dragStartY;
    private double originalX, originalY;
    private boolean isDragging = false;

    @FXML
    private ImageView imageView;

    @FXML
    private Pane imageContainer;

    @FXML
    private VBox vBoxRightOptions;

    @FXML
    private HBox heightBoxViewPortImage;

    @FXML
    protected void onImageDrag(MouseEvent event) {
        if (!isDragging) return;

        double deltaX = event.getSceneX() - dragStartX;
        double deltaY = event.getSceneY() - dragStartY;

        double newX = originalX + deltaX;
        double newY = originalY + deltaY;

        // Get container boundaries
        double containerWidth = imageContainer.getWidth();
        double containerHeight = imageContainer.getHeight();

        // Get image dimensions
        double imageWidth = imageView.getFitWidth();
        double imageHeight = imageView.getFitHeight();

        // Constrain movement within container bounds
        newX = Math.max(0, Math.min(newX, containerWidth - imageWidth));
        newY = Math.max(0, Math.min(newY, containerHeight - imageHeight));

        imageView.setLayoutX(newX);
        imageView.setLayoutY(newY);
    }

    @FXML
    protected void onImageDragStart(MouseEvent event) {
        if (!event.isPrimaryButtonDown()) return;

        dragStartX = event.getSceneX();
        dragStartY = event.getSceneY();
        originalX = imageView.getLayoutX();
        originalY = imageView.getLayoutY();
        isDragging = true;

        event.consume();
    }

    @FXML
    protected void onImageDragStop(MouseEvent event) {
        isDragging = false;
        event.consume();
    }

    @FXML
    protected void onAboutClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("about-dialog.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 300, 200);
            Stage stage = new Stage();
            stage.setTitle("About");
            stage.setScene(scene);

            // Set the window to be modal (blocks input to other windows)
            stage.initModality(Modality.APPLICATION_MODAL);

            // Set the window to be non-resizable
            stage.setResizable(false);

            stage.show();
        } catch (Exception e) {
            System.out.println("Error opening about dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void onSaveImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save image");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                Image imageToBeSaved = imageView.getImage();
                WritableImage writableImage = new WritableImage(
                        (int) imageToBeSaved.getWidth(),
                        (int) imageToBeSaved.getHeight()
                );

                imageView.snapshot(null, writableImage);
                BufferedImage bufferedImage = javafx.embed.swing.SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(bufferedImage, "png", file);

                System.out.println("Image saved successfully to " + file.getAbsolutePath());
            } catch (Exception ex) {
                System.out.println("Error while saving the image: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    @FXML
    protected void onSelectImage() {
        if (OpenFileViaExplorer(imageView)) {
            // Reset position to top-left corner
            imageView.setLayoutX(0);
            imageView.setLayoutY(0);

            // Add listener to image loading
            imageView.imageProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    Platform.runLater(this::resizeImage);
                }
            });
        }
    }

    private void resizeImage() {
        if (imageView.getImage() != null) {
            double containerWidth = imageContainer.getWidth() - 20;
            double containerHeight = imageContainer.getHeight() - 20;

            double imageWidth = imageView.getImage().getWidth();
            double imageHeight = imageView.getImage().getHeight();

            double widthRatio = containerWidth / imageWidth;
            double heightRatio = containerHeight / imageHeight;
            double scale = Math.min(widthRatio, heightRatio);
            scale = Math.min(scale, 1.0);

            imageView.setFitWidth(imageWidth * scale);
            imageView.setFitHeight(imageHeight * scale);

            // Position image in top-left corner
            imageView.setLayoutX(0);
            imageView.setLayoutY(0);
        }
    }

    public void initialize() {
        // Add listeners for container size changes
        imageContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (imageView.getImage() != null) {
                Platform.runLater(this::resizeImage);
            }
        });

        imageContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (imageView.getImage() != null) {
                Platform.runLater(this::resizeImage);
            }
        });

        imageView.imageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Platform.runLater(this::resizeImage);
            }
        });
    }

    public static boolean OpenFileViaExplorer(ImageView imageView) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Image files", "png", "jpg", "jpeg", "bmp", "gif");
            fileChooser.setFileFilter(filter);
            fileChooser.setCurrentDirectory(new File("."));

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                Image image = new Image(selectedFile.toURI().toString());
                imageView.setImage(image);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}