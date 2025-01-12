package com.example.apptestingjavafx;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;

public class TriggerMenu extends Menu {
    private final ObjectProperty<EventHandler<Event>> onMenuShowing = new SimpleObjectProperty<>();
    private BooleanProperty trigger;

    public TriggerMenu() {
        setOnShowing(e -> {
            trigger.setValue(true);
            var evh = onMenuShowing.get();
            if (evh != null) {
                evh.handle(e);
            }
            Platform.runLater(this::hide);
            e.consume();
        });
    }


    public void setTrigger(BooleanProperty trigger) { this.trigger = trigger; }
    public BooleanProperty getTrigger() { return trigger; }

    public EventHandler<Event> getOnMenuShowing() {
        return onMenuShowing.get();
    }

    public void setOnMenuShowing(EventHandler<Event> handler) {
        onMenuShowing.set(handler);
    }

    public ObjectProperty<EventHandler<Event>> onMenuShowingProperty() {
        return onMenuShowing;
    }
}