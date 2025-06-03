package com.yutnori.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @BeforeEach
    void resetId() {
        Player.resetCounter(); // ID 초기화
    }

    @Test
    void testPlayerNameAndId() {
        Player p1 = new Player("Jaemin");
        Player p2 = new Player("Yumin");

        assertEquals("Jaemin", p1.getName());
        assertEquals("Yumin", p2.getName());

        assertEquals(1, p1.getId());
        assertEquals(2, p2.getId());
    }

    @Test
    void testAddScoreAndGetScore() {
        Player player = new Player("TestPlayer");

        assertEquals(0, player.getScore());

        player.addScore(10);
        assertEquals(10, player.getScore());

        player.addScore(5);
        assertEquals(15, player.getScore());
    }

    @Test
    void testAddPieceAndGetPieces() {
        Player player = new Player("TestPlayer");
        Piece piece1 = new Piece(0, player);
        Piece piece2 = new Piece(1, player);

        assertTrue(player.getPieces().isEmpty());

        player.addPiece(piece1);
        player.addPiece(piece2);

        assertEquals(2, player.getPieces().size());
        assertTrue(player.getPieces().contains(piece1));
        assertTrue(player.getPieces().contains(piece2));
    }

    @Test
    void testResetCounter() {
        new Player("A");
        new Player("B");

        Player.resetCounter();

        Player newPlayer = new Player("C");
        assertEquals(1, newPlayer.getId());
    }
}