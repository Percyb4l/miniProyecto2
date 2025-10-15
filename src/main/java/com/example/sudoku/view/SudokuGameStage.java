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
    private static SudokuGameStage instance; // 1. Variable estática para la única instancia
    // RUTA FXML: Asegúrate de que esta ruta coincida con el nombre de tu archivo FXML
    private static final String FXML_PATH = "/com/example/sudoku/sudoku-game-view.fxml";
    private static final String APP_TITLE = "Sudoku 6x6 Game (FPOE)";

    /**
     * Constructor privado para prevenir la creación de instancias externas.
     */
    private SudokuGameStage() throws IOException {
        // Carga el FXML y obtiene el controlador
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
        Parent root = loader.load();

        this.controller = loader.getController();

        // Configuración del Stage
        Scene scene = new Scene(root);
        this.setTitle(APP_TITLE);
        this.setScene(scene);
        this.setResizable(false);

        // Inicializa el tablero después de cargar la vista
        // Esta línea requería que 'this.controller' NO sea null, lo cual ya se resolvió.
        controller.initializeBoard();
    }

    /**
     * Método de acceso Singleton. Crea la instancia si no existe o la retorna.
     * Si este método estaba vacío o incompleto, AÑADE ESTE BLOQUE.
     */
    public static SudokuGameStage getInstance() throws IOException {
        if (instance == null) {
            // 2. Crea la instancia llamando al constructor privado
            instance = new SudokuGameStage();
        }
        // 3. Retorna la única instancia
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
