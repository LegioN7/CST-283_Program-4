package org.example.program4;

import javafx.scene.shape.Rectangle;

/**
 * Represents a cell in a forest. Each cell has a state, burn duration, a rectangle for graphical representation, and a flag indicating if it has been burned.
 */
public class ForestCell {


    /**
     * Enum to represent the state of the cell.
     * Enum because the state of the cell can only be one of the three values.
     * UNTOUCHED: The cell has not been set on fire or scorched.
     * BURNING: The cell is currently on fire.
     * SCORCHED: The cell has been burned.
     */
    public enum State {
        UNTOUCHED, BURNING, SCORCHED
    }

    private State state;
    private int burnDuration;
    private final Rectangle rectangle;
    private boolean isBurned;

    /**
     * Constructs a ForestCell with the given rectangle for graphical representation.
     * Initially, the state is set to UNTOUCHED, burn duration is 0, and isBurned flag is false.
     *
     * @param rectangle the rectangle for graphical representation
     */
    public ForestCell(Rectangle rectangle) {
        this.state = State.UNTOUCHED;
        this.burnDuration = 0;
        this.rectangle = rectangle;
    }

    /**
     * Returns the state of the cell.
     *
     * @return the state of the cell
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the state of the cell.
     *
     * @param state the state to set
     */
    public void setState(State state) {
        this.state = state;
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
     * Increments the burn duration of the cell by 1.
     */
    public void incrementBurnDuration() {
        this.burnDuration++;
    }

    /**
     * Returns the rectangle of the cell for graphical representation.
     *
     * @return the rectangle of the cell
     */
    public Rectangle getRectangle() {
        return rectangle;
    }

    /**
     * Checks if the cell is burning.
     *
     * @return true if the cell is in BURNING or SCORCHED state, false otherwise
     */
    public boolean isBurning() {
        return this.state == State.BURNING || this.state == State.SCORCHED;
    }

    /**
     * Checks if the cell has been burned.
     *
     * @return true if the cell has been burned, false otherwise
     */
    public boolean isBurned() {
        return isBurned;
    }

    /**
     * Sets the burned status of the cell.
     *
     * @param burned the burned status to set
     */
    public void setBurned(boolean burned) {
        isBurned = burned;
    }
}