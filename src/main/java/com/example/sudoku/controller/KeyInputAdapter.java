package com.example.sudoku.controller;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

/**
 * Adapter Class for KeyEvent handling.
 * Translates the low-level KeyEvent into a structured call to the Controller,
 * providing the necessary row and column context.
 */
public class KeyInputAdapter implements EventHandler<KeyEvent> {

    private final I_InputHandler controller;
    private final int row;
    private final int col;

    // Este es el constructor. A cada celda le creo uno de estos adaptadores
    // y le paso el controlador principal y la fila y columna de esa celda.
    // Así, el adaptador siempre sabe de dónde viene el evento.
    /**
     * Constructs the adapter.
     * @param controller The main controller that will handle the input.
     * @param row The row index of the cell this adapter is for.
     * @param col The column index of the cell this adapter is for.
     */
    public KeyInputAdapter(I_InputHandler controller, int row, int col) {
        this.controller = controller;
        this.row = row;
        this.col = col;
    }

    // Este es el método que se activa cuando el jugador presiona una tecla en una celda.
    // En vez de hacer toda la lógica aquí, simplemente llama al método del controlador
    // principal y le pasa el evento junto con la fila y columna que ya guardé.
    /**
     * Handles the KeyEvent.
     * This method delegates the event handling to the main controller, passing
     * along the cell's row and column context.
     * @param event The KeyEvent triggered by the user.
     */
    @Override
    public void handle(KeyEvent event) {
        // Delegate the handling to the actual controller, passing the context (row, col)
        controller.handleKeyInput(row, col, event);
        event.consume();
    }
}