package com.example.sudoku.controller;

import com.example.sudoku.model.Cell;
import com.example.sudoku.model.SudokuModel;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    private Button checkButton;
    @FXML
    private Button helpButton;
    @FXML
    private Button restartButton;
    @FXML
    private Label messageLabel;

    private SudokuModel model;
    private final Map<String, TextField> cellFields = new HashMap<>();
    private TextField selectedCell = null;
    private static final int SIZE = 6;

    @FXML
    public void initialize() {
        this.model = new SudokuModel();
    }

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

    private String getKey(int row, int col) { return row + "_" + col; }

    private TextField createCellField(int row, int col, Cell cellModel) {
        TextField cellField = new TextField(cellModel.getValue() != 0 ? String.valueOf(cellModel.getValue()) : "");
        cellField.setPrefSize(70, 70);
        cellField.setMinSize(70, 70);
        cellField.setMaxSize(70, 70);
        cellField.setAlignment(Pos.CENTER);
        cellField.setFont(Font.font("Arial", 26));
        cellField.setEditable(false); // CAMBIO: Deshabilitar edición directa
        cellField.setFocusTraversable(true);

        cellField.getProperties().put("row", row);
        cellField.getProperties().put("col", col);

        cellField.setOnMouseClicked(new CellClickHandler(row, col, cellModel.isFixed()));
        cellField.setOnKeyPressed(new KeyInputAdapter(this, row, col));

        cellField.getStyleClass().add("sudoku-cell");

        return cellField;
    }

    private void applyGridStyling(TextField cellField, int row, int col) {
        StringBuilder style = new StringBuilder();

        // Bordes base para todas las celdas
        style.append("-fx-border-style: solid; ");

        // Bordes LEFT
        if (col == 0) {
            style.append("-fx-border-left-width: 0; ");
        } else if (col == 3) {
            style.append("-fx-border-left-width: 4; -fx-border-left-color: #2C3E50; ");
        } else {
            style.append("-fx-border-left-width: 1; -fx-border-left-color: #BDC3C7; ");
        }

        // Bordes RIGHT
        if (col == 5) {
            style.append("-fx-border-right-width: 0; ");
        } else if (col == 2) {
            style.append("-fx-border-right-width: 4; -fx-border-right-color: #2C3E50; ");
        } else {
            style.append("-fx-border-right-width: 1; -fx-border-right-color: #BDC3C7; ");
        }

        // Bordes TOP
        if (row == 0) {
            style.append("-fx-border-top-width: 0; ");
        } else if (row == 2 || row == 4) {
            style.append("-fx-border-top-width: 4; -fx-border-top-color: #2C3E50; ");
        } else {
            style.append("-fx-border-top-width: 1; -fx-border-top-color: #BDC3C7; ");
        }

        // Bordes BOTTOM
        if (row == 5) {
            style.append("-fx-border-bottom-width: 0; ");
        } else if (row == 1 || row == 3) {
            style.append("-fx-border-bottom-width: 4; -fx-border-bottom-color: #2C3E50; ");
        } else {
            style.append("-fx-border-bottom-width: 1; -fx-border-bottom-color: #BDC3C7; ");
        }

        // Estilos de color según tipo de celda
        if (model.getCell(row, col).isFixed()) {
            style.append("-fx-background-color: #E8EAF6; -fx-font-weight: bold; -fx-text-fill: #3F51B5; ");
            cellField.getStyleClass().add("sudoku-cell-fixed");
        } else {
            style.append("-fx-background-color: white; -fx-text-fill: #2C3E50; ");
        }

        cellField.setStyle(style.toString());
    }

    private class CellClickHandler implements javafx.event.EventHandler<MouseEvent> {
        private final int row, col;
        private final boolean isFixed;

        public CellClickHandler(int row, int col, boolean isFixed) {
            this.row = row;
            this.col = col;
            this.isFixed = isFixed;
        }

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

    private void updateCellStyling(TextField cellField, boolean isSelected, boolean isError) {
        int row = (int)cellField.getProperties().get("row");
        int col = (int)cellField.getProperties().get("col");

        cellField.getStyleClass().removeAll("sudoku-cell-selected", "sudoku-cell-error", "sudoku-cell-hint");

        StringBuilder style = new StringBuilder();

        // Bordes base
        style.append("-fx-border-style: solid; ");

        // Bordes LEFT
        if (col == 0) {
            style.append("-fx-border-left-width: 0; ");
        } else if (col == 3) {
            style.append("-fx-border-left-width: 4; -fx-border-left-color: #2C3E50; ");
        } else {
            style.append("-fx-border-left-width: 1; -fx-border-left-color: #BDC3C7; ");
        }

        // Bordes RIGHT
        if (col == 5) {
            style.append("-fx-border-right-width: 0; ");
        } else if (col == 2) {
            style.append("-fx-border-right-width: 4; -fx-border-right-color: #2C3E50; ");
        } else {
            style.append("-fx-border-right-width: 1; -fx-border-right-color: #BDC3C7; ");
        }

        // Bordes TOP
        if (row == 0) {
            style.append("-fx-border-top-width: 0; ");
        } else if (row == 2 || row == 4) {
            style.append("-fx-border-top-width: 4; -fx-border-top-color: #2C3E50; ");
        } else {
            style.append("-fx-border-top-width: 1; -fx-border-top-color: #BDC3C7; ");
        }

        // Bordes BOTTOM
        if (row == 5) {
            style.append("-fx-border-bottom-width: 0; ");
        } else if (row == 1 || row == 3) {
            style.append("-fx-border-bottom-width: 4; -fx-border-bottom-color: #2C3E50; ");
        } else {
            style.append("-fx-border-bottom-width: 1; -fx-border-bottom-color: #BDC3C7; ");
        }

        // Aplicar estilos según el estado
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

    private void handleVictory() {
        showStyledAlert(Alert.AlertType.CONFIRMATION, "🏆 ¡VICTORIA!",
                "¡Felicitaciones! Has completado el Sudoku exitosamente.\n\n¡Eres un verdadero maestro del pensamiento lógico! 🎊");
        messageLabel.setText("🏆 ¡GANASTE! Presiona 'Reiniciar' para un nuevo desafío.");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

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