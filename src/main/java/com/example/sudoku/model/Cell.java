package com.example.sudoku.model;

/**
 * Represents a single cell in the Sudoku board.
 */
public class Cell {

    private int value;
    private final int row;
    private final int col;
    private final boolean isFixed; // To know if it's one of the starting numbers
    private boolean isError;     // To paint the cell red if there's a mistake

    // Este es el constructor de la celda. Cada vez que se crea una,
    // se le asigna su fila, columna y el número que tiene.
    // También decide si es una celda fija (si tiene un número desde el principio).
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
    // Métodos simples para poder ver los valores de la celda desde afuera.

    /**
     * Gets the current value of the cell.
     * @return The number in the cell (0 if empty).
     */
    public int getValue() { return value; }

    /**
     * Gets the row index of the cell.
     * @return The row index.
     */
    public int getRow() { return row; }

    /**
     * Gets the column index of the cell.
     * @return The column index.
     */
    public int getCol() { return col; }

    /**
     * Checks if the cell is a fixed (pre-filled) cell.
     * @return true if the cell is fixed, false otherwise.
     */
    public boolean isFixed() { return isFixed; }

    /**
     * Checks if the cell is currently marked as an error.
     * @return true if the cell has an error, false otherwise.
     */
    public boolean isError() { return isError; }

    // --- Setters ---

    // Este es para cambiar el número de la celda.
    // Le puse un 'if' para que no me deje cambiar las celdas que son fijas.
    /**
     * Sets the value of the cell.
     * This operation will only succeed if the cell is not fixed.
     * @param value The new value to set.
     */
    public void setValue(int value) {
        if (!isFixed) {
            this.value = value;
        }
    }

    // Un setter simple para marcar si la celda tiene un error o no.
    /**
     * Sets the error state of the cell.
     * @param error true to mark the cell as an error, false to clear it.
     */
    public void setError(boolean error) { this.isError = error; }
}