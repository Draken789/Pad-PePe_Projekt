package com.example.apptestingjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class AboutDialogController {

    @FXML
    protected void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) (event.getSource())).getScene().getWindow();
        stage.close();
    }
}