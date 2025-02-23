module hello {
    requires javafx.fxml;
    requires java.desktop;
    requires org.junit.jupiter.api;
    requires javafx.controls;    // For Swing / AWT
    // ...other requires if needed...
    exports MiniFireForce;    // Export the package containing your main class
}
