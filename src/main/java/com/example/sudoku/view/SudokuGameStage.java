package com.example.sudoku.view;

import com.example.sudoku.controller.SudokuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Custom Stage class for the Sudoku Game window.
 * Implements the Singleton pattern (advanced MVC).
 */
public class SudokuGameStage extends Stage {

    private SudokuController controller;
    private static SudokuGameStage instance;
    private static final String FXML_PATH = "/com/example/sudoku/sudoku-game-view.fxml";
    private static final String APP_TITLE = "Sudoku 6×6 - Main Game"; // FIXED: More descriptive title

    /**
     * Private constructor to prevent external instantiation.
     */
    private SudokuGameStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
        Parent root = loader.load();

        this.controller = loader.getController();

        Scene scene = new Scene(root);
        this.setTitle(APP_TITLE);
        this.setScene(scene);
        this.setResizable(false);

        // Center the window on the screen
        this.centerOnScreen();

        // Initializes the board after loading the view
        controller.initializeBoard();
    }

    /**
     * Singleton access method. Creates the instance if it doesn't exist, or returns it.
     */
    public static SudokuGameStage getInstance() throws IOException {
        if (instance == null) {
            instance = new SudokuGameStage();
        }
        return instance;
    }

    /**
     * Getter for the Controller (used for inter-controller communication).
     * @return The SudokuController instance.
     */
    public SudokuController getController() {
        return controller;
    }
}
