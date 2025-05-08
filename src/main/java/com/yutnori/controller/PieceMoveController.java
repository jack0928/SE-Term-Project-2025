package com.yutnori.controller;

import com.yutnori.model.*;

public class PieceMoveController {
    private Board board;

    public PieceMoveController(Board board) {
        this.board = board;
    }

    // 말 이동 로직: 목적지 Cell 계산 후 이동 + grouping 처리
    public void movePiece(Piece piece, int steps) {
        if (!piece.isOnBoard()) {
            if (steps > 0) {  // 빽도일 땐 출발하지 않음
                Cell start = board.getCells().get(0);
                piece.moveTo(start);

                // 첫 moveTo 후에도 추가로 이동
                Cell next = board.getNextCell(start, steps);
                if (next != null && next != start) {
                    piece.moveTo(next);
                }
                handleGrouping(piece);  // ✅ grouping 처리
            }
            return;
        }

        Cell current = piece.getPosition();
        Cell next = board.getDestinationCell(current, steps);
        if (next != null) {
            piece.moveTo(next);
            handleGrouping(piece);  // ✅ grouping 처리
        }
    }

    // 말 잡기 로직: 다른 플레이어 말이 있으면 잡고 원위치
    public void handleCapture(Piece piece) {
        Cell cell = piece.getPosition();
        for (Piece other : cell.getStackedPieces()) {
            if (!other.getOwner().equals(piece.getOwner())) {
                other.reset(); // 잡힘
            }
        }
    }

    // 말 업기 로직: 같은 플레이어의 다른 말이 해당 칸에 있으면 업기 수행
    public void handleGrouping(Piece piece) {
        Cell cell = piece.getPosition();
        for (Piece other : cell.getStackedPieces()) {
            if (other.getOwner().equals(piece.getOwner()) && other != piece) {
                other.addGroupingPiece(piece);  // ✅ piece를 other에 업힌 말로 추가
                break; // 한 번만 업히게
            }
        }
    }
}
