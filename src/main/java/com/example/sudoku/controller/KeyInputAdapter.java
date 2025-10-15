package com.example.sudoku.controller;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

/**
 * Adapter Class for KeyEvent handling (Criterio: Clases Adaptadoras).
 * Translates the low-level KeyEvent into a structured call to the Controller,
 * providing the necessary row and column context (HU-1).
 */
public class KeyInputAdapter implements EventHandler<KeyEvent> {

    private final I_InputHandler controller;
    private final int row;
    private final int col;

    /**
     * Constructs the adapter.
     */
    public KeyInputAdapter(I_InputHandler controller, int row, int col) {
        this.controller = controller;
        this.row = row;
        this.col = col;
    }

    @Override
    public void handle(KeyEvent event) {
        // Delegate the handling to the actual controller, passing the context (row, col)
        controller.handleKeyInput(row, col, event);
        event.consume();
    }
}