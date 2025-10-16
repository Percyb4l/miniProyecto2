package com.example.sudoku.controller;

import com.example.sudoku.view.SudokuGameStage;
import com.example.sudoku.view.SudokuWelcomeStage;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.io.IOException;

/**
 * Controller for the Welcome Stage (sudoku-welcome-view.fxml).
 * Handles the 'Play' button action and manages the transition between windows.
 */
public class SudokuWelcomeController {

    @FXML
    private TextField nicknameTxt;

    // Este método es para que el jugador pueda darle a Enter en el campo de texto y empezar
    // el juego, en vez de tener que hacer clic en el botón. Es más cómodo.
    /**
     * Handles the key press event on the TextField.
     * If the pressed key is ENTER, it calls handlePlay.
     * @param event The keyboard event.
     * @throws IOException If a loading error occurs.
     */
    @FXML
    void handleEnterKey(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            // Call the game start logic
            handlePlay(new ActionEvent());
            event.consume(); // Consume the event so it doesn't propagate
        }
    }

    // Esta es la acción principal. Cuando el jugador le da al botón 'Jugar', este método se activa.
    // Lo que hace es esconder la ventana de bienvenida y mostrar la del juego.
    /**
     * Handles the 'Play Sudoku' button press, which initiates the game.
     * It hides the welcome screen and shows the main game screen.
     * @param event The action event (can be simulated by handleEnterKey).
     * @throws IOException If the FXML for the SudokuGameStage cannot be loaded.
     */
    @FXML
    void handlePlay(ActionEvent event) throws IOException {
        try {
            // Hide the welcome window (using the Singleton instance)
            SudokuWelcomeStage.getInstance().hide();

            // Show the game window. If it doesn't exist, it creates it.
            SudokuGameStage gameStage = SudokuGameStage.getInstance();
            gameStage.show();

        } catch (IOException e) {
            System.err.println("Error loading the main Sudoku game view.");
            e.printStackTrace();
        }
    }
}