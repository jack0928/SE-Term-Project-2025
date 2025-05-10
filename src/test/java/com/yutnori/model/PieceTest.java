package com.yutnori.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PieceTest {

    private Piece piece;
    private Player player;

    @BeforeEach
    void setUp() {
        Player.resetCounter(); // 플레이어 ID를 1부터 시작
        player = new Player("Player1");
        piece = new Piece(0, player);
    }

    @Test
    void testInitialState() {
        assertNull(piece.getPosition());
        assertFalse(piece.isOnBoard());
        assertFalse(piece.isFinished());
        assertFalse(piece.hasPassedStartOnce());
        assertEquals(0, piece.getHistory().size());
        assertEquals(new Color(255, 102, 102), piece.getColor()); // 수정됨
    }

    @Test
    void testMoveToUpdatesPositionAndOnBoard() {
        Cell cell = new Cell(0);
        piece.moveTo(cell);

        assertEquals(cell, piece.getPosition());
        assertTrue(piece.isOnBoard());
        assertTrue(cell.getStackedPieces().contains(piece));
    }

    @Test
    void testFinishClearsPositionAndSetsFinished() {
        Cell cell = new Cell(1);
        piece.moveTo(cell);
        piece.setFinished(true);
        piece.moveTo(null);

        assertNull(piece.getPosition());
        assertTrue(piece.isFinished());
        assertFalse(piece.isOnBoard());
        assertEquals(0, piece.getGroupingPieces().size());
    }

    @Test
    void testResetRestoresInitialState() {
        Cell cell = new Cell(2);
        piece.moveTo(cell);
        piece.setPassedStartOnce(true);
        piece.setFinished(true);
        piece.getHistory().push(2);

        piece.reset();

        assertNull(piece.getPosition());
        assertFalse(piece.isOnBoard());
        assertFalse(piece.isFinished());
        assertFalse(piece.hasPassedStartOnce());
        assertEquals(0, piece.getHistory().size());
        assertEquals(0, piece.getGroupingPieces().size());
    }

    @Test
    void testAddGroupingPieceCreatesBidirectionalLink() {
        Piece other = new Piece(1, player);
        piece.addGroupingPiece(other);

        assertTrue(piece.getGroupingPieces().contains(other));
        assertEquals(piece, other.getGroupLeader()); // 양방향이 아닌 단방향 확인
    }

    @Test
    void testResetGroupingBreaksLinks() {
        Piece other = new Piece(1, player);
        piece.addGroupingPiece(other);
        piece.setGroupLeader(other);

        piece.resetGrouping();

        assertNull(piece.getGroupLeader());
        assertFalse(other.getGroupingPieces().contains(piece));
        assertEquals(0, piece.getGroupingPieces().size());
    }
}