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
    private static final String APP_TITLE = "Sudoku 6×6 - Juego Principal"; // CORREGIDO: Título más descriptivo

    /**
     * Constructor privado para prevenir la creación de instancias externas.
     */
    private SudokuGameStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
        Parent root = loader.load();

        this.controller = loader.getController();

        Scene scene = new Scene(root);
        this.setTitle(APP_TITLE);
        this.setScene(scene);
        this.setResizable(false);

        // AÑADIDO: Centrar la ventana en la pantalla
        this.centerOnScreen();

        // Inicializa el tablero después de cargar la vista
        controller.initializeBoard();
    }

    /**
     * Método de acceso Singleton. Crea la instancia si no existe o la retorna.
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
