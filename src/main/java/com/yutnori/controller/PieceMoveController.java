package com.yutnori.controller;

import com.yutnori.model.*;

public class PieceMoveController {
    private Board board;
    private Game game;

    public PieceMoveController(Board board, Game game) {
        this.board = board;
        this.game = game;
    }

    public Piece selectPiece(Player player) {
        return player.getActivePieces().stream().findFirst().orElse(null); // 단순 선택
    }

    //이거랑 Piece의 movePiece랑은 차이가 무엇인가?
    public void movePiece(Piece piece, int steps) {
        Cell current = piece.getPosition();
        Cell next = board.getNextCell(current, steps);
        piece.moveTo(next);
    }


    public void handleCapture(Piece piece) {
        Cell cell = piece.getPosition();
        for (Piece other : cell.getStackedPieces()) {
            if (!other.getOwner().equals(piece.getOwner())) {
                other.reset(); // 잡힘
            }
        }
    }

    public void handleStacking(Piece piece) {
        Cell cell = piece.getPosition();
        for (Piece other : cell.getStackedPieces()) {
            if (other.getOwner().equals(piece.getOwner()) && other != piece) {
                piece.moveTogetherPiece.add(other);
            }
        }
    }
}
