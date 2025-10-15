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

    private static SudokuWelcomeStage instance;

    private static final String FXML_PATH = "/com/example/sudoku/sudoku-welcome-view.fxml";
    private static final String APP_TITLE = "Sudoku 6×6 - Bienvenida"; // CORREGIDO: Título más largo

    /**
     * Constructor privado para prevenir la creación de instancias externas.
     */
    private SudokuWelcomeStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
        Parent root = loader.load();

        this.setTitle(APP_TITLE);
        this.setScene(new Scene(root));
        this.setResizable(false);

        // AÑADIDO: Centrar la ventana en la pantalla
        this.centerOnScreen();
    }

    /**
     * Método de acceso Singleton. Crea la instancia si no existe o la retorna.
     */
    public static SudokuWelcomeStage getInstance() throws IOException {
        if (instance == null) {
            instance = new SudokuWelcomeStage();
        }
        return instance;
    }
}