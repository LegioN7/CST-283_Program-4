package org.example.program4;

// CST-283
// Aaron Pelto
// Winter 2024

// Program Description
// The U.S. Forest Service has contracted you to write a program that will simulate the behavior of a forest fire.
// Your program should include a two-dimensional grid (array) of forest zones.
// Your simulation should allow you to see the progression of the fire as it spreads over time.
//      For the general grid, define a reasonably large drawing window and with that a "substantial" number of rows and columns.
//          At least 40-50 suggested.
//          An odd number of rows and columns would also be suggested to allow there to be an exact middle position.
// Variables to define the simulation include:
//      Probability of fire spreading from a zone on fire to an adjacent zone not yet burning (0.0...1.0)
//      Wind direction (from N, S, E, or W).
//          Note: wind directions are always referenced as the direction wind is blowing from.
// Create a basic JavaFX user interface that will include the ability for the user to do the following:
//      Set the fire probability and the wind direction
//      A "go" button to start the simulation, as well as define the simulation drawing area.
// The fire will start in the center grid.
//      This will define the first time unit in the simulation cycle.
// As the simulation progresses, each step should include the following actions for each grid:
//      If a grid is on fire (red), and zones adjacent are untouched (green) use a random number generator value to determine if the adjacent zone should catch fire.
//      For example, if a random number (0.0...1.0) is less than the burn probability, then the adjacent grid will be on fire for the next cycle.
//      Once a grid catches fire, it will burn for two simulation time cycles.
//      Afterward, it is considered scorched and should be given another color (yellow as seen below).
//      This area will then not burn again.
//      The fire will only spread to the left, right, up, or down (i.e., no diagonals).
//      The probability of burning downstream from the wind will increase 10% and upstream from the wind will decrease 10% based on that wind direction.
//              For example, if the general probability of burning is 30% and the wind is from the north, then the simulation probability for burning should be north (up) 20%, east (right) 30%, south (down) 40%, and west (30%).
//                  Be sure that the wind adjustment does not push the probability number for a direction outside the 0.0...1.0 range.

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * This class represents a forest fire simulator application.
 * The application uses a grid to represent the forest, where each cell can be in one of three states: untouched, burning, or burned.
 * The fire starts in the center of the grid and can spread to adjacent cells based on a probability and the direction of the wind.
 * The application provides a GUI for the user to set the fire probability and the wind direction, and to start and reset the simulation.
 */
public class ForestFireSimulator extends Application {

    /**
     * The size of the grid that represents the forest.
     */
    private static final int GRID_SIZE = 11;

    /**
     * The grid pane that represents the forest in the GUI.
     */
    private GridPane gridPane;

    /**
     * The forest that is being simulated.
     */
    private Forest forest = new Forest();

    /**
     * The scene of the application.
     */
    private Scene scene;

    /**
     * The label that displays the number of simulation cycles.
     */
    private Label simulationCyclesLabel;

    /**
     * The number of simulation cycles.
     */
    private int simulationCycles;

    /**
     * The label that displays the countdown to the next simulation cycle.
     */
    private Label countdownLabel;

    /**
     * The timeline that controls the simulation.
     */
    private Timeline timeline;

    /**
     * The timeline that controls the countdown.
     */
    private Timeline countdownTimeline;
    /**
     * A 2D array of rectangles representing the cells in the forest grid.
     */
    private final Rectangle[][] forestGrid = new Rectangle[GRID_SIZE][GRID_SIZE];

    /**
     * The main method that launches the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * The method that is called when the application is started.
     * It creates the GUI for the forest fire simulator and shows the stage.
     *
     * @param stage the primary stage
     */
    @Override
    public void start(Stage stage) {
        // Create the grid pane to represent the forest
        this.gridPane = createForestGrid();
        // Create the GUI for the forest fire simulator
        createForestFireSimulatorGUI();
        // Set the title, scene, and show the stage
        stage.setTitle("Forest Fire Simulator");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates a VBox with a label and a slider to select the fire probability.
     *
     * @return the VBox with a label and a slider
     */
    private VBox createFireProbabilityBox() {
        // Set the Title
        Label title = new Label("Fire Probability");
        title.setPadding(new Insets(10));
        // Create the Slider
        Slider probabilitySlider = new Slider(0.01, 1, 0.3);
        // Set the width to the maximum value of the window
        probabilitySlider.setMaxWidth(Double.MAX_VALUE);
        // Set the Tooltip
        probabilitySlider.setTooltip(new Tooltip("Set the probability of fire spreading"));
        // Create the Label for probability
        Label probabilityLabel = new Label();
        probabilitySlider.valueProperty().addListener((observable, oldValue, newValue) ->
                // Set the probability label to the new value and format it to a percentage
                probabilityLabel.setText(String.format("Selected Probability: %.0f%%", newValue.doubleValue() * 100))
        );
        probabilityLabel.setText(String.format("Selected Probability: %.0f%%", probabilitySlider.getValue() * 100));
        // Create the VBox
        VBox fireProbabilityBox = new VBox(new VBox(title, probabilityLabel), probabilitySlider);
        fireProbabilityBox.setSpacing(10);
        return fireProbabilityBox;
    }

    /**
     * Creates a VBox with a label and a combo box to select the wind direction.
     *
     * @return the VBox with a label and a combo box
     */
    private VBox createWindDirectionBox() {
        // Set the Title
        Label title = new Label("Wind Direction");
        title.setPadding(new Insets(10));
        // Create the ComboBox
        ComboBox<String> windDirectionComboBox = new ComboBox<>();
        // Add the wind directions to the combo box
        windDirectionComboBox.getItems().addAll("N", "S", "E", "W");
        // Set the default value to "N"
        windDirectionComboBox.setValue("N");
        // Set the tooltip
        windDirectionComboBox.setTooltip(new Tooltip("Set the wind direction"));
        // Create the VBox
        VBox windDirectionBox = new VBox(title, windDirectionComboBox);
        windDirectionBox.setSpacing(10);
        return windDirectionBox;
    }

    /**
     * Creates a HBox with buttons to start, pause, stop, and reset the simulation.
     *
     * @param gridPane              the grid pane that represents the forest
     * @param probabilitySlider     the slider that controls the fire probability
     * @param windDirectionComboBox the combo box that controls the wind direction
     * @return the HBox with buttons
     */
    private HBox createSimulationButtonsBox(GridPane gridPane, Slider probabilitySlider, ComboBox<String> windDirectionComboBox) {
        // Create the Start Simulation Button
        Button startButton = new Button("Start Simulation");
        startButton.setOnAction(e -> startSimulation(probabilitySlider.getValue(), windDirectionComboBox.getValue()));

        // Create the Pause Simulation Button
        Button pauseButton = new Button("Pause Simulation");
        pauseButton.setOnAction(e -> pauseSimulation());

        // Create the Reset Simulation Button
        Button resetButton = new Button("Reset Simulation");
        resetButton.setOnAction(e -> resetSimulation(probabilitySlider, windDirectionComboBox));

        // Create the HBox
        HBox simulationButtonsBox = new HBox(startButton, pauseButton, resetButton);
        simulationButtonsBox.setSpacing(10);

        return simulationButtonsBox;
    }

    /**
     * Creates a VBox with labels to display the number of simulation cycles and the countdown to the next simulation cycle.
     *
     * @return the VBox with labels
     */
    private VBox createSimulationCycleLabelsBox() {
        // Create the Simulation cycles label
        simulationCyclesLabel = new Label("Simulation Cycles: 0");
        // Create the Countdown label
        countdownLabel = new Label("Next cycle in: 5 seconds");
        // Create the VBox
        VBox simulationCycleLabelsBox = new VBox(simulationCyclesLabel, countdownLabel);
        simulationCycleLabelsBox.setSpacing(10);
        return simulationCycleLabelsBox;
    }

    /**
     * Creates a grid pane with rectangles to represent the cells in the forest.
     * Each cell is initially green to represent untouched forest.
     *
     * @return the grid pane with rectangles
     */
    private GridPane createForestGridPane() {
        // Create the forest grid
        GridPane gridPane = createForestGrid();
        gridPane.setGridLinesVisible(true);
        return gridPane;
    }

    /**
     * Creates the GUI for the forest fire simulator.
     */
    private void createForestFireSimulatorGUI() {
        // Create the GUI for the forest fire simulator
        // The GUI includes a grid pane to represent the forest, sliders to set the fire probability, and a combo box to set the wind direction
        // The GUI also includes buttons to start, pause, stop, and reset the simulation, and labels to display the number of simulation cycles and the countdown to the next simulation cycle
        VBox fireProbabilityBox = createFireProbabilityBox();
        VBox windDirectionBox = createWindDirectionBox();
        HBox simulationButtonsBox = createSimulationButtonsBox(gridPane, (Slider) fireProbabilityBox.getChildren().get(1), (ComboBox<String>) windDirectionBox.getChildren().get(1));
        VBox simulationCycleLabelsBox = createSimulationCycleLabelsBox();
        GridPane forestGridPane = createForestGridPane();

        VBox vbox = new VBox(fireProbabilityBox, new Separator(), windDirectionBox, new Separator(), simulationButtonsBox, new Separator(), simulationCycleLabelsBox, new Separator(), forestGridPane);
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setFillWidth(true);

        scene = new Scene(vbox);
    }

    /**
     * Creates a grid pane with rectangles to represent the cells in the forest.
     * Each cell is initially green to represent untouched forest.
     *
     * @return the grid pane with rectangles
     */
    private GridPane createForestGrid() {
        GridPane gridPane = new GridPane();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Rectangle rectangle = new Rectangle(40, 40);
                rectangle.setFill(Color.GREEN);
                gridPane.add(rectangle, i, j);
                forestGrid[i][j] = rectangle;
            }
        }
        return gridPane;
    }

    /**
     * Populates the given GridPane with rectangles to represent the cells in the forest.
     * Each cell is initially green to represent untouched forest.
     *
     * @param gridPane the GridPane to populate
     */
    private void populateGrid(GridPane gridPane) {
        // Add rectangles to the grid pane to represent the cells in the forest
        // The cells are initially green to represent untouched forest
        // IntelliJ offered to adjust the grid_size requirement by calling the constant from the class
        for (int i = 0; i < ForestFireSimulator.GRID_SIZE; i++) {
            for (int j = 0; j < ForestFireSimulator.GRID_SIZE; j++) {
                Rectangle rectangle = new Rectangle(30, 30);
                rectangle.setFill(Color.GREEN);
                gridPane.add(rectangle, i, j);
            }
        }
    }

    /**
     * Updates the forest grid in the GUI based on the state of the forest.
     */
    private void updateGrid() {
        // Update the forest grid in the GUI based on the state of the forest
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                // Get the cell at the specified position
                // The cell can be untouched, burning, or burned
                Rectangle rectangle = forestGrid[i][j];
                Forest.Burn burn = forest.getBurn(i, j);
                // If the cell is burning, change its color to red
                if (burn.isBurning()) {
                    if (burn.getBurnDuration() < 2) {
                        rectangle.setFill(Color.RED);
                    } else {
                        // If the cell has been burning for 2 time units, change its color to yellow on the 3rd
                        rectangle.setFill(Color.YELLOW);
                    }
                    // Debug Statement
                    // System.out.println("Tile at (" + i + ", " + j + ") is on fire. Burn duration: " + burn.getBurnDuration());
                } else if (burn.isBurned()) {
                    // If the cell is burned, change its color to yellow
                    rectangle.setFill(Color.YELLOW);
                    // Debug Statement
                    //System.out.println("Tile at (" + i + ", " + j + ") is scorched. Burn duration: " + burn.getBurnDuration());
                } else {
                    // If the cell is untouched, keep its color green
                    rectangle.setFill(Color.GREEN);
                }
            }
        }
    }

    /**
     * Starts the simulation with the given fire probability and wind direction.
     *
     * @param probability   the fire probability
     * @param windDirection the wind direction
     */
    private void startSimulation(double probability, String windDirection) {
        // Debug Statement
        System.out.println("Simulation Start # " + (simulationCycles + 1));
        // Start the fire in the center of the forest
        startFire();
        // Update the forest grid in the GUI
        updateGrid();
        countdownTimeline = createCountdownTimeline();
        // Set the countdown timeline to run indefinitely
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
        // Create the fire spread timeline
        timeline = createFireSpreadTimeline(probability, windDirection);
        // Set the timeline to run indefinitely
        timeline.setCycleCount(Timeline.INDEFINITE);
        // Start the simulation
        timeline.play();
    }

    /**
     * Starts the fire in the center of the forest.
     */
    private void startFire() {
        // Start the fire in the center of the forest
        int center = GRID_SIZE / 2;
        forest.getBurn(center, center).setBurning(true);
        // Update the forest grid in the GUI
        updateGrid();
        // Increment the number of simulation cycles to 1
        // This is the first cycle of the simulation
        simulationCycles = 1;
        // Update the simulation cycles label
        simulationCyclesLabel.setText("Simulation Cycles: " + simulationCycles);
    }

    /**
     * Creates the countdown timeline for the simulation.
     *
     * @return the countdown timeline
     */
    private Timeline createCountdownTimeline() {
        // Create a timeline to update the countdown label every second
        return new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String countdownText = countdownLabel.getText();
            int countdownTime = Integer.parseInt(countdownText.substring(countdownText.lastIndexOf(":") + 2).replace(" seconds", ""));
            if (countdownTime > 0) {
                // Decrement the countdown time
                countdownTime--;
                countdownLabel.setText("Next cycle in: " + countdownTime + " seconds");
            } else {
                countdownTimeline.stop();
            }
        }));
    }

    /**
     * Starts the simulation with the given fire probability and wind direction.
     * This method initializes the simulation, starts the fire in the center of the forest,
     * and begins the countdown and fire spread timelines.
     *
     * @param probability   the fire probability
     * @param windDirection the wind direction
     */
    private Timeline createFireSpreadTimeline(double probability, String windDirection) {
        // Create a timeline to control the simulation
        // The timeline will update the fire spread in the forest every 5 seconds
        return new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            // System.out.println("Simulation Cycle # " + (simulationCycles + 1));
            boolean[][] burningCells = new boolean[GRID_SIZE][GRID_SIZE];
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    Forest.Burn burn = forest.getBurn(i, j);
                    if (burn.isBurning()) {
                        burningCells[i][j] = true;
                        burn.incrementBurnDuration();
                    }
                }
            }
            // Update the fire spread in the forest
            updateFireSpread(burningCells, probability, windDirection);
            // Increment the number of simulation cycles
            simulationCycles++;
            // Update the simulation cycles label
            simulationCyclesLabel.setText("Simulation Cycles: " + simulationCycles);
            // Update the forest grid in the GUI
            updateGrid();
            if (checkStillBurning()) {
                // If the fire has gone out, stop the timeline and display an alert
                timeline.stop();
                javafx.application.Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Simulation Complete");
                    alert.setHeaderText(null);
                    alert.setContentText("The simulation has completed. It took " + simulationCycles + " simulation cycle time units for the fire to go out.");
                    alert.showAndWait();
                });
            }
            // Update the countdown label
            countdownLabel.setText("Next cycle in: 5 seconds");
        }));
    }

    /**
     * Updates the fire spread in the forest based on the given fire probability and wind direction.
     * This method iterates over all cells and, if a cell is burning, it attempts to spread the fire to adjacent cells.
     *
     * @param burningCells  the cells that are currently burning
     * @param probability   the fire probability
     * @param windDirection the wind direction
     */
    private void updateFireSpread(boolean[][] burningCells, double probability, String windDirection) {
        // Spread the fire to adjacent cells
        // The fire can spread to the north, south, east, or west of the specified cell
        // The probability of the fire spreading to an adjacent cell is adjusted based on the wind direction
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (burningCells[i][j]) {
                    forest.burnAdjacent(i, j, probability, windDirection);
                }
            }
        }
    }

    /**
     * Checks if the fire has visited all cells in the forest.
     *
     * @return true if all cells have been visited, false otherwise
     */
    private boolean checkStillBurning() {
        // Check if all cells have been visited
        // If all cells have been visited, the fire has gone out
        // If there are still cells burning, the fire is still spreading
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Forest.Burn burn = forest.getBurn(i, j);
                if (burn.isBurning()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Pauses the simulation.
     */
    private void pauseSimulation() {
        if (timeline != null) {
            timeline.pause();
        }
        if (countdownTimeline != null) {
            countdownTimeline.pause();
        }
    }

    /**
     * Resets the simulation.
     *
     * @param probabilitySlider     the slider that controls the fire probability
     * @param windDirectionComboBox the combo box that controls the wind direction
     */
    private void resetSimulation(Slider probabilitySlider, ComboBox<String> windDirectionComboBox) {
        // Stop the simulation timelines
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }

        // Stop the countdown timeline
        if (countdownTimeline != null) {
            countdownTimeline.stop();
            countdownTimeline = null;
        }

        // Reset the forest and the simulation cycles
        forest = new Forest();
        simulationCycles = 0;

        // Set the fire probability and wind direction to their default values
        probabilitySlider.setValue(0.3);
        windDirectionComboBox.setValue("N");

        // Reset the simulation cycles label and the countdown label
        simulationCyclesLabel.setText("Simulation Cycles: 0");
        countdownLabel.setText("Next cycle in: 5 seconds");

        // Reset the forestGrid
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                forestGrid[i][j].setFill(Color.GREEN);
            }
        }
    }
}