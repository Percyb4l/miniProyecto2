package com.example.sudoku.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The Model component in MVC. Manages the Sudoku board state and game logic.
 * Enhanced version to ensure each 2x3 block starts with exactly 2 fixed numbers.
 */
public class SudokuModel {

    private final Map<String, Cell> board;
    private static final int SIZE = 6;
    private static final int BLOCK_ROWS = 2;
    private static final int BLOCK_COLS = 3;

    /**
     * Constructs a new SudokuModel and initializes the board.
     */
    public SudokuModel() {
        this.board = new HashMap<>();
        resetBoard();
    }

    /**
     * Generates a unique string key for a cell based on its coordinates.
     * @param row The cell's row.
     * @param col The cell's column.
     * @return A string key like "row_col".
     */
    private String getKey(int row, int col) {
        return row + "_" + col;
    }

    /**
     * Resets the board by generating a new, random, solvable puzzle.
     * It starts from a solved template, shuffles the numbers, and then clears
     * most of the cells, leaving exactly 2 fixed numbers per 2x3 block.
     * This ensures compliance with HU-1 requirement.
     */
    public void resetBoard() {
        // 1. Start with a valid solved 6x6 Sudoku template
        int[][] solvedTemplate = {
                {1, 2, 3, 4, 5, 6},
                {4, 5, 6, 1, 2, 3},
                {2, 3, 1, 5, 6, 4},
                {5, 6, 4, 2, 3, 1},
                {3, 1, 2, 6, 4, 5},
                {6, 4, 5, 3, 1, 2}
        };

        // 2. Create a number mapping for randomization (1->3, 2->5, etc.)
        List<Integer> numbers = IntStream.rangeClosed(1, SIZE).boxed().collect(Collectors.toList());
        Collections.shuffle(numbers);
        Map<Integer, Integer> numberMap = new HashMap<>();
        for (int i = 0; i < SIZE; i++) {
            numberMap.put(i + 1, numbers.get(i));
        }

        // 3. Apply the mapping to create a randomized solved board
        int[][] randomizedSolvedBoard = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                randomizedSolvedBoard[r][c] = numberMap.get(solvedTemplate[r][c]);
            }
        }

        // 4. Create the puzzle by selecting exactly 2 cells per block as fixed
        // There are 6 blocks in a 6x6 grid (3 blocks horizontally × 2 blocks vertically)
        int[][] initialBoard = new int[SIZE][SIZE];
        Random random = new Random();

        // Process each 2x3 block
        for (int blockRow = 0; blockRow < SIZE / BLOCK_ROWS; blockRow++) {
            for (int blockCol = 0; blockCol < SIZE / BLOCK_COLS; blockCol++) {
                // Get all positions in this block
                List<int[]> blockPositions = new ArrayList<>();
                int startRow = blockRow * BLOCK_ROWS;
                int startCol = blockCol * BLOCK_COLS;

                for (int r = startRow; r < startRow + BLOCK_ROWS; r++) {
                    for (int c = startCol; c < startCol + BLOCK_COLS; c++) {
                        blockPositions.add(new int[]{r, c});
                    }
                }

                // Shuffle and select exactly 2 positions
                Collections.shuffle(blockPositions, random);
                for (int i = 0; i < 2; i++) {
                    int[] pos = blockPositions.get(i);
                    initialBoard[pos[0]][pos[1]] = randomizedSolvedBoard[pos[0]][pos[1]];
                }
            }
        }

        // 5. Initialize the board with Cell objects
        board.clear();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = new Cell(row, col, initialBoard[row][col]);
                board.put(getKey(row, col), cell);
            }
        }
        validateAllCells();
    }

    /**
     * Retrieves a cell from the board at the specified coordinates.
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return The Cell object at the given location.
     */
    public Cell getCell(int row, int col) {
        return board.get(getKey(row, col));
    }

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

    /**
     * Runs a full validation on the board. It checks every row, column, and
     * block for duplicate numbers and updates the error state of each cell accordingly.
     */
    public void validateAllCells() {
        // Clear all errors first
        for (Cell cell : board.values()) {
            cell.setError(false);
        }

        // Validate rows
        for (int i = 0; i < SIZE; i++) {
            validateLine(getCellsInRow(i));
        }

        // Validate columns
        for (int i = 0; i < SIZE; i++) {
            validateLine(getCellsInCol(i));
        }

        // Validate blocks
        for (int br = 0; br < SIZE / BLOCK_ROWS; br++) {
            for (int bc = 0; bc < SIZE / BLOCK_COLS; bc++) {
                validateLine(getCellsInBlock(br * BLOCK_ROWS, bc * BLOCK_COLS));
            }
        }
    }

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

    /**
     * Checks if there are any validation errors on the board.
     * @return true if there are errors, false otherwise.
     */
    public boolean hasErrors() {
        for (Cell cell : board.values()) {
            if (cell.isError()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Provides a smart, non-linear hint by finding a random empty cell
     * and returning its correct value from the solved board.
     * This ensures hints are not always given in sequential order.
     * @return A new Cell object containing the correct value for a randomly selected empty cell,
     * or null if the board is already solved or unsolvable.
     */
    public Cell getHint() {
        // 1. Collect all empty cells
        List<int[]> emptyCells = new ArrayList<>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (getCell(r, c).getValue() == 0) {
                    emptyCells.add(new int[]{r, c});
                }
            }
        }

        // If no empty cells, return null
        if (emptyCells.isEmpty()) {
            return null;
        }

        // 2. Solve the board to get correct values
        int[][] boardCopy = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                boardCopy[r][c] = getCell(r, c).getValue();
            }
        }

        if (!solve(boardCopy)) {
            return null; // Unsolvable board
        }

        // 3. Select a RANDOM empty cell (non-linear hint)
        Random random = new Random();
        int[] selectedCell = emptyCells.get(random.nextInt(emptyCells.size()));
        int row = selectedCell[0];
        int col = selectedCell[1];

        return new Cell(row, col, boardCopy[row][col]);
    }

    /**
     * Provides an intelligent hint by finding the empty cell with the fewest
     * possible valid options (most constrained cell). This gives a more
     * strategic hint that helps the player learn Sudoku techniques.
     * @return A new Cell object with the correct value for the most constrained cell,
     * or null if no empty cells exist.
     */
    public Cell getSmartHint() {
        // 1. Find the empty cell with fewest possible values
        int minOptions = SIZE + 1;
        List<int[]> bestCells = new ArrayList<>();

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (getCell(r, c).getValue() == 0) {
                    // Count how many numbers are possible for this cell
                    int possibleCount = countPossibleValues(r, c);

                    if (possibleCount < minOptions) {
                        minOptions = possibleCount;
                        bestCells.clear();
                        bestCells.add(new int[]{r, c});
                    } else if (possibleCount == minOptions) {
                        bestCells.add(new int[]{r, c});
                    }
                }
            }
        }

        if (bestCells.isEmpty()) {
            return null;
        }

        // If multiple cells have the same minimum options, pick randomly among them
        Random random = new Random();
        int[] selectedCell = bestCells.get(random.nextInt(bestCells.size()));
        int row = selectedCell[0];
        int col = selectedCell[1];

        // Get the correct value by solving
        int[][] boardCopy = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                boardCopy[r][c] = getCell(r, c).getValue();
            }
        }

        if (solve(boardCopy)) {
            return new Cell(row, col, boardCopy[row][col]);
        }

        return null;
    }

    /**
     * Counts how many valid numbers can be placed in a given cell.
     * Used by getSmartHint() to find the most constrained cells.
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return The count of possible valid values (1-6).
     */
    private int countPossibleValues(int row, int col) {
        int count = 0;
        int[][] tempBoard = new int[SIZE][SIZE];

        // Copy current board state
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                tempBoard[r][c] = getCell(r, c).getValue();
            }
        }

        // Test each number 1-6
        for (int num = 1; num <= SIZE; num++) {
            if (isSafe(tempBoard, row, col, num)) {
                count++;
            }
        }

        return count;
    }

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