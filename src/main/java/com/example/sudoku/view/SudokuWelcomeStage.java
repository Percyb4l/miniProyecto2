package com.example.sudoku.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Custom Stage for the Welcome screen.
 * Implements Singleton pattern to guarantee only one welcome window exists.
 */
public class SudokuWelcomeStage extends Stage {

    private static SudokuWelcomeStage instance; // 1. Variable estática para la única instancia

    // RUTA FXML: Asegúrate de que esta ruta coincida con el nombre de tu archivo FXML
    private static final String FXML_PATH = "/com/example/sudoku/sudoku-welcome-view.fxml";
    private static final String APP_TITLE = "Sudoku Welcome";

    /**
     * Constructor privado para prevenir la creación de instancias externas.
     */
    private SudokuWelcomeStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
        Parent root = loader.load();

        this.setTitle(APP_TITLE);
        this.setScene(new Scene(root));
        this.setResizable(false);
    }

    /**
     * Método de acceso Singleton. Crea la instancia si no existe o la retorna.
     * Si el método getInstance() estaba vacío, AÑADE TODO ESTE BLOQUE.
     */
    public static SudokuWelcomeStage getInstance() throws IOException {
        if (instance == null) {
            // 2. Crea la instancia llamando al constructor privado
            instance = new SudokuWelcomeStage();
        }
        // 3. Retorna la única instancia
        return instance;
    }
}