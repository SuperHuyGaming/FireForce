module hello {
    requires javafx.controls;
    requires javafx.fxml;

    opens hello to javafx.fxml;
    exports hello;
}
