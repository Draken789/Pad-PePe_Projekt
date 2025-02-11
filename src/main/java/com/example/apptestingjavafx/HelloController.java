package com.example.apptestingjavafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.canvas.Canvas;


public class HelloController {
    public RadioButton radioModified;
    @FXML
    private VBox radioVBox;

    @FXML
    private ToggleGroup radioImageOptions;

    @FXML
    private ImageView imageView;

    @FXML
    private MenuItem menuSave;

    @FXML
    private Button restoreImgBtn;

    @FXML
    private Menu menuFilters;

    @FXML
    private Pane imageContainer;

    @FXML
    private VBox vBoxRightOptions;

    @FXML
    private HBox heightBoxViewPortImage;

    @FXML
    private TextArea messagesTextArea;

    // Canvas for drawing
    @FXML
    private Canvas drawingCanvas;

    // Drawing state variables
    private boolean isDrawing = false;
    private double startX, startY;
    @FXML
    private Button rotateButton;

    // ============================================== //
//             Application lifecycle              //
// ============================================== //

    public void initialize() {
        messageHistory = new ArrayList<>();
        drawingCanvas.setVisible(false); // Initially hidden
        drawingCanvas.widthProperty().bind(imageContainer.widthProperty());
        drawingCanvas.heightProperty().bind(imageContainer.heightProperty());

        drawingCanvas.widthProperty().addListener((obs, oldVal, newVal) -> clearCanvas());
        drawingCanvas.heightProperty().addListener((obs, oldVal, newVal) -> clearCanvas());
    }

// ============================================== //
//                     Drawing                    //
// ============================================== //

    private boolean isEraserMode = false; // Track if eraser mode is active

    // Add the ability to toggle drawing mode
    @FXML
    protected void toggleDrawing(ActionEvent event) {
        isDrawing = !isDrawing;
        drawingCanvas.setVisible(isDrawing);
        if (isDrawing) {
            drawingCanvas.setViewOrder(-100);
            log("Drawing mode enabled.");
        } else {
            log("Drawing mode disabled.");
            drawingCanvas.setViewOrder(0);
        }
    }

    // Toggle eraser mode
    @FXML
    protected void toggleEraser(ActionEvent event) {
        isEraserMode = !isEraserMode;
        if (isEraserMode) {
            log("Eraser mode enabled.");
        } else {
            log("Eraser mode disabled.");
        }
    }

    // Mouse events to draw
    @FXML
    protected void onCanvasMousePressed(MouseEvent event) {
        if (!isDrawing) return;
        startX = event.getX();
        startY = event.getY();
    }

    @FXML
    protected void onCanvasMouseDragged(MouseEvent event) {
        if (!isDrawing) return;

        double endX = event.getX();
        double endY = event.getY();

        // Get the GraphicsContext of the canvas
        GraphicsContext gc = drawingCanvas.getGraphicsContext2D();

        if (isEraserMode) {
            // Eraser logic: Clear the line by setting it to the background color
            gc.setStroke(Color.TRANSPARENT); // Set the stroke color to transparent
            gc.setLineWidth(20); // Set a thicker line width for the eraser
            gc.clearRect(endX - 10, endY - 10, 20, 20); // Clear a small rectangular area
        } else {
            // Drawing logic
            gc.setStroke(Color.BLACK); // Set color of the drawing
            gc.setLineWidth(3); // Set line width
            gc.strokeLine(startX, startY, endX, endY); // Draw a line
        }

        // Update the start coordinates for the next line
        startX = endX;
        startY = endY;
    }

    @FXML
    protected void onCanvasMouseReleased(MouseEvent event) {
        if (isDrawing) {
            log(isEraserMode ? "Finished erasing." : "Finished drawing.");
        }
    }

    @FXML
    protected void onClearCanvas(ActionEvent event) {
        GraphicsContext gc = drawingCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
        log("Canvas cleared.");
    }

    private void clearCanvas() {
        GraphicsContext gc = drawingCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
    }




    // ============================================== //
    //            Image data manipulation             //
    // ============================================== //

    private Image srcImg;
    private WritableImage dstImg;
    private int imgWidth;
    private int imgHeight;
    private Image origImg;
    private boolean showOrig;

    private void blitImage() {
        panicIfNoImg();
        dstImg.getPixelWriter().setPixels(0, 0, imgWidth, imgHeight, srcImg.getPixelReader(), 0, 0);
    }

    private void flushImage() {
        panicIfNoImg();
        srcImg = dstImg;
        imageView.setImage(srcImg);
        dstImg = new WritableImage(imgWidth, imgHeight);
    }

    private void resetToImg(Image img) {
        setEnabled(true);
        imgWidth = (int) img.getWidth();
        imgHeight = (int) img.getHeight();
        dstImg = new WritableImage(imgWidth, imgHeight);
        srcImg = img;

        if (origImg == null) {
            origImg = img;
        }

        imageView.setImage(img);
        imageView.setRotate(0);
        resizeImage();

        showOrig = false;
        radioModified.setSelected(true);
    }

    private void resetTransform() {
        imageView.setLayoutX(0);
        imageView.setLayoutY(0);
        imageView.setTranslateX(0);
        imageView.setTranslateY(0);
        imageView.setScaleX(1);
        imageView.setScaleY(1);
        scale = 1;
    }

    private void setEnabled(boolean enabled) {
        boolean inv = !enabled;
        restoreImgBtn.setDisable(inv);
        menuFilters.setDisable(inv);
        menuSave.setDisable(inv);
        radioVBox.setDisable(inv);
        rotateButton.setDisable(inv);
    }

    private void setScale(double factor) {
        imageView.setScaleX(factor);
        imageView.setScaleY(factor);
        log("Zoom: " + Math.round(factor * 100) / 100d);
    }

    private void resizeImage() {
        resetTransform();
        double containerWidth = imageContainer.getWidth();
        double containerHeight = imageContainer.getHeight();

        double imageWidth = imageView.getImage().getWidth();
        double imageHeight = imageView.getImage().getHeight();

        double widthRatio = containerWidth / imageWidth;
        double heightRatio = containerHeight / imageHeight;
        double factor = Math.min(1, Math.min(widthRatio, heightRatio));

        if (factor == 1) return;

        scale = (Math.log(factor) / Math.log(2)) / 8;
        setScale(factor);

        imageView.setLayoutX(((containerWidth - imageWidth * factor) -(imageWidth - imageWidth * factor)) / 2);
        imageView.setLayoutY(((containerHeight - imageHeight * factor) -(imageHeight - imageHeight * factor)) / 2);
        System.out.println(imageView.getLayoutX());
        System.out.println(imageView.getLayoutY());
        System.out.println(factor);
    }

    // ============================================== //
    //                Image dragging                  //
    // ============================================== //

    private double dragStartX, dragStartY;
    private double originalX, originalY;
    private double scale;
    private boolean isDragging = false;

    @FXML
    protected void onImageDrag(MouseEvent event) {
        if (!isDragging || isDrawing) return;

        double deltaX = event.getSceneX() - dragStartX;
        double deltaY = event.getSceneY() - dragStartY;

        double newX = originalX + deltaX;
        double newY = originalY + deltaY;

        imageView.setLayoutX(newX);
        imageView.setLayoutY(newY);
    }

    @FXML
    protected void onImageDragStart(MouseEvent event) {
        if (isDrawing) return;
        if (origImg == null) return;
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
        if (origImg == null) return;
        isDragging = false;
        event.consume();
    }

    @FXML
    protected void onScroll(ScrollEvent ev) {
        if (origImg == null) return;
        double delta = ev.getDeltaY() / ev.getMultiplierY();
        if (delta == 0) return;
        scale += delta;
        setScale(Math.pow(2, scale / 8));
    }

    // ============================================== //
    //              Logging and alerts                //
    // ============================================== //

    private ArrayList<String> messageHistory;

    private void log(String text) {
        messagesTextArea.appendText(text + "\n");
        messageHistory.add(text);
        System.out.println(text);
    }
    private static void showError(String message, Exception ex) {
        ex.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception");
        alert.setHeaderText(message);
        alert.setContentText(ex.getMessage());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Stacktrace:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    private static void showAlert(String message) {
        Alert dialog = new Alert(Alert.AlertType.WARNING);
        dialog.setTitle("Warning");
        dialog.setHeaderText(message);
        dialog.getButtonTypes().setAll(ButtonType.OK);
        dialog.showAndWait();
    }

    private static void panic(Exception ex) {
        showError("A fatal error has occurred and the application can no longer run", ex);
        System.exit(1);
    }



    // ============================================== //
    //                    Filters                     //
    // ============================================== //

    @FXML
    protected void onGrayscale() {
        if (noImg()) return;

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
        log("Applied grayscale");
    }

    @FXML
    protected void onPixelmelt() {
        if (noImg()) return;
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
                pw.setArgb(x, y, a
                        | (int)(coefR * (v << 16))
                        | (int)(coefG * (v << 8))
                        | (int)(coefB * v));
            }
        }
        flushImage();
        log("Applied pixel melt");
    }

    @FXML
    protected void onPixelize() {
        if (noImg()) return;

        int n;
        String input = "2";
        while (true) {
            TextInputDialog dialog = new TextInputDialog(input);
            dialog.setTitle("Pixalize");
            dialog.setHeaderText("Filter settings");
            dialog.setContentText("Degree of pixelization:");
            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty()){
                return;
            }
            input = result.get();
            try {
                n = Integer.parseInt(input);
                if (n < 1) {
                    showAlert( "Degree must be greater or equal to 1, and a whole number");
                    continue;
                }
                if (n == 1) return;
            }
            catch (NumberFormatException _) {
                showAlert("Degree must be greater or equal to 1, and a whole number");
                continue;
            }
            break;
        }

        var pr = srcImg.getPixelReader();
        var pw = dstImg.getPixelWriter();
        for (int i = 0; i < imgWidth; i += n) {
            for (int j = 0; j < imgHeight; j += n) {
                int wmax = Math.min(i + n, imgWidth);
                int hmax = Math.min(j + n, imgHeight);
                int ttl = 0;
                double a = 0;
                double r = 0;
                double g = 0;
                double b = 0;
                for (int x = i; x < wmax; x++) {
                    for (int y = j; y < hmax; y++) {
                        var color = pr.getColor(x, y);
                        a += color.getOpacity();
                        r += color.getRed();
                        g += color.getGreen();
                        b += color.getBlue();
                        ttl++;
                    }
                }
                var color = new Color(r / ttl, g / ttl, b / ttl, a / ttl);
                for (int x = i; x < Math.min(i + n, imgWidth); x++) {
                    for (int y = j; y < Math.min(j + n, imgHeight); y++) {
                        pw.setColor(x, y, color);
                    }
                }
            }
        }
        flushImage();
        log("Applied pixelize with n=" + n);
    }

    @FXML
    protected void onInvertColors() {
        if (noImg()) return;

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
        log("Applied color invert filter.");

    }
    @FXML
    protected void onBlackAndWhite() {
        if (noImg()) return;

        var pr = srcImg.getPixelReader();
        var pw = dstImg.getPixelWriter();

        for (int x = 0; x < imgWidth; x++) {
            for (int y = 0; y < imgHeight; y++) {
                int argb = pr.getArgb(x, y);
                int a = (argb >> 24) & 0xFF; // Extract alpha channel
                int r = (argb >> 16) & 0xFF; // Extract red channel
                int g = (argb >> 8) & 0xFF;  // Extract green channel
                int b = argb & 0xFF;         // Extract blue channel

                // Calculate brightness
                int brightness = (int) (0.21 * r + 0.71 * g + 0.07 * b);

                // Threshold to determine black or white
                int bwColor = (brightness < 128) ? 0 : 255;

                // Set the pixel to black or white
                int bwArgb = (a << 24) | (bwColor << 16) | (bwColor << 8) | bwColor;
                pw.setArgb(x, y, bwArgb);
            }
        }

        flushImage();
        log("Applied black-and-white filter.");
    }

    // ============================================== //
    //                     Asserts                    //
    // ============================================== //

    private boolean noImg() {
        if (origImg != null) return false;
        log("No image loaded!");
        Alert dialog = new Alert(Alert.AlertType.WARNING);
        dialog.setTitle("Warning");
        dialog.setHeaderText("No image loaded");
        dialog.getButtonTypes().clear();
        dialog.getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
        return true;
    }

    private void panicIfNoImg() {
        if (origImg == null)
            panic(new IOException("No image loaded"));
    }

    // ============================================== //
    //                   Menu Bar                     //
    // ============================================== //

    @FXML
    protected void onExitClick() {
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
            panic(e);
        }
    }

    @FXML
    protected void onSaveImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"),
                new FileChooser.ExtensionFilter("JPEG files (*.jpg, *.jpeg)", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("BMP files (*.bmp)", "*.bmp"),
                new FileChooser.ExtensionFilter("All Images", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                Image imageToBeSaved = srcImg;
                BufferedImage bufferedImage = javafx.embed.swing.SwingFXUtils.fromFXImage(imageToBeSaved, null);

                String fileName = file.getName().toLowerCase();
                String formatName;

                if (fileName.endsWith(".png")) {
                    formatName = "png";
                } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    formatName = "jpg";

                    // for jpeg, remove alpha channel so there's no invisibility
                    BufferedImage newBufferedImage = new BufferedImage(
                            bufferedImage.getWidth(),
                            bufferedImage.getHeight(),
                            BufferedImage.TYPE_INT_RGB
                    );
                    newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, null);
                    bufferedImage = newBufferedImage;
                } else if (fileName.endsWith(".bmp")) {
                    formatName = "bmp";
                } else {
                    formatName = "png"; //default
                }

                ImageIO.write(bufferedImage, formatName, file);

                System.out.println("Image saved successfully to " + file.getAbsolutePath());
                log("Image saved.");
            } catch (Exception ex) {
                showError("An error has occurred while saving the image", ex);
                ex.printStackTrace();
            }
        }
    }

    @FXML
    protected void onRotateImage() {
        if (noImg()) return;

        // Zapamatujeme si aktuální pozici
        double currentX = imageView.getLayoutX();
        double currentY = imageView.getLayoutY();
        double currentScale = imageView.getScaleX();

        WritableImage rotatedImage = new WritableImage(imgHeight, imgWidth);
        PixelWriter writer = rotatedImage.getPixelWriter();
        PixelReader reader = srcImg.getPixelReader();

        for (int x = 0; x < imgWidth; x++) {
            for (int y = 0; y < imgHeight; y++) {
                int newX = imgHeight - 1 - y;
                int newY = x;
                Color color = reader.getColor(x, y);
                writer.setColor(newX, newY, color);
            }
        }

        int temp = imgWidth;
        imgWidth = imgHeight;
        imgHeight = temp;

        dstImg = rotatedImage;
        flushImage();
        resetToImg(srcImg);

        // Obnovíme pozici a scale po rotaci
        imageView.setLayoutX(currentX);
        imageView.setLayoutY(currentY);
        imageView.setScaleX(currentScale);
        imageView.setScaleY(currentScale);
        scale = Math.log(currentScale) / Math.log(2) * 8; // Aktualizujeme scale proměnnou

        log("Rotated image by 90 degrees");
    }


    @FXML
    protected void onSelectImage() {
        Image result = OpenFileViaExplorer();
        if (result != null) {
            resetToImg(result);
            resizeImage();
        }
    }

    // ============================================== //
    //                   Buttons                      //
    // ============================================== //

    @FXML
    protected void onRestoreOriginalImage() {
        if (noImg()) return;  // Check if no image exists

        // Store the current drawing state
        boolean wasDrawing = isDrawing;

        // Restore the original image
        resetToImg(origImg);  // Restore the original image

        // If drawing mode is enabled, clear the canvas and restore the original image
        if (wasDrawing) {
            // Clear the canvas to erase any drawings
            clearCanvas();

            // Ensure the canvas is still visible and active
            drawingCanvas.setVisible(true);
            drawingCanvas.setViewOrder(-100);  // Ensure it's on top if necessary
            log("Restored original image and cleared drawings.");
        } else {
            log("Restored original image.");
        }
    }


    @FXML
    protected void onRandomImage() {
        imgWidth = imgHeight = 512;
        origImg = srcImg = dstImg = new WritableImage(imgWidth, imgHeight);
        resetTransform();
        setEnabled(false);
        int start = (int)(Math.random() * 10000);
        onRandomImageGen(start, start + 4);
    }

    private void onRandomImageGen(int seed, int end) {
        var pw = dstImg.getPixelWriter();
        for (int x = 0; x < imgWidth; x++) {
            for (int y = 0; y < imgHeight; y++) {
                int a = x, b = y;
                x *= seed;
                y *= seed;
                pw.setColor(a, b, new Color(
                        Math.abs(Math.sin(y / 255d + x / 255d * 90) * 0.5d) % 1,
                        (1 + Math.cos(y * (x + 1d) * 4)) * 0.5d,
                        (1 + Math.cos(y + 50) * Math.sin(x*y)) * 0.5d,
                        1d));
                x = a;
                y = b;
            }
        }
        flushImage();
        origImg = srcImg;
        if (seed < end) Platform.runLater(() -> onRandomImageGen(seed + 1, end));
        else {
            setEnabled(true);
            showOrig = false;
            radioModified.setSelected(true);
        }
    }

    @FXML
    private void onRadioOriginal() {
        if (noImg() || showOrig) return;
        setEnabled(false);
        radioVBox.setDisable(false);
        restoreImgBtn.setDisable(false);
        showOrig = true;
        imageView.setImage(origImg);
    }
    @FXML
    private void onRadioModified() {
        if (noImg() || !showOrig) return;
        setEnabled(true);
        showOrig = false;
        imageView.setImage(srcImg);
    }




    // ============================================== //
    //                    Utility                     //
    // ============================================== //
    private static Image OpenFileViaExplorer() {
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
            showError("An error has occurred while loading an image", e);
            e.printStackTrace();
        }
        return null;
    }
}

