package com.example.sudoku.model;

/**
 * Represents a single cell in the Sudoku board.
 */
public class Cell {

    private int value;
    private final int row;
    private final int col;
    private final boolean isFixed; // Pre-filled cells cannot be changed
    private boolean isError;     // Used for real-time validation highlighting (HU-2)

    /**
     * Constructs a new Cell.
     * @param row The row index (0-5).
     * @param col The column index (0-5).
     * @param initialValue The initial value (0 for empty, 1-6 for fixed).
     */
    public Cell(int row, int col, int initialValue) {
        this.row = row;
        this.col = col;
        this.value = initialValue;
        this.isFixed = (initialValue != 0);
        this.isError = false;
    }

    // --- Getters ---
    public int getValue() { return value; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public boolean isFixed() { return isFixed; }
    public boolean isError() { return isError; }

    // --- Setters ---
    public void setValue(int value) {
        if (!isFixed) {
            this.value = value;
        }
    }
    public void setError(boolean error) { this.isError = error; }
}