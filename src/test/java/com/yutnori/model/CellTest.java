package com.yutnori.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    private Cell cell;
    private Player player;

    @BeforeEach
    void setUp() {
        Player.resetCounter();
        cell = new Cell(0, false, false);
        player = new Player("Tester");
    }

    @Test
    void testGetId() {
        assertEquals(0, cell.getId());
    }

    @Test
    void testAddSinglePiece() {
        Piece piece = new Piece(1, player);
        cell.addPiece(piece);

        List<Piece> pieces = cell.getStackedPieces();
        assertEquals(1, pieces.size());
        assertTrue(pieces.contains(piece));
    }

    @Test
    void testAddGroupedPiecesTogether() {
        Piece leader = new Piece(1, player);
        Piece follower = new Piece(2, player);

        leader.addGroupingPiece(follower);
        cell.addPiece(leader);

        List<Piece> pieces = cell.getStackedPieces();
        assertEquals(2, pieces.size());
        assertTrue(pieces.contains(leader));
        assertTrue(pieces.contains(follower));
    }

    @Test
    void testRemoveSinglePiece() {
        Piece piece = new Piece(1, player);
        cell.addPiece(piece);
        cell.removePiece(piece);

        List<Piece> pieces = cell.getStackedPieces();
        assertEquals(0, pieces.size());
    }

    @Test
    void testRemoveGroupedPiecesTogether() {
        Piece leader = new Piece(1, player);
        Piece follower = new Piece(2, player);

        leader.addGroupingPiece(follower);
        cell.addPiece(leader);
        cell.removePiece(leader);

        List<Piece> pieces = cell.getStackedPieces();
        assertFalse(pieces.contains(leader));
        assertFalse(pieces.contains(follower));
        assertEquals(0, pieces.size());
    }
}