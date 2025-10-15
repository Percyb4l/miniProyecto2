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
 * The Controller component in MVC for the com.example.sudoku.main.main game board.
 * Handles user input and updates the Model and View.
 */
public class SudokuController implements I_InputHandler {

    @FXML
    private GridPane sudokuGrid;
    @FXML
    private Button checkButton;
    @FXML
    private Button helpButton;
    @FXML
    private Label messageLabel;

    private SudokuModel model;
    private final Map<String, TextField> cellFields = new HashMap<>();
    private TextField selectedCell = null;
    private static final int SIZE = 6;

    /**
     * Initializes the model. Called automatically after FXML is loaded.
     */
    @FXML
    public void initialize() {
        this.model = new SudokuModel();
    }

    /**
     * Dynamically builds the Sudoku board GUI (View).
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

    private String getKey(int row, int col) { return row + "_" + col; }

    /**
     * Creates and configures a TextField for a cell.
     */
    private TextField createCellField(int row, int col, Cell cellModel) {
        TextField cellField = new TextField(cellModel.getValue() != 0 ? String.valueOf(cellModel.getValue()) : "");
        cellField.setPrefSize(40, 40);
        cellField.setMaxSize(40, 40);
        cellField.setAlignment(Pos.CENTER);
        cellField.setFont(Font.font("Arial", 20));
        cellField.setEditable(!cellModel.isFixed());

        // Store context in properties
        cellField.getProperties().put("row", row);
        cellField.getProperties().put("col", col);

        // Attach event handlers (HU-1)
        cellField.setOnMouseClicked(new CellClickHandler(row, col, cellModel.isFixed()));
        cellField.setOnKeyPressed(new KeyInputAdapter(this, row, col));

        return cellField;
    }

    /**
     * Applies CSS styling for block separation.
     */
    private void applyGridStyling(TextField cellField, int row, int col) {
        String style = "-fx-border-color: black;";

        if (col % 3 == 2 && col != SIZE - 1) { style += "-fx-border-right-width: 3;"; } else { style += "-fx-border-right-width: 1;"; }
        if (row % 2 == 1 && row != SIZE - 1) { style += "-fx-border-bottom-width: 3;"; } else { style += "-fx-border-bottom-width: 1;"; }

        if (model.getCell(row, col).isFixed()) {
            style += " -fx-background-color: #d3d3d3; -fx-font-weight: bold;";
        } else {
            style += " -fx-background-color: white;";
        }
        cellField.setStyle(style);
    }

    /**
     * Internal Class for handling Mouse Click events on cells (Criterio: Clases Internas).
     * Handles cell selection (HU-1).
     */
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

    /**
     * Implements I_InputHandler. Processes keyboard input (HU-1).
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
            value = 0; // Empty the cell
        } else {
            // MODIFICACIÓN: Muestra alerta para entradas inválidas
            showAlert(Alert.AlertType.WARNING, "Entrada Inválida", "Por favor, ingresa únicamente números del 1 al 6.");
            keyEvent.consume();
            return;
        }

        model.setCellValue(row, col, value);
        updateView();

        if (model.isBoardSolved()) {
            handleVictory();
        }
    }

    /**
     * Updates all TextField elements based on the Model state (HU-2).
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
        messageLabel.setText("Click a cell and use 1-6 keys to play.");
    }

    /**
     * Applies styling for selection and error.
     */
    private void updateCellStyling(TextField cellField, boolean isSelected, boolean isError) {
        String baseStyle = cellField.getStyle().replaceAll(" -fx-background-color: #[a-fA-F0-9]{6};", "");
        if (isError) {
            baseStyle += " -fx-background-color: #ffcccc; -fx-text-fill: red;";
        } else if (isSelected) {
            baseStyle += " -fx-background-color: #add8e6; -fx-text-fill: black;";
        } else if (model.getCell((int)cellField.getProperties().get("row"), (int)cellField.getProperties().get("col")).isFixed()) {
            baseStyle += " -fx-background-color: #d3d3d3; -fx-text-fill: black;";
        } else {
            baseStyle += " -fx-background-color: white; -fx-text-fill: black;";
        }
        cellField.setStyle(baseStyle);
    }

    /**
     * Handles the "Check Board" button action (HU-3).
     */
    @FXML
    private void handleCheckBoard() {
        if (model.isBoardSolved()) {
            showAlert(Alert.AlertType.INFORMATION, "Congratulations!", "You have solved the Sudoku puzzle correctly!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Board Check", "The board is not yet solved or contains errors (highlighted in red).");
        }
        updateView();
    }

    /**
     * Handles the "Hint" button action (HU-4).
     */
    @FXML
    private void handleHelpOption() {
        Cell hint = model.getHint();
        if (hint != null) {
            model.setCellValue(hint.getRow(), hint.getCol(), hint.getValue());
            updateView();
            TextField cellField = cellFields.get(getKey(hint.getRow(), hint.getCol()));
            cellField.setStyle(cellField.getStyle() + " -fx-background-color: #90ee90;");
            messageLabel.setText(String.format("Hint provided: %d placed at (%d, %d).", hint.getValue(), hint.getRow() + 1, hint.getCol() + 1));
            if (model.isBoardSolved()) { handleVictory(); }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Hint", "The board is already complete or no valid move is possible.");
        }
    }

    /**
     * MÉTODO NUEVO: Handles the "Restart Game" button action.
     */
    @FXML
    private void handleRestartGame() {
        model.resetBoard();
        updateView();
        messageLabel.setText("Juego reiniciado. ¡Buena suerte!");
    }

    private void handleVictory() {
        showAlert(Alert.AlertType.CONFIRMATION, "Victory!", "You have won! The game is complete.");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}