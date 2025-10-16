package com.example.sudoku.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The Model component in MVC. Manages the Sudoku board state and game logic.
 */
public class SudokuModel {

    private final Map<String, Cell> board;
    private static final int SIZE = 6;
    private static final int BLOCK_ROWS = 2;
    private static final int BLOCK_COLS = 3;

    // Este es el constructor. Solo crea el hashmap para el tablero
    // y llama a resetBoard para que arme el primer juego.
    /**
     * Constructs a new SudokuModel and initializes the board.
     */
    public SudokuModel() {
        this.board = new HashMap<>();
        resetBoard();
    }

    // Un helper simple para crear una clave única para el hashmap, como "3_4" para la fila 3, col 4.
    /**
     * Generates a unique string key for a cell based on its coordinates.
     * @param row The cell's row.
     * @param col The cell's column.
     * @return A string key like "row_col".
     */
    private String getKey(int row, int col) {
        return row + "_" + col;
    }

    // Este es el método importante para empezar un juego nuevo. Coge un tablero resuelto,
    // baraja los números (o sea, todos los 1 se vuelven 5, etc.) y luego le abre
    // huecos para crear el tablero que ve el jugador. Así me aseguro que siempre tenga solución.
    /**
     * Resets the board by generating a new, random, solvable puzzle.
     * It starts from a solved template, shuffles the numbers, and then clears
     * most of the cells, leaving only a few fixed clues.
     */
    public void resetBoard() {
        // 1. Empezamos con una plantilla de un Sudoku 6x6 ya resuelto y válido.
        int[][] solvedTemplate = {
                {1, 2, 3, 4, 5, 6},
                {4, 5, 6, 1, 2, 3},
                {2, 3, 1, 5, 6, 4},
                {5, 6, 4, 2, 3, 1},
                {3, 1, 2, 6, 4, 5},
                {6, 4, 5, 3, 1, 2}
        };

        // Creamos una lista de números (1-6) y la barajamos para la aleatoriedad.
        // Esto nos servirá para mapear los números. Ej: todos los 1 se volverán 5, los 2 se volverán 3, etc.
        List<Integer> numbers = IntStream.rangeClosed(1, SIZE).boxed().collect(Collectors.toList());
        Collections.shuffle(numbers);
        Map<Integer, Integer> numberMap = new HashMap<>();
        for (int i = 0; i < SIZE; i++) {
            numberMap.put(i + 1, numbers.get(i));
        }

        // Creamos un nuevo tablero resuelto y aleatorio aplicando el mapeo de números.
        int[][] randomizedSolvedBoard = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                randomizedSolvedBoard[r][c] = numberMap.get(solvedTemplate[r][c]);
            }
        }

        // Creamos el tablero del puzle final, "quitando" las piezas que no queremos.
        int[][] initialBoard = new int[SIZE][SIZE];
        List<int[]> fixedPositions = Arrays.asList(
                new int[]{0, 0}, new int[]{0, 3},
                new int[]{2, 0}, new int[]{2, 3},
                new int[]{4, 0}, new int[]{4, 3}
        );

        for (int[] pos : fixedPositions) {
            int row = pos[0];
            int col = pos[1];
            initialBoard[row][col] = randomizedSolvedBoard[row][col];
        }

        board.clear();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = new Cell(row, col, initialBoard[row][col]);
                board.put(getKey(row, col), cell);
            }
        }
        validateAllCells();
    }

    // Un getter simple para sacar una celda del tablero usando su fila y columna.
    /**
     * Retrieves a cell from the board at the specified coordinates.
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return The Cell object at the given location.
     */
    public Cell getCell(int row, int col) {
        return board.get(getKey(row, col));
    }

    // Esto lo llama el controller cuando el jugador pone un número. Revisa que
    // la celda no sea una de las fijas, le pone el valor nuevo, y de una
    // corre la validación para ver si hay errores.
    /**
     * Sets the value of a cell, if it's not a fixed cell.
     * After setting the value, it triggers a full board validation.
     * @param row The row of the cell to change.
     * @param col The column of the cell to change.
     * @param value The new value (1-6, or 0 to clear).
     * @return true if the value was set, false otherwise.
     */
    public boolean setCellValue(int row, int col, int value) {
        Cell cell = getCell(row, col);
        if (cell != null && !cell.isFixed()) {
            cell.setValue(value);
            validateAllCells();
            return true;
        }
        return false;
    }

    // El método principal de chequeo. Primero borra todos los errores viejos.
    // Luego recorre cada fila, columna y bloque para encontrar números repetidos.
    /**
     * Runs a full validation on the board. It checks every row, column, and
     * block for duplicate numbers and updates the error state of each cell accordingly.
     */
    public void validateAllCells() {
        for (Cell cell : board.values()) {
            cell.setError(false);
        }
        for (int i = 0; i < SIZE; i++) {
            validateLine(getCellsInRow(i));
            validateLine(getCellsInCol(i));
        }
        for (int br = 0; br < SIZE / BLOCK_ROWS; br++) {
            for (int bc = 0; bc < SIZE / BLOCK_COLS; bc++) {
                validateLine(getCellsInBlock(br * BLOCK_ROWS, bc * BLOCK_COLS));
            }
        }
    }

    // Un helper para validateAllCells. Coge un grupo de celdas (fila, columna o bloque)
    // y usa un hashmap para contar cuántas veces aparece cada número.
    // Si un número sale más de una vez, marca la celda como error.
    /**
     * Validates a single line (a row, column, or block) for duplicate numbers.
     * @param cells An array of Cells representing the line to check.
     */
    private void validateLine(Cell[] cells) {
        Map<Integer, Integer> valueCount = new HashMap<>();
        for (Cell cell : cells) {
            int val = cell.getValue();
            if (val != 0) {
                valueCount.put(val, valueCount.getOrDefault(val, 0) + 1);
            }
        }
        for (Cell cell : cells) {
            int val = cell.getValue();
            if (val != 0 && valueCount.get(val) > 1) {
                cell.setError(true);
            }
        }
    }

    // Coge todas las celdas de una fila y las mete en un array.
    /**
     * Gets all cells in a specific row.
     * @param row The index of the row.
     * @return An array of Cell objects.
     */
    private Cell[] getCellsInRow(int row) {
        Cell[] cells = new Cell[SIZE];
        for (int col = 0; col < SIZE; col++) {
            cells[col] = getCell(row, col);
        }
        return cells;
    }

    // Coge todas las celdas de una columna y las mete en un array.
    /**
     * Gets all cells in a specific column.
     * @param col The index of the column.
     * @return An array of Cell objects.
     */
    private Cell[] getCellsInCol(int col) {
        Cell[] cells = new Cell[SIZE];
        for (int row = 0; row < SIZE; row++) {
            cells[row] = getCell(row, col);
        }
        return cells;
    }

    // Coge todas las celdas de un bloque 2x3.
    /**
     * Gets all cells in a specific 2x3 block.
     * @param startRow The starting row of the block.
     * @param startCol The starting column of the block.
     * @return An array of Cell objects.
     */
    private Cell[] getCellsInBlock(int startRow, int startCol) {
        Cell[] cells = new Cell[BLOCK_ROWS * BLOCK_COLS];
        int k = 0;
        for (int r = 0; r < BLOCK_ROWS; r++) {
            for (int c = 0; c < BLOCK_COLS; c++) {
                cells[k++] = getCell(startRow + r, startCol + c);
            }
        }
        return cells;
    }

    // Revisa si el jugador ya ganó. Primero corre la validación, y luego
    // simplemente mira si alguna celda está vacía o tiene un error.
    /**
     * Checks if the entire board is solved correctly. A solved board
     * has no empty cells (value 0) and no validation errors.
     * @return true if the board is solved, false otherwise.
     */
    public boolean isBoardSolved() {
        validateAllCells();
        for (Cell cell : board.values()) {
            if (cell.getValue() == 0 || cell.isError()) {
                return false;
            }
        }
        return true;
    }

    // La lógica del botón de pista. Hace una copia del tablero, corre el método 'solve'
    // en la copia, y luego busca el primer espacio vacío para darle al jugador la respuesta.
    /**
     * Provides a hint by finding the solution for the first available empty cell.
     * It creates a copy of the current board and runs the solver on it.
     * @return A new Cell object containing the correct value for an empty cell,
     * or null if the board is already solved or unsolvable.
     */
    public Cell getHint() {
        int[][] boardCopy = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                boardCopy[r][c] = getCell(r, c).getValue();
            }
        }

        if (solve(boardCopy)) {
            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    if (getCell(r, c).getValue() == 0) {
                        return new Cell(r, c, boardCopy[r][c]);
                    }
                }
            }
        }
        return null;
    }

    // Este es el solver recursivo, el 'backtracking' que vimos en clase.
    // Intenta un número, se llama a sí mismo para resolver el resto, y si falla,
    // se devuelve y prueba con el siguiente número. Es pesado pero funciona.
    /**
     * Solves a Sudoku board using a recursive backtracking algorithm.
     * @param board A 2D integer array representing the board.
     * @return true if a solution was found, false otherwise.
     */
    private boolean solve(int[][] board) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == 0) {
                    for (int num = 1; num <= SIZE; num++) {
                        if (isSafe(board, r, c, num)) {
                            board[r][c] = num;
                            if (solve(board)) {
                                return true;
                            }
                            board[r][c] = 0; // Backtrack
                        }
                    }
                    return false;
                }
            }
        }
        return true; // Solved
    }

    // Un helper para el solver. Solo revisa si un número se puede poner en un
    // sitio sin romper las reglas (revisa fila, columna y bloque).
    /**
     * Checks if it is safe to place a number in a given cell according to Sudoku rules.
     * @param board The board state.
     * @param row The row to check.
     * @param col The column to check.
     * @param num The number to check.
     * @return true if the placement is safe, false otherwise.
     */
    private boolean isSafe(int[][] board, int row, int col, int num) {
        // Check row
        for (int c = 0; c < SIZE; c++) {
            if (board[row][c] == num) return false;
        }
        // Check column
        for (int r = 0; r < SIZE; r++) {
            if (board[r][col] == num) return false;
        }
        // Check block
        int blockStartRow = row - row % BLOCK_ROWS;
        int blockStartCol = col - col % BLOCK_COLS;
        for (int r = 0; r < BLOCK_ROWS; r++) {
            for (int c = 0; c < BLOCK_COLS; c++) {
                if (board[blockStartRow + r][blockStartCol + c] == num) return false;
            }
        }
        return true;
    }
}