module hello {
<<<<<<< HEAD
    requires java.desktop;
}
=======
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.junit.jupiter.api;

    opens hello to javafx.fxml;
    exports hello;
}
>>>>>>> 8a6a744a192a562289586d5d7e87d8541c81a5d4
