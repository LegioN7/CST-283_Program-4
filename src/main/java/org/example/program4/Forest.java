package org.example.program4;

import java.util.Random;

/**
 * This class represents a forest with a grid of cells. Each cell can be in one of three states:
 * untouched, burning, or burned. The forest can be set on fire at a specific cell, and the fire
 * can spread to adjacent cells based on a probability and the direction of the wind.
 */
public class Forest {
    private static final int GRID_SIZE = 11;
    private final Burn[][] grid;
    private final Random random;

    /**
     * Constructs a new forest with a grid of cells.
     */
    public Forest() {
        this.grid = new Burn[GRID_SIZE][GRID_SIZE];
        this.random = new Random();
        initializeGrid();
    }

    /**
     * Initializes the grid of cells in the forest.
     */
    private void initializeGrid() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = new Burn();
            }
        }
    }

    /**
     * Spreads the fire to the adjacent cells of the specified cell, based on the given probability and wind direction.
     * The fire can spread to the north, south, east, or west of the specified cell.
     * The probability of the fire spreading to an adjacent cell is adjusted based on the wind direction.
     * If the fire spreads to an adjacent cell, that cell is set to burning.
     * If the specified cell is in the state of starting to burn, its burn duration is incremented.
     *
     * @param i             the x-coordinate of the specified cell
     * @param j             the y-coordinate of the specified cell
     * @param probability   the probability of the fire spreading to an adjacent cell
     * @param windDirection the direction of the wind, which affects the probability of the fire spreading
     */
    public void burnAdjacent(int i, int j, double probability, String windDirection) {
        if (getBurn(i, j).isBurning() && !getBurn(i, j).isBurned()) {
            // Check the cells to the north and check if they start burning
            if (i > 0 && !getBurn(i - 1, j).isBurning() && !getBurn(i - 1, j).isBurned()) {
                double rand = random.nextDouble();
                if (rand < adjustProbability(probability, windDirection, "N")) {
                    getBurn(i - 1, j).setBurning(true);
                }
            }
            // Check the cells to the south and check if they start burning
            if (i < GRID_SIZE - 1 && !getBurn(i + 1, j).isBurning() && !getBurn(i + 1, j).isBurned()) {
                double rand = random.nextDouble();
                if (rand < adjustProbability(probability, windDirection, "S")) {
                    getBurn(i + 1, j).setBurning(true);
                }
            }
            // Check the cells to the west and check if they start burning
            if (j > 0 && !getBurn(i, j - 1).isBurning() && !getBurn(i, j - 1).isBurned()) {
                double rand = random.nextDouble();
                if (rand < adjustProbability(probability, windDirection, "W")) {
                    getBurn(i, j - 1).setBurning(true);
                }
            }
            // Check the cells to the east and check if they start burning
            if (j < GRID_SIZE - 1 && !getBurn(i, j + 1).isBurning() && !getBurn(i, j + 1).isBurned()) {
                double rand = random.nextDouble();
                if (rand < adjustProbability(probability, windDirection, "E")) {
                    getBurn(i, j + 1).setBurning(true);
                }
            }
            // Increment burn duration for the current cell
            if (getBurn(i, j).getBurnState() == Burn.BurnState.START_BURNING) {
                getBurn(i, j).incrementBurnDuration();
            }
        }
    }


    /**
     * Returns the Burn object at the specified coordinates in the grid.
     *
     * @param x the x-coordinate of the cell
     * @param y the y-coordinate of the cell
     * @return the Burn object at the specified coordinates
     */
    public Burn getBurn(int x, int y) {
        // Return the Burn object at the specified coordinates
        return grid[x][y];
    }

    private double adjustProbability(double probability, String windDirection, String direction) {
        // Adjust the probability based on the wind direction
        double adjustedProbability;
        // If the wind is blowing in the same direction as the spread, increase the probability
        if (windDirection.equals(direction)) {
            adjustedProbability = Math.min(probability + 0.1, 1.0);
        // If the wind is blowing in the opposite direction as the spread, decrease the probability
        } else if (windDirection.equals(oppositeDirection(direction))) {
            adjustedProbability = Math.max(probability - 0.1, 0.0);
        // Otherwise, the probability remains the same
        } else {
            adjustedProbability = probability;
        }
        return adjustedProbability;
    }

    /**
     * Returns the opposite direction of the given direction.
     * The opposite direction is determined as follows:
     * - North ("N") is opposite to South ("S")
     * - South ("S") is opposite to North ("N")
     * - East ("E") is opposite to West ("W")
     * - West ("W") is opposite to East ("E")
     * If the given direction is not one of the above, an empty string is returned.
     *
     * @param direction the direction for which to find the opposite
     * @return the opposite direction, or an empty string if the given direction is not recognized
     */
    private String oppositeDirection(String direction) {
        // Return the opposite direction of the given direction
        return switch (direction) {
            case "N" -> "S";
            case "S" -> "N";
            case "E" -> "W";
            case "W" -> "E";
            default -> "";
        };
    }

    /**
     * Represents a cell in the forest that can be in one of three states: untouched, burning, or burned.
     * The burning state of the cell is represented by a boolean value.
     * The duration of the burn is represented by an integer value.
     * The state of the burn is represented by an instance of the BurnState enum.
     */
    public static class Burn {
        /**
         * Indicates whether the cell is currently burning.
         */
        private boolean burning;
        /**
         * The duration for which the cell has been burning.
         */
        private int burnDuration;

        /**
         * The state of the burn, represented by an instance of the BurnState enum.
         */
        private BurnState burnState;

        /**
         * Constructs a new Burn object, representing a cell in the forest.
         * Initially, the cell is not burning, its burn duration is zero, and its state is untouched.
         */
        public Burn() {
            burning = false;
            burnDuration = 0;
            burnState = BurnState.UNTOUCHED;
        }

        /**
         * Increments the burn duration of the cell if it is burning.
         * If the burn duration reaches 3, the cell is set to not burning and its state is set to stop burning.
         * Otherwise, its state is set to continue burning.
         */
        public void incrementBurnDuration() {
            // Increment the burn duration if the cell is burning
            if (burning) {
                burnDuration++;
                // If the burn duration reaches 3, stop burning
                if (burnDuration == 3) {
                    burning = false;
                    burnState = BurnState.STOP_BURNING;
                } else {
                    burnState = BurnState.CONTINUE_BURNING;
                }
            }
        }

        /**
         * Checks if the cell is currently burning.
         *
         * @return true if the cell is burning, false otherwise
         */
        public boolean isBurning() {
            return burning;
        }

        /**
         * Sets the burning state of the cell.
         * If the cell is set to burning, its state is set to start burning.
         * If the cell is set to not burning and its state is not untouched, its state is set to stop burning.
         *
         * @param burning the new burning state of the cell
         */
        public void setBurning(boolean burning) {
            // Set the burning state of the cell
            this.burning = burning;
            if (burning) {
                // If the cell is set to burning, start burning
                this.burnState = BurnState.START_BURNING;
                // If the cell is set to not burning and its state is not untouched, stop burning
            } else if (burnState != BurnState.UNTOUCHED) {
                this.burnState = BurnState.STOP_BURNING;
            }
        }

        /**
         * Checks if the cell is burned.
         * A cell is considered burned if its state is stop burning and its burn duration is greater than 0.
         *
         * @return true if the cell is burned, false otherwise
         */
        public boolean isBurned() {
            // Check if the cell is burned,
            // A cell is considered burned if its state is stop burning and its burn duration is greater than 0
            return burnState == BurnState.STOP_BURNING && burnDuration > 0;
        }

        /**
         * Returns the burn duration of the cell.
         *
         * @return the burn duration of the cell
         */
        public int getBurnDuration() {
            return burnDuration;
        }

        /**
         * Returns the burn state of the cell.
         *
         * @return the burn state of the cell
         */
        public BurnState getBurnState() {
            return burnState;
        }

        /**
         * Enum representing the state of a cell in the forest.
         * The cell can be in one of four states: untouched, start burning, continue burning, or stop burning.
         */
        public enum BurnState {
            UNTOUCHED,
            START_BURNING,
            CONTINUE_BURNING,
            STOP_BURNING
        }
    }
}