package com.brodwall.kata.sudoku;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class SudokuSolverTest {
    private SudokuBoard board = mock(SudokuBoard.class);
    private SudokuSolver solver = new SudokuSolver(board);

    @Before
    public void allCellsAreFilled() {
        when(board.isFilled(anyInt(), anyInt())).thenReturn(true);
    }

    @Test
    public void shouldSolveFilledBoard() {
        assertThat(solver.solve()).isTrue();
    }

    @Test
    public void shouldNotSolveBoardWhereCellHasNoOptions() throws Exception {
        when(board.isFilled(8, 8)).thenReturn(false);
        when(board.getOptionsForCell(8,8)).thenReturn(noOptions());

        assertThat(solver.solve()).isFalse();
    }

    @Test
    public void shouldSolveBoardWhereCellHasOneOption() throws Exception {
        when(board.isFilled(8, 8)).thenReturn(false);
        when(board.getOptionsForCell(8,8)).thenReturn(oneOption(4));

        assertThat(solver.solve()).isTrue();
        verify(board).setCellValue(8,8, 4);
    }

    @Test
    public void shouldTryNextOptionWhenNoOptionsForFutureCell() throws Exception {
        when(board.isFilled(7, 8)).thenReturn(false);
        when(board.getOptionsForCell(7,8)).thenReturn(options(1,2,3));

        when(board.isFilled(8, 8)).thenReturn(false);
        when(board.getOptionsForCell(8,8))
                .thenReturn(noOptions())
                .thenReturn(oneOption(1));

        assertThat(solver.solve()).isTrue();
        verify(board).setCellValue(7,8, 2);
        verify(board, never()).setCellValue(7,8, 3);
    }

    @Test
    public void shouldClearCellWhenNoSolutionFound() throws Exception {
        when(board.isFilled(7, 8)).thenReturn(false);
        when(board.getOptionsForCell(7,8)).thenReturn(options(1,2));

        when(board.isFilled(8, 8)).thenReturn(false);
        when(board.getOptionsForCell(8,8)).thenReturn(noOptions());

        assertThat(solver.solve()).isFalse();

        InOrder order = inOrder(board);
        order.verify(board).setCellValue(7,8, 1);
        order.verify(board).setCellValue(7,8, 2);
        order.verify(board).clearCell(7,8);
    }

    @Test
    public void shouldSolveCompleteGame() throws Exception {
        String puzzle = "..3.2.6..9..3.5..1..18.64....81.29..7.......8..67.82....26.95..8..2.3..9..5.1.3..";
        SudokuSolver solver = new SudokuSolver(puzzle);
        solver.solve();
        String[] lines = solver.dumpBoard().split("\n");
        for (String line : lines) {
            assertThat(line).matches("[1-9]{9}");
        }
    }

    private List<Integer> options(Integer... options) {
        return Arrays.asList(options);
    }

    private List<Integer> oneOption(int option) {
        return Arrays.asList(option);
    }

    private List<Integer> noOptions() {
        return new ArrayList<Integer>();
    }
}
