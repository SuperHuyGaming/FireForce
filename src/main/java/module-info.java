module hello {
    requires java.desktop;    // For Swing / AWT
    // ...other requires if needed...
    exports MiniFireForce;    // Export the package containing your main class
}
