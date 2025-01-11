package com.example.apptestingjavafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.Node;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.event.ActionEvent;

import java.awt.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.ArrayList;
import javafx.scene.control.TextArea;


//about dialog imports
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;


public class HelloController {
    private MessageHandler messageHandler;
    private double dragStartX, dragStartY;
    private double originalX, originalY;
    private boolean isDragging = false;

    private Image srcImg;
    private WritableImage dstImg;
    private int imgWidth;
    private int imgHeight;
    private Image origImg;

    @FXML
    private ImageView imageView;

    @FXML
    private Pane imageContainer;

    @FXML
    private VBox vBoxRightOptions;

    @FXML
    private HBox heightBoxViewPortImage;


    @FXML
    private  TextArea messagesTextArea;

    protected void setMessagesTextArea() {
        ArrayList<String> messages = messageHandler.getMessages();
        String text = "";

        for (String message : messages) {
            text += message + "\n";
        }
        messagesTextArea.setText(text);
    }

    protected void addMessageInTextArea(String text) {
        messageHandler.addMessage(text);
        setMessagesTextArea();
    }

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
    protected void onGrayscale() {
        var pr = srcImg.getPixelReader();
        var pw = dstImg.getPixelWriter();
        for (int x = 0; x < srcImg.getWidth(); x++) {
            for (int y = 0; y < srcImg.getHeight(); y++) {
                int argb = pr.getArgb(x, y);
                int a = argb & 0xFF000000;
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;
                int v = (int)(0.21 * (double)r + 0.71 * (double)g + 0.07 * b);

                //0.21 * (double)this.red + 0.71 * (double)this.green + 0.07 * (double)this.blue
                pw.setArgb(x, y, a | (v << 16) | (v << 8) | v);

            }
        }
        flushImage();
    }

    @FXML
    protected void onUngrayscale() {
        double coefR = 1/0.21;
        double coefG = 1/0.71;
        double coefB = 1/0.07;

        var pr = srcImg.getPixelReader();
        var pw = dstImg.getPixelWriter();
        for (int x = 0; x < srcImg.getWidth(); x++) {
            for (int y = 0; y < srcImg.getHeight(); y++) {
                int argb = pr.getArgb(x, y);
                int a = argb & 0xFF000000;
                int v = argb & 0xFF;

                //0.21 * (double)this.red + 0.71 * (double)this.green + 0.07 * (double)this.blue
                pw.setArgb(x, y, a
                        | (int)(coefR * (v << 16))
                        | (int)(coefG * (v << 8))
                        | (int)(coefB * v));

            }
        }
        flushImage();
    }

    protected void flushImage() {
        imageView.setImage(dstImg);
        srcImg = dstImg;
        dstImg = new WritableImage(imgWidth, imgHeight);
    }

    protected void blitImage() {
        dstImg.getPixelWriter().setPixels(0, 0, imgWidth, imgHeight, srcImg.getPixelReader(), 0, 0);
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
/*                                           MENU ITEM ABOUT                  */

    @FXML
    protected void onExitClick(ActionEvent event) {
        System.out.println("exit");
        javafx.stage.Window.getWindows().stream()
                .filter(javafx.stage.Window::isShowing)
                .findFirst()
                .map(window -> (Stage) window)
                .ifPresent(Stage::close);
    }

    @FXML
    protected void onAboutClick() {
        try {
            System.out.println("ajsdsakjd");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("about-dialog.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 200);
            Stage stage = new Stage();
            stage.setTitle("About");
            stage.setScene(scene);

            // Set the stage as modal and non-resizable
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setX(0);
            stage.setY(2);

            // this will be displayed after the window is rendered
            Platform.runLater(() -> {
                stage.setOpacity(1.0);
                stage.toFront();
            });

            stage.show();
        } catch (IOException e) {
            // Handle any IO exceptions
            e.printStackTrace();
        }
    }

    @FXML
    protected void onSaveImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                Image imageToBeSaved = imageView.getImage();
                if (imageToBeSaved == null) {
                    System.out.println("No image loaded to save.");
                    return;
                }


                BufferedImage bufferedImage = javafx.embed.swing.SwingFXUtils.fromFXImage(imageToBeSaved, null);

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
        Image result = OpenFileViaExplorer();
        if (result != null) {
            imgWidth = (int) result.getWidth();
            imgHeight = (int) result.getHeight();
            dstImg = new WritableImage(result.getPixelReader(), imgWidth, imgHeight);
            srcImg = result;
            origImg = result;

            // Reset position to top-left corner
            imageView.setLayoutX(0);
            imageView.setLayoutY(0);
            imageView.setImage(result);

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
        messageHandler = new MessageHandler();
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
        addMessageInTextArea("Application started successfully.");
    }

    public static Image OpenFileViaExplorer() {
        try {
            JFileChooser fileChooser = new JFileChooser("./src/main/resources/com/example/apptestingjavafx/img");
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Image files", "png", "jpg", "jpeg", "bmp", "gif");
            fileChooser.setFileFilter(filter);

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                return new Image(selectedFile.toURI().toString());
            }
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    protected void onRestoreOriginalImage() {
        if (origImg != null) {
            imageView.setImage(origImg);
            System.out.println("Original image restored.");
        } else {
            System.out.println("No original image to restore.");
        }
    }

    @FXML
    protected void onInvertColors() {
        if (srcImg == null) {
            System.out.println("No image loaded to invert colors.");
            return;
        }

        var writer = dstImg.getPixelWriter();
        var reader = srcImg.getPixelReader();
        for (int x = 0; x < imgWidth; x++) {
            for (int y = 0; y < imgHeight; y++) {
                int argb = reader.getArgb(x, y);

                // Extract color components
                int alpha = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;

                // Invert color components
                red = 255 - red;
                green = 255 - green;
                blue = 255 - blue;

                // Set the inverted pixel
                int invertedArgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                writer.setArgb(x, y, invertedArgb);
            }
        }

        flushImage();
        addMessageInTextArea("Image colors inverted.");
        System.out.println("Image colors inverted.");
    }
}

