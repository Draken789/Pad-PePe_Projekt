module com.example.apptestingjavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.apptestingjavafx to javafx.fxml;
    exports com.example.apptestingjavafx;
}