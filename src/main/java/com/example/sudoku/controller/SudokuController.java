package com.example.sudoku.controller;

import com.example.sudoku.model.Cell;
import com.example.sudoku.model.SudokuModel;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import java.util.HashMap;
import java.util.Map;

/**
 * The Controller component in MVC for the main game board.
 * Handles user input and updates the Model and View with enhanced styling.
 */
public class SudokuController implements I_InputHandler {

    @FXML
    private GridPane sudokuGrid;

    @FXML
    private Label messageLabel;

    private SudokuModel model;
    private final Map<String, TextField> cellFields = new HashMap<>();
    private TextField selectedCell = null;
    private static final int SIZE = 6;

    // Este método se llama solo cuando se carga el FXML. Aquí es donde creo el modelo del juego.
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        this.model = new SudokuModel();
    }

    // Este método es clave, arma todo el tablero visual. Lo llamo desde el SudokuGameStage
    // después de que se crea la ventana para que dibuje todos los cuadritos.
    /**
     * Creates and populates the sudoku grid with TextField cells. This method
     * sets up the initial visual state of the board.
     */
    public void initializeBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cellModel = model.getCell(row, col);
                TextField cellField = createCellField(row, col, cellModel);

                sudokuGrid.add(cellField, col, row);
                cellFields.put(getKey(row, col), cellField);

                applyGridStyling(cellField, row, col);
            }
        }
        updateView();
    }

    // Un helper para la clave del mapa, para no repetir código.
    /**
     * Generates a unique string key for a cell based on its coordinates.
     * @param row The cell's row.
     * @param col The cell's column.
     * @return A string key like "row_col".
     */
    private String getKey(int row, int col) { return row + "_" + col; }

    // Aquí es donde creo cada cuadrito (TextField) del sudoku. Le pongo el tamaño,
    // la fuente y lo más importante, los listeners para el clic y las teclas.
    /**
     * Creates a single TextField to represent a cell in the grid.
     * It configures the size, font, and event listeners for the cell.
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @param cellModel The data model for the cell.
     * @return A configured TextField for the cell.
     */
    private TextField createCellField(int row, int col, Cell cellModel) {
        TextField cellField = new TextField(cellModel.getValue() != 0 ? String.valueOf(cellModel.getValue()) : "");
        cellField.setPrefSize(70, 70);
        cellField.setMinSize(70, 70);
        cellField.setMaxSize(70, 70);
        cellField.setAlignment(Pos.CENTER);
        cellField.setFont(Font.font("Arial", 26));
        cellField.setEditable(false); // Lo puse no editable para que solo funcione con las teclas 1-6
        cellField.setFocusTraversable(true);

        cellField.getProperties().put("row", row);
        cellField.getProperties().put("col", col);

        cellField.setOnMouseClicked(new CellClickHandler(row, col, cellModel.isFixed()));
        cellField.setOnKeyPressed(new KeyInputAdapter(this, row, col));

        cellField.getStyleClass().add("sudoku-cell");

        return cellField;
    }

    // Este método se encarga de ponerle los bordes gruesos al tablero para que
    // se vea como un sudoku de verdad. Mucho if-else pero funciona.
    /**
     * Applies the initial CSS styling to a cell, including the thick borders
     * that define the 2x3 blocks.
     * @param cellField The TextField to style.
     * @param row The row of the cell.
     * @param col The column of the cell.
     */
    private void applyGridStyling(TextField cellField, int row, int col) {
        StringBuilder style = new StringBuilder();

        // Base borders for all cells
        style.append("-fx-border-style: solid; ");

        // LEFT borders
        if (col == 0) {
            style.append("-fx-border-left-width: 0; ");
        } else if (col == 3) {
            style.append("-fx-border-left-width: 4; -fx-border-left-color: #2C3E50; ");
        } else {
            style.append("-fx-border-left-width: 1; -fx-border-left-color: #BDC3C7; ");
        }

        // RIGHT borders
        if (col == 5) {
            style.append("-fx-border-right-width: 0; ");
        } else if (col == 2) {
            style.append("-fx-border-right-width: 4; -fx-border-right-color: #2C3E50; ");
        } else {
            style.append("-fx-border-right-width: 1; -fx-border-right-color: #BDC3C7; ");
        }

        // TOP borders
        if (row == 0) {
            style.append("-fx-border-top-width: 0; ");
        } else if (row == 2 || row == 4) {
            style.append("-fx-border-top-width: 4; -fx-border-top-color: #2C3E50; ");
        } else {
            style.append("-fx-border-top-width: 1; -fx-border-top-color: #BDC3C7; ");
        }

        // BOTTOM borders
        if (row == 5) {
            style.append("-fx-border-bottom-width: 0; ");
        } else if (row == 1 || row == 3) {
            style.append("-fx-border-bottom-width: 4; -fx-border-bottom-color: #2C3E50; ");
        } else {
            style.append("-fx-border-bottom-width: 1; -fx-border-bottom-color: #BDC3C7; ");
        }

        // Color styles based on cell type
        if (model.getCell(row, col).isFixed()) {
            style.append("-fx-background-color: #E8EAF6; -fx-font-weight: bold; -fx-text-fill: #3F51B5; ");
            cellField.getStyleClass().add("sudoku-cell-fixed");
        } else {
            style.append("-fx-background-color: white; -fx-text-fill: #2C3E50; ");
        }

        cellField.setStyle(style.toString());
    }

    // Hice una clase interna para manejar los clics. Así el código queda más ordenado
    // y no tengo que poner todo en un solo método gigante.
    /**
     * An inner class to handle mouse click events on cells.
     */
    private class CellClickHandler implements javafx.event.EventHandler<MouseEvent> {
        private final int row, col;
        private final boolean isFixed;

        public CellClickHandler(int row, int col, boolean isFixed) {
            this.row = row;
            this.col = col;
            this.isFixed = isFixed;
        }

        /**
         * Handles the mouse click event on a cell. It selects the cell if it's not fixed.
         * @param event The mouse event.
         */
        @Override
        public void handle(MouseEvent event) {
            if (event.getSource() instanceof TextField) {
                TextField clickedCell = (TextField) event.getSource();
                if (selectedCell != null) {
                    updateCellStyling(selectedCell, false, false);
                }
                if (!isFixed) {
                    selectedCell = clickedCell;
                    selectedCell.requestFocus();
                    updateCellStyling(selectedCell, true, false);
                } else {
                    selectedCell = null;
                }
                updateView();
            }
        }
    }

    // Este es el método que la interfaz I_InputHandler me obliga a tener.
    // El KeyInputAdapter lo llama y me pasa la tecla que se presionó y en qué celda fue.
    /**
     * Handles key input for a selected cell. This method is called by the
     * KeyInputAdapter and fulfills the I_InputHandler interface contract.
     * @param row The row of the cell where the key was pressed.
     * @param col The column of the cell.
     * @param keyEvent The key event.
     */
    @Override
    public void handleKeyInput(int row, int col, KeyEvent keyEvent) {
        if (selectedCell == null || !selectedCell.getProperties().get("row").equals(row)) {
            return;
        }

        int value = 0;
        String key = keyEvent.getText();

        if (key.matches("[1-6]")) {
            value = Integer.parseInt(key);
        } else if (keyEvent.getCode() == KeyCode.BACK_SPACE || keyEvent.getCode() == KeyCode.DELETE) {
            value = 0;
        } else {
            showAlert(Alert.AlertType.WARNING, "Entrada Inválida",
                    "Por favor, ingresa únicamente números del 1 al 6.");
            keyEvent.consume();
            return;
        }

        model.setCellValue(row, col, value);
        updateView();

        if (model.isBoardSolved()) {
            handleVictory();
        }
    }

    // Este es mi método para "refrescar" la pantalla. Lo llamo cada vez que algo cambia
    // para que el tablero muestre los números y colores correctos.
    /**
     * Updates the entire view based on the current state of the model. It refreshes
     * cell values and styles (like errors or selections).
     */
    public void updateView() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cellModel = model.getCell(row, col);
                TextField cellField = cellFields.get(getKey(row, col));
                int value = cellModel.getValue();
                cellField.setText(value != 0 ? String.valueOf(value) : "");
                boolean isSelected = (selectedCell == cellField);
                updateCellStyling(cellField, isSelected, cellModel.isError());
            }
        }
        messageLabel.setText("👆 Haz clic en una celda y usa las teclas 1-6 para jugar");
    }

    // Un helper para cambiar el color de una celda dependiendo de si está
    // seleccionada, tiene un error, o es una de las fijas.
    /**
     * Updates the specific CSS styling of a single cell based on its state
     * (selected, error, fixed, etc.).
     * @param cellField The TextField to style.
     * @param isSelected Whether the cell is currently selected.
     * @param isError Whether the cell has a validation error.
     */
    private void updateCellStyling(TextField cellField, boolean isSelected, boolean isError) {
        int row = (int)cellField.getProperties().get("row");
        int col = (int)cellField.getProperties().get("col");

        cellField.getStyleClass().removeAll("sudoku-cell-selected", "sudoku-cell-error", "sudoku-cell-hint");

        StringBuilder style = new StringBuilder();
        // Apply styles based on state
        if (isError) {
            style.append("-fx-background-color: #FFCDD2; -fx-text-fill: #C62828; ");
            cellField.getStyleClass().add("sudoku-cell-error");
        } else if (isSelected) {
            style.append("-fx-background-color: #BBDEFB; -fx-text-fill: #1565C0; ");
            cellField.getStyleClass().add("sudoku-cell-selected");
        } else if (model.getCell(row, col).isFixed()) {
            style.append("-fx-background-color: #E8EAF6; -fx-text-fill: #3F51B5; -fx-font-weight: bold; ");
        } else {
            style.append("-fx-background-color: white; -fx-text-fill: #2C3E50; ");
        }

        cellField.setStyle(style.toString());
    }

    // La acción para el botón de 'Verificar'. Llama al modelo para ver si el
    // tablero está resuelto y muestra un mensaje dependiendo del resultado.
    /**
     * Handles the action for the 'Check Board' button. It checks if the board is
     * solved and shows an appropriate alert.
     */
    @FXML
    private void handleCheckBoard() {
        if (model.isBoardSolved()) {
            showStyledAlert(Alert.AlertType.INFORMATION, "🎉 ¡Felicitaciones!",
                    "¡Has resuelto el Sudoku correctamente! Eres un maestro de la lógica.");
        } else {
            showStyledAlert(Alert.AlertType.ERROR, "❌ Verificación del Tablero",
                    "El tablero aún no está resuelto o contiene errores (resaltados en rojo).");
        }
        updateView();
    }

    // La acción para el botón de 'Pista'. Le pide una pista al modelo y la pone en el tablero.
    // También le pone un colorcito verde para que se note cuál fue la pista.
    /**
     * Handles the action for the 'Hint' button. It gets a hint from the model,
     * updates the view, and highlights the new cell.
     */
    @FXML
    private void handleHelpOption() {
        Cell hint = model.getHint();
        if (hint != null) {
            model.setCellValue(hint.getRow(), hint.getCol(), hint.getValue());
            updateView();

            TextField cellField = cellFields.get(getKey(hint.getRow(), hint.getCol()));
            String currentStyle = cellField.getStyle();
            cellField.setStyle(currentStyle + " -fx-background-color: #C8E6C9; -fx-text-fill: #2E7D32;");
            cellField.getStyleClass().add("sudoku-cell-hint");

            messageLabel.setText(String.format("💡 Pista proporcionada: %d colocado en (%d, %d).",
                    hint.getValue(), hint.getRow() + 1, hint.getCol() + 1));

            if (model.isBoardSolved()) {
                handleVictory();
            }
        } else {
            showStyledAlert(Alert.AlertType.INFORMATION, "💡 Pista",
                    "El tablero ya está completo o no hay movimientos válidos posibles.");
        }
    }

    // La acción para el botón de 'Reiniciar'. Llama a resetBoard() en el modelo
    // y actualiza toda la vista para empezar de cero con un tablero nuevo.
    /**
     * Handles the action for the 'Restart Game' button. It resets the model to a
     * new puzzle and updates the entire view.
     */
    @FXML
    private void handleRestartGame() {
        model.resetBoard();

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cellModel = model.getCell(row, col);
                TextField cellField = cellFields.get(getKey(row, col));

                cellField.setText(cellModel.getValue() != 0 ? String.valueOf(cellModel.getValue()) : "");
                cellField.setEditable(false);

                cellField.getStyleClass().removeAll("sudoku-cell-selected", "sudoku-cell-error", "sudoku-cell-hint");

                applyGridStyling(cellField, row, col);
            }
        }

        selectedCell = null;
        updateView();
        messageLabel.setText("🔄 ¡Juego reiniciado! Nuevo desafío cargado. ¡Buena suerte!");
    }

    // Este método se llama cuando el jugador gana. Solo muestra un mensaje bonito de felicitación.
    /**
     * Displays a victory message when the board is successfully solved.
     */
    private void handleVictory() {
        showStyledAlert(Alert.AlertType.CONFIRMATION, "🏆 ¡VICTORIA!",
                "¡Felicitaciones! Has completado el Sudoku exitosamente.\n\n¡Eres un verdadero maestro del pensamiento lógico! 🎊");
        messageLabel.setText("🏆 ¡GANASTE! Presiona 'Reiniciar' para un nuevo desafío.");
    }

    // Un par de helpers para no repetir el código de las alertas a cada rato.
    // Uno es para las alertas normales y el otro para las que tienen mi estilo personalizado.
    /**
     * Displays a standard alert dialog.
     * @param type The type of alert.
     * @param title The title of the alert.
     * @param content The content message.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays a custom-styled alert dialog.
     * @param type The type of alert.
     * @param title The title of the alert.
     * @param content The content message.
     */
    private void showStyledAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.getDialogPane().setStyle(
                "-fx-font-family: 'Segoe UI', Arial, sans-serif; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-color: #f5f7fa; " +
                        "-fx-padding: 20;"
        );

        alert.getDialogPane().lookup(".content.label").setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-text-fill: #2C3E50; " +
                        "-fx-padding: 10;"
        );

        alert.showAndWait();
    }
}