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

    public SudokuModel() {
        this.board = new HashMap<>();
        resetBoard();
    }

    private String getKey(int row, int col) {
        return row + "_" + col;
    }

    /**
     * Resets the board by generating a complete, solved grid, randomizing it,
     * and then removing numbers to leave only the 6 fixed clues.
     * This guarantees every puzzle is solvable.
     */
    public void resetBoard() {
        // --- LÓGICA DE GENERACIÓN MEJORADA ---

        // 1. Empezamos con una plantilla de un Sudoku 6x6 ya resuelto y válido.
        int[][] solvedTemplate = {
                {1, 2, 3, 4, 5, 6},
                {4, 5, 6, 1, 2, 3},
                {2, 3, 1, 5, 6, 4},
                {5, 6, 4, 2, 3, 1},
                {3, 1, 2, 6, 4, 5},
                {6, 4, 5, 3, 1, 2}
        };

        // 2. Creamos una lista de números (1-6) y la barajamos para la aleatoriedad.
        // Esto nos servirá para mapear los números. Ej: todos los 1 se volverán 5, los 2 se volverán 3, etc.
        List<Integer> numbers = IntStream.rangeClosed(1, SIZE).boxed().collect(Collectors.toList());
        Collections.shuffle(numbers);
        Map<Integer, Integer> numberMap = new HashMap<>();
        for (int i = 0; i < SIZE; i++) {
            numberMap.put(i + 1, numbers.get(i));
        }

        // 3. Creamos un nuevo tablero resuelto y aleatorio aplicando el mapeo de números.
        int[][] randomizedSolvedBoard = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                randomizedSolvedBoard[r][c] = numberMap.get(solvedTemplate[r][c]);
            }
        }

        // 4. Creamos el tablero del puzle final, "quitando" las piezas que no queremos.
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

        // --- FIN DE LA LÓGICA DE GENERACIÓN ---

        board.clear();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = new Cell(row, col, initialBoard[row][col]);
                board.put(getKey(row, col), cell);
            }
        }
        validateAllCells();
    }

    // ... (El resto de tu clase permanece exactamente igual)


    public Cell getCell(int row, int col) {
        return board.get(getKey(row, col));
    }

    public boolean setCellValue(int row, int col, int value) {
        Cell cell = getCell(row, col);
        if (cell != null && !cell.isFixed()) {
            cell.setValue(value);
            validateAllCells();
            return true;
        }
        return false;
    }

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

    private Cell[] getCellsInRow(int row) {
        Cell[] cells = new Cell[SIZE];
        for (int col = 0; col < SIZE; col++) {
            cells[col] = getCell(row, col);
        }
        return cells;
    }

    private Cell[] getCellsInCol(int col) {
        Cell[] cells = new Cell[SIZE];
        for (int row = 0; row < SIZE; row++) {
            cells[row] = getCell(row, col);
        }
        return cells;
    }

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

    public boolean isBoardSolved() {
        validateAllCells();
        for (Cell cell : board.values()) {
            if (cell.getValue() == 0 || cell.isError()) {
                return false;
            }
        }
        return true;
    }

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
                            board[r][c] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isSafe(int[][] board, int row, int col, int num) {
        for (int c = 0; c < SIZE; c++) {
            if (board[row][c] == num) return false;
        }
        for (int r = 0; r < SIZE; r++) {
            if (board[r][col] == num) return false;
        }
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