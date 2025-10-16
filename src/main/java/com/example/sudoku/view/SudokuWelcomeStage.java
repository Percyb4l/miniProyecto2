package com.example.sudoku.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image; // Importar la clase Image
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Custom Stage for the Welcome screen.
 * Implements Singleton pattern to guarantee only one welcome window exists.
 */
public class SudokuWelcomeStage extends Stage {

    private static SudokuWelcomeStage instance;

    private static final String FXML_PATH = "/com/example/sudoku/sudoku-welcome-view.fxml";
    private static final String APP_TITLE = "Sudoku 6×6 - Bienvenida";
    //la ruta del ícono
    private static final String ICON_PATH = "/com/example/sudoku/favicon.png";

    /**
     * Constructor privado para prevenir la creación de instancias externas.
     */
    private SudokuWelcomeStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
        Parent root = loader.load();

        this.setTitle(APP_TITLE);
        this.setScene(new Scene(root));
        this.setResizable(false);
        this.centerOnScreen();

        this.getIcons().add(new Image(getClass().getResource(ICON_PATH).toExternalForm()));
    }

    /**
     * Singleton access method. Creates the instance if it doesn't exist, or returns it.
     */
    public static SudokuWelcomeStage getInstance() throws IOException {
        if (instance == null) {
            instance = new SudokuWelcomeStage();
        }
        return instance;
    }
}