module hello {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens hello to javafx.fxml;
    exports hello;
}
