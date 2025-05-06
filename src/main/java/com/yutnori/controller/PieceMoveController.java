package com.yutnori.controller;

import com.yutnori.model.*;

public class PieceMoveController {
    private Board board;

    public PieceMoveController(Board board) {
        this.board = board;
    }

//    public Piece selectPiece(Player player) {
//        return player.getActivePieces().stream().findFirst().orElse(null); // 유저가 여러 옵션이 있을 때 선택할 수 있게 하는 부분. 추후 더 구현하여 사용 바람.
//    }

    //이거랑 Piece의 movePiece랑은 차이가 무엇인가?
    // -> movePiece는 어느 cell로 가야 할 지 계산해주는 function. moveTo()는 그 cell로 가는 것.
    public void movePiece(Piece piece, int steps) {
        if (!piece.isOnBoard()) {
            if (steps > 0) {  // 빽도일 땐 출발하지 않음
                Cell start = board.getCells().get(0);
                piece.moveTo(start);
            }
            return;
        }

        Cell current = piece.getPosition();
        Cell next = board.getNextCell(current, steps);
        if (next != null) {
            piece.moveTo(next);
        }
    }


//    public void handleCapture(Piece piece) { 잡는 것을 처리 , 후속 작업 필요
//        Cell cell = piece.getPosition();
//        for (Piece other : cell.getStackedPieces()) {
//            if (!other.getOwner().equals(piece.getOwner())) {
//                other.reset(); // 잡힘
//            }
//        }
//    }
//
//    public void handleGrouping(Piece piece) { // grouping (업기) 처리, 후속 작업 필요.
//        Cell cell = piece.getPosition();
//        for (Piece other : cell.getStackedPieces()) {
//            if (other.getOwner().equals(piece.getOwner()) && other != piece) {
//                piece.moveTogetherPiece.add(other);
//            }
//        }
//    }
}
