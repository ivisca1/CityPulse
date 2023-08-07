module com.example.proba {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jasperreports;
    requires java.desktop;


    opens com.example.proba to javafx.fxml;
    exports com.example.proba;
}