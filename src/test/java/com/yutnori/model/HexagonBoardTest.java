package com.yutnori.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HexagonBoardTest {

    private HexagonBoard board;
    private Player player;
    private Piece piece;

    @BeforeEach
    void setUp() {
        board = new HexagonBoard();
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
        Cell result = board.getDestinationCell(board.getCells().get(0), 1, board, piece);
        assertEquals(1, result.getId());
    }

    @Test
    void testSpecialRouteFrom5() {
        Cell result = board.getDestinationCell(board.getCells().get(5), 1, board, piece);
        assertEquals(30, result.getId()); // 특수 경로 분기
    }

    @Test
    void testMultiStepFrom0To3() {
        Cell result = board.getDestinationCell(board.getCells().get(0), 3, board, piece);
        assertEquals(3, result.getId());
    }

    @Test
    void testHistoryStackedProperly() {
        board.getDestinationCell(board.getCells().get(0), 3, board, piece);
        assertEquals(3, piece.getHistory().size());
        assertEquals(2, piece.getHistory().peek());
    }
}