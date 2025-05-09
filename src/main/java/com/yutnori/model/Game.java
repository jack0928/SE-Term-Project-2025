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

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
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

    public boolean checkWinCondition() {
        for (Player player : players) {
            long finishedCount = player.getPieces().stream()
                    .filter(Piece::isFinished)
                    .count();
            if (finishedCount == pieceNumPerPlayer) {
                return true;
            }
        }
        return false;
    }

    public Player getWinningPlayer() {
        for (Player player : players) {
            long finishedCount = player.getPieces().stream()
                    .filter(Piece::isFinished)
                    .count();
            if (finishedCount == pieceNumPerPlayer) {
                return player;
            }
        }
        return null;
    }

    // 선택적으로 재시작 기능을 넣고 싶을 경우
    public void resetGame() {
        for (Player player : players) {
            player.getPieces().forEach(Piece::reset); // Piece 클래스에 reset() 필요
        }
        currentPlayerIndex = 0;
    }
}
