package com.yutnori.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SquareBoardTest {

    private SquareBoard board;
    private Player player;
    private Piece piece;

    @BeforeEach
    void setUp() {
        board = new SquareBoard();
        Player.resetCounter();
        player = new Player("Tester");
        piece = new Piece(0, player);
    }

    @Test
    void testInitialCellWhenNull() {
        Cell result = board.getDestinationCell(null, 1, board, piece);
        assertEquals(0, result.getId());
    }

    @Test
    void testOneStepFrom0() {
        Cell current = board.getCells().get(0);
        Cell result = board.getDestinationCell(current, 1, board, piece);
        assertEquals(1, result.getId());
    }

    @Test
    void testSpecialRouteFrom5() {
        Cell current = board.getCells().get(5); // 분기점
        Cell result = board.getDestinationCell(current, 1, board, piece);
        assertEquals(20, result.getId()); // special route로 진입
    }

    @Test
    void testHistoryStacked() {
        Cell current = board.getCells().get(0);
        board.getDestinationCell(current, 3, board, piece);

        assertEquals(3, piece.getHistory().size());
        assertEquals(2, piece.getHistory().peek()); // 마지막 방문 셀
    }

    @Test
    void testMultiStepMovement() {
        Cell current = board.getCells().get(0);
        Cell result = board.getDestinationCell(current, 3, board, piece);
        assertEquals(3, result.getId());
    }

    @Test
    void testIsCorner_returnsTrueForCornerIds() {
        // 0, 5, 10, 15는 코너
        assertTrue(board.isCorner(0));
        assertTrue(board.isCorner(5));
        assertTrue(board.isCorner(10));
        assertTrue(board.isCorner(15));
    }

    @Test
    void testIsCorner_returnsFalseForNonCornerIds() {
        // 1은 코너가 아님
        assertFalse(board.isCorner(1));
        assertFalse(board.isCorner(6));
    }

    @Test
    void testIsCentre_returnsTrueForCentreIds() {
        assertTrue(board.isCentre(22));
        assertTrue(board.isCentre(27));
    }

    @Test
    void testIsCentre_returnsFalseForNonCentreIds() {
        assertFalse(board.isCentre(0));
        assertFalse(board.isCentre(21));
    }
}