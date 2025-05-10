package com.yutnori.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;
    private Player player1;
    private Player player2;
    private Board mockBoard;

    @BeforeEach
    void setUp() {
        Player.resetCounter(); // ID predictable
        player1 = new Player("Player1");
        player2 = new Player("Player2");
        mockBoard = new MockBoard();
        game = new Game(Arrays.asList(player1, player2), mockBoard, 4);
    }

    @Test
    void testInitialState() {
        List<Player> players = game.getPlayers();
        assertEquals(2, players.size());
        assertEquals(player1, players.get(0));
        assertEquals(player2, players.get(1));

        assertEquals(mockBoard, game.getBoard());
        assertEquals(4, game.getPieceNumPerPlayer());
    }

    @Test
    void testGetCurrentPlayer() {
        assertEquals(player1, game.getCurrentPlayer());
        game.nextPlayer();
        assertEquals(player2, game.getCurrentPlayer());
    }

    @Test
    void testNextPlayerCyclesCorrectly() {
        assertEquals(player1, game.getCurrentPlayer());
        game.nextPlayer();
        assertEquals(player2, game.getCurrentPlayer());
        game.nextPlayer();
        assertEquals(player1, game.getCurrentPlayer()); // 순환 확인
    }

    @Test
    void testGetPieceNumPerPlayer() {
        assertEquals(4, game.getPieceNumPerPlayer());
    }
}