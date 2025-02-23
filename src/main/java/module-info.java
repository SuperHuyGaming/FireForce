module hello {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.junit.jupiter.api;

    opens hello to javafx.fxml;
    exports hello;
}
