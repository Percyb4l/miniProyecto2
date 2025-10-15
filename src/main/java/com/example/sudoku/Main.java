package com.example.sudoku;

import com.example.sudoku.view.SudokuWelcomeStage;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Main application class (Lanzador).
 * This class serves as the entry point for the JavaFX application.
 */
public class Main extends Application { // CLASE LLAMADA 'main' EN MINÚSCULAS

    @Override
    public void start(Stage primaryStage) {
        try {
            // Si getInstance() estaba en rojo, esta línea debería ahora compilar
            SudokuWelcomeStage welcomeStage = SudokuWelcomeStage.getInstance();
            welcomeStage.show();
        } catch (IOException e) {
            System.err.println("Error initializing the welcome stage.");
            e.printStackTrace();
        }
    }

    /**
     * Método main estándar de Java para lanzar la aplicación.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Llama al método launch() de javafx.application.Application
        launch(args);
    }
}
