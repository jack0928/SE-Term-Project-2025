package com.yutnori.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PentagonBoardTest {

    private PentagonBoard board;
    private Player player;
    private Piece piece;

    @BeforeEach
    void setUp() {
        board = new PentagonBoard();
        Player.resetCounter();
        player = new Player("TestPlayer");
        piece = new Piece(0, player);
    }

    @Test
    void testInitialCellWhenNull() {
        Cell result = board.getDestinationCell(null, 1, board, piece);
        assertEquals(0, result.getId());
    }

    @Test
    void testOneStepFrom0() {
        Cell result = board.getDestinationCell(board.getCells().get(0), 1, board, piece);
        assertEquals(1, result.getId());
    }

    @Test
    void testSpecialRouteFrom5() {
        Cell result = board.getDestinationCell(board.getCells().get(5), 1, board, piece);
        assertEquals(25, result.getId());
    }

    @Test
    void testMultiStepFrom0To3() {
        Cell result = board.getDestinationCell(board.getCells().get(0), 3, board, piece);
        assertEquals(3, result.getId());
    }

    @Test
    void testHistoryRecordedCorrectly() {
        board.getDestinationCell(board.getCells().get(0), 3, board, piece);
        assertEquals(3, piece.getHistory().size());
        assertEquals(2, piece.getHistory().peek());
    }

    @Test
    void testIsCorner_withCornerIds_returnsTrue() {
        assertTrue(board.isCorner(0));
        assertTrue(board.isCorner(5));
        assertTrue(board.isCorner(10));
        assertTrue(board.isCorner(15));
        assertTrue(board.isCorner(20));
    }

    @Test
    void testIsCorner_withNonCornerId_returnsFalse() {
        assertFalse(board.isCorner(1));
        assertFalse(board.isCorner(6));
        assertFalse(board.isCorner(27)); // centre
    }

    @Test
    void testIsCentre_withCentreId_returnsTrue() {
        assertTrue(board.isCentre(27));
    }

    @Test
    void testIsCentre_withNonCentreId_returnsFalse() {
        assertFalse(board.isCentre(0));
        assertFalse(board.isCentre(5));
        assertFalse(board.isCentre(30));
    }
}