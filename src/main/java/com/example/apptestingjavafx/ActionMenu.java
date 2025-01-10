package com.example.apptestingjavafx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.application.Platform;
import javafx.scene.Scene;

public class ActionMenu extends Menu {
    private final ObjectProperty<EventHandler<ActionEvent>> onMenuAction = new SimpleObjectProperty<>();
    private boolean cssApplied = false;

    public ActionMenu() {
        MenuItem hiddenItem = new MenuItem();
        getItems().add(hiddenItem);

        setStyle("-fx-padding: 3 8 3 8; -fx-min-height: 25;");
        getStyleClass().add("action-menu");

        parentMenuProperty().addListener((observable, oldMenu, newMenu) -> {
            if (newMenu != null && newMenu.getParentPopup() != null && !cssApplied) {
                Scene scene = newMenu.getParentPopup().getScene();
                if (scene != null) {
                    scene.getStylesheets().add("data:text/css," +
                            ".action-menu > .context-menu {" +
                            "    -fx-padding: 0;" +
                            "    -fx-background-color: transparent;" +
                            "    -fx-background-insets: 0;" +
                            "    -fx-background-radius: 0;" +
                            "    -fx-border-width: 0;" +
                            "    -fx-min-width: 0;" +
                            "    -fx-min-height: 0;" +
                            "    -fx-max-width: 0;" +
                            "    -fx-max-height: 0;" +
                            "    -fx-opacity: 0;" +
                            "}" +
                            ".action-menu > .label {" +
                            "    -fx-padding: 3 0 3 0;" +
                            "    -fx-text-fill: -fx-text-base-color;" +
                            "    -fx-alignment: center-left;" +
                            "    -fx-content-display: left;" +
                            "    -fx-min-height: 25;" +
                            "    -fx-pref-height: 25;" +
                            "}" +
                            ".action-menu {" +
                            "    -fx-min-height: 25;" +
                            "    -fx-pref-height: 25;" +
                            "}" +
                            ".action-menu:showing > .label {" +
                            "    -fx-padding: 3 0 3 0;" +
                            "    -fx-min-height: 25;" +
                            "}");
                    cssApplied = true;
                }
            }
        });

        setOnShowing(e -> {
            if (onMenuAction.get() != null) {
                onMenuAction.get().handle(new ActionEvent(this, null));
            }
            Platform.runLater(this::hide);
            e.consume();
        });
    }

    public EventHandler<ActionEvent> getOnMenuAction() {
        return onMenuAction.get();
    }

    public void setOnMenuAction(EventHandler<ActionEvent> handler) {
        onMenuAction.set(handler);
    }

    public ObjectProperty<EventHandler<ActionEvent>> onMenuActionProperty() {
        return onMenuAction;
    }
}