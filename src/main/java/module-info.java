module hello {
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires org.junit.jupiter.api;    // For Swing / AWT
    // ...other requires if needed...
    exports MiniFireForce;    // Export the package containing your main class
}
