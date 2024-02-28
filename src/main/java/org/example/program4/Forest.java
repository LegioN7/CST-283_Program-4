package org.example.program4;

import java.util.Random;

/**
 * This class represents a forest with a grid of cells. Each cell can be in one of three states:
 * untouched, burning, or burned. The forest can be set on fire at a specific cell, and the fire
 * can spread to adjacent cells based on a probability and the direction of the wind.
 */
public class Forest {

    /**
     * The size of the grid that represents the forest.
     */
    private final int GRID_SIZE;

    /**
     * The grid of cells that represents the forest.
     */
    private final ForestCell[][] grid;

    /**
     * A random number generator used for determining whether a cell catches fire.
     */
    private final Random random = new Random();


    /**
     * Constructs a new Forest with the specified grid size and initial grid of cells.
     *
     * @param gridSize the size of the grid that represents the forest
     * @param grid     the initial grid of cells
     */
    public Forest(int gridSize, ForestCell[][] grid) {
        this.GRID_SIZE = gridSize;
        this.grid = grid;
    }

    /**
     * Returns the grid of cells that represents the forest.
     *
     * @return the grid of cells
     */
    public ForestCell[][] getGrid() {
        return grid;
    }

    /**
     * Returns the cell at the specified coordinates in the grid.
     *
     * @param x the x-coordinate of the cell
     * @param y the y-coordinate of the cell
     * @return the cell at the specified coordinates
     */
    public ForestCell getCell(int x, int y) {
        return this.grid[x][y];
    }


    /**
     * Attempts to spread the fire from the specified cell to its adjacent cells.
     * The fire can spread to the north, south, east, or west of the specified cell.
     * The probability of the fire spreading to an adjacent cell is adjusted based on the wind direction.
     *
     * @param i             the x-coordinate of the specified cell
     * @param j             the y-coordinate of the specified cell
     * @param probability   the initial probability of the fire spreading to an adjacent cell
     * @param windDirection the direction of the wind
     */
    // Redid this entire code because it was redundant and not very efficient
    // I wanted to ensure I iterated over the 2D array only once instead of twice or more
    public void burnAdjacent(int i, int j, double probability, String windDirection) {
        ForestCell cell = getCell(i, j);
        if (cell.isBurning() && !cell.isBurned()) {
            // Check and burn the cells in each direction
            if (i > 0) {
                burnCell(i - 1, j, adjustProbability(probability, windDirection, "NORTH"));
            }
            if (i < GRID_SIZE - 1) {
                burnCell(i + 1, j, adjustProbability(probability, windDirection, "SOUTH"));
            }
            if (j > 0) {
                burnCell(i, j - 1, adjustProbability(probability, windDirection, "WEST"));
            }
            if (j < GRID_SIZE - 1) {
                burnCell(i, j + 1, adjustProbability(probability, windDirection, "EAST"));
            }
            // Increment burn duration for the current cell
            if (cell.getState() == ForestCell.State.BURNING) {
                cell.incrementBurnDuration();
                // If the burn duration is 2, set the cell to scorched
                if (cell.getBurnDuration() == 2) {
                    cell.setState(ForestCell.State.SCORCHED);
                }
            }
        }
    }

    /**
     * Sets the specified cell to burning based on the given adjusted probability.
     * The probability is adjusted based on the wind direction.
     *
     * @param i                   the x-coordinate of the specified cell
     * @param j                   the y-coordinate of the specified cell
     * @param adjustedProbability the adjusted probability of the fire spreading to the specified cell
     */
    // This method was added to avoid redundancy in the burnAdjacent method
    // It sets the cell to burning based on the adjusted probability
    private void burnCell(int i, int j, double adjustedProbability) {
        ForestCell cell = getCell(i, j);
        if (cell.getState() == ForestCell.State.UNTOUCHED) {
            double rand = random.nextDouble();
            if (rand < adjustedProbability) {
                cell.setState(ForestCell.State.BURNING);
            }
        }
    }

    /**
     * Enum representing the four cardinal directions.
     * Enum value because a wind direction is a fixed set of values.
     * Each direction has an opposite direction, which can be obtained using the getOppositeWindDirection method.
     */
    public enum WindDirection {
        NORTH, SOUTH, EAST, WEST;

        /**
         * Returns the opposite direction of the current direction.
         * The opposite direction is determined as follows:
         * - North ("N") is opposite to South ("S")
         * - South ("S") is opposite to North ("N")
         * - East ("E") is opposite to West ("W")
         * - West ("W") is opposite to East ("E")
         *
         * @return the opposite direction
         */
        public WindDirection getOppositeWindDirection() {
            return switch (this) {
                case NORTH -> SOUTH;
                case SOUTH -> NORTH;
                case EAST -> WEST;
                case WEST -> EAST;
            };
        }
    }

    /**
     * Adjusts the given probability based on the wind direction and the direction of fire spread.
     * The wind direction and direction of fire spread are converted from String to WindDirection enum before the adjustment.
     *
     * @param probability         the initial probability of the fire spreading
     * @param windDirectionString the direction of the wind as a String
     * @param directionString     the direction of the fire spread as a String
     * @return the adjusted probability
     */
    // 2nd attempt to integrate enum values into the methods
    private double adjustProbability(double probability, String windDirectionString, String directionString) {

        // Convert the String wind direction and direction to the WindDirection enum
        WindDirection windDirection = WindDirection.valueOf(windDirectionString.toUpperCase());

        // Convert the String direction to the WindDirection enum
        WindDirection direction = WindDirection.valueOf(directionString.toUpperCase());

        // Adjust the probability based on the wind direction
        double adjustedProbability;
        // If the wind is blowing in the same direction as the spread, increase the probability
        if (windDirection == direction) {
            adjustedProbability = Math.min(probability + 0.1, 1.0);
            // If the wind is blowing in the opposite direction as the spread, decrease the probability
        } else if (windDirection == direction.getOppositeWindDirection()) {
            adjustedProbability = Math.max(probability - 0.1, 0.0);
        } else {
            adjustedProbability = probability;
        }
        return adjustedProbability;
    }
}