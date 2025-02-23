package MiniFireForce; // adjust package as needed

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import MiniFireForce.Fire;
import MiniFireForce.GenSituationClass;

public class App extends Application {

    private GenSituationClass genSituation;
    private Label fireCountLabel;
    private Canvas canvas;

    // Canvas dimensions
    private final double CANVAS_WIDTH = 800;
    private final double CANVAS_HEIGHT = 600;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize the simulation class
        genSituation = new GenSituationClass();

        // Load background image (ensure this file exists in /images/background.png)
        Image backgroundImage = new Image(getClass().getResourceAsStream("/images/background.png"));
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        StackPane root = new StackPane();
        root.setBackground(new Background(bgImage));

        // Set the stage icon (ensure this file exists in /images/icon.png)
        Image icon = new Image(getClass().getResourceAsStream("/images/icon.png"));
        primaryStage.getIcons().add(icon);

        // Create canvas for drawing simulation graphics
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);

        // Create a control panel with a button and a label
        fireCountLabel = new Label("Active Fires: 0");
        Button generateFireButton = new Button("Generate Fire");
        generateFireButton.setOnAction(e -> {
            genSituation.generateFire();
            updateDisplay();
        });
        VBox controlPanel = new VBox(10);
        controlPanel.getChildren().addAll(generateFireButton, fireCountLabel);
        // Position the control panel in the top-left corner
        StackPane.setAlignment(controlPanel, Pos.TOP_LEFT);
        controlPanel.setTranslateX(10);
        controlPanel.setTranslateY(10);

        // Add both the canvas and the control panel to the root pane
        root.getChildren().addAll(canvas, controlPanel);

        // Set up the scene and stage
        Scene scene = new Scene(root, CANVAS_WIDTH, CANVAS_HEIGHT);
        primaryStage.setTitle("Fire Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Use an AnimationTimer to continuously refresh the canvas
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                drawSimulation();
            }
        };
        timer.start();
    }

    // Updates the label showing the number of active fires
    private void updateDisplay() {
        int activeFires = genSituation.getActiveFires().size();
        fireCountLabel.setText("Active Fires: " + activeFires);
    }

    // Draws the simulation state (fires) on the canvas
    private void drawSimulation() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // Clear the canvas before redrawing
        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        // For each fire, draw a red circle whose radius is determined by its severity.
        // The simulation coordinate range is assumed to be [-1000, 1000] for both x and y.
        for (Fire fire : genSituation.getActiveFires().values()) {
            double x = (fire.getX() + 1000) / 2000 * CANVAS_WIDTH;
            double y = (fire.getY() + 1000) / 2000 * CANVAS_HEIGHT;
            double radius = fire.getSeverity() * 2;
            gc.setFill(Color.RED);
            gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        }
        updateDisplay();
    }
}