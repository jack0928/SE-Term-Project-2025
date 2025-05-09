package com.yutnori.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final List<Player> players;
    private final Board board;
    private int currentPlayerIndex;
    private final int pieceNumPerPlayer;

    public Game(List<Player> players, Board board, int pieceNumPerPlayer) {
        this.players = new ArrayList<>(players);
        this.board = board;
        this.pieceNumPerPlayer = pieceNumPerPlayer;
        this.currentPlayerIndex = 0;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Board getBoard() {
        return board;
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int getPieceNumPerPlayer() {
        return pieceNumPerPlayer;
    }

}
