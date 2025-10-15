package com.example.sudoku.controller;

import javafx.scene.input.KeyEvent;

/**
 * Interface for handling specific inputs (Criterio: Interfaces).
 * Allows the KeyInputAdapter to communicate key events back to the com.example.sudoku.main.main Controller.
 */
public interface I_InputHandler {

    /**
     * Handles a key press event at a specific board location.
     * @param row The row index.
     * @param col The column index.
     * @param keyEvent The key event details.
     */
    void handleKeyInput(int row, int col, KeyEvent keyEvent);
}
