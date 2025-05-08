package com.yutnori.controller;

import com.yutnori.model.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PieceMoveController {
    private Board board;



    public PieceMoveController(Board board) {
        this.board = board;
    }

    // 말 이동 로직: 목적지 Cell 계산 후 이동 + grouping 처리
    public void movePiece(Piece piece, int steps) {
        if (piece.isFinished()) return;

        if (!piece.isOnBoard()) { // 말이 보드에 올라와 있지 않으면 (맨 처음)
            if (steps > 0) {  // 빽도일 땐 출발하지 않음
                Cell start = board.getCells().get(0);
                piece.setOnBoard(true);
                piece.moveTo(start);

                // 첫 moveTo 후에도 추가로 이동
                Cell next = board.getDestinationCell(start, steps, board, piece);
                if (next != null && next != start) {
                    piece.moveTo(next);
                }
                handleGrouping(piece);  // grouping 처리
                checkFinishCondition(piece);
            }
            return;
        }
        if (steps == -1) { // 빽도일 때 (말이 보드에 올라와 있음)
            if(!piece.history.isEmpty()){
                System.out.println(piece.history.peek());
                piece.moveTo(board.getCells().get(piece.history.pop()));
                handleGrouping(piece); // grouping 처리
                checkFinishCondition(piece);
            }
        }
        else { // Regular case (말이 보드에 올라와 있고, 빽도가 아님.)
            Cell current = piece.getPosition();
            Cell next = board.getDestinationCell(current, steps, board, piece);
            if (next != null) {
                if (piece.isOnBoard() && next.getId() == 0 && piece.hasPassedStartOnce()) {
                    finishPiece(piece);
                } else {
                    if (piece.isOnBoard() && piece.getPosition().getId() != 0 && next.getId() == 0) {
                        piece.setPassedStartOnce(true); // 한 바퀴 돌았음을 기록
                    }
                    piece.moveTo(next);
                    handleGrouping(piece);
                    checkFinishCondition(piece);
                }

            }
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

    private void finishPiece(Piece piece) {
        List<Piece> group = new ArrayList<>(piece.getGroupingPieces());
        group.add(piece); // 본인 포함

        for (Piece p : group) {
            p.setFinished(true);
            p.setOnBoard(false);
            Cell cell = p.getPosition();
            if (cell != null) {
                cell.removePiece(p); // 보드에서 제거
            }
            p.setPosition(null);


        }

        piece.getGroupingPieces().clear(); // 업은 목록 초기화
    }

    private void checkFinishCondition(Piece piece) {
        // 출발점에 도달했고, 한 바퀴 이상 돈 경우
        if (piece.getPosition().getId() == 0 && piece.getHistory().size() > 1) {
            finishPiece(piece); // 업힌 말 포함 처리
        }
    }


}
