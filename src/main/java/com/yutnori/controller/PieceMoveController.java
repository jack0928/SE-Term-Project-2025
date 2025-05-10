package com.yutnori.controller;

import com.yutnori.model.*;

import java.util.*;

public class PieceMoveController {
    private Board board;
    public Boolean isCaptured = false;


    public PieceMoveController(Board board) {
        this.board = board;
    }

    // 말 이동 로직: 목적지 Cell 계산 후 이동 + grouping 처리
    public void movePiece(Piece piece, int steps) {
        if (piece.isFinished()) return;


        // 만약 piece가 업힌 말이라면 → 리더 기준으로 처리
        if (piece.getGroupLeader() != null) {
            piece = piece.getGroupLeader();
        }

        if (!piece.isOnBoard()) { // 말이 보드에 올라와 있지 않으면 (맨 처음)
            if (steps > 0) {  // 빽도일 땐 출발하지 않음
                Cell start = this.board.getCells().get(0);
                piece.setOnBoard(true);
                piece.moveTo(start);

                // 첫 moveTo 후에도 추가로 이동
                Cell next = this.board.getDestinationCell(start, steps, this.board, piece);
                if (next != null && next != start) {
                    piece.moveTo(next);
                }
                this.isCaptured = handleCapture(piece);
                handleGrouping(piece);  // grouping 처리
                checkFinishCondition(piece);
            }
            return;
        }
        if (steps == -1) { // 빽도일 때 (말이 보드에 올라와 있음)
            if(!piece.history.isEmpty()){ // history가 비어있지 않다면 (regular case)
                piece.moveTo(board.getCells().get(piece.history.pop()));
                this.isCaptured = handleCapture(piece);
                handleGrouping(piece); // grouping 처리

                if (piece.getPosition().getId() == 0) {
                    piece.setPassedStartOnce(true);
                }
                checkFinishCondition(piece);
            }
            else { // history가 비어있다면 (왔던만큼 다시 빽도로 돌아가서 출발점에 있는 노드는 history가 비어있음), 그 상태에서 빽도가 또 들어온다면 finish 처리
                if (piece.getPosition().getId() == 0 && piece.hasPassedStartOnce()) {
                    finishPiece(piece); // 업힌 말 포함, 끝내기 처리
                }
            }
        }
        else { // Regular case (말이 보드에 올라와 있고, 빽도가 아님.)
            Cell current = piece.getPosition();
            Cell next = board.getDestinationCell(current, steps, board, piece);
            if (next != null) {
                if (next.getId() == 0) { // 출발점에 도착했을 때
                    piece.setPassedStartOnce(true);
                }
                if (next.getId() == 1 || next.getId() == 2 || next.getId() == 3 || next.getId() ==4){ // 1,2,3,4번 칸에 도착했을 때
                    int zeroCount = getZeroCount(piece); // 해당 말이 출발점을 몇번 밟았는지 history에서 추적.

                    if (zeroCount > 1) { // zeroCount가 1보다 크다면, 출발점을 두번 이상 밟았다는 의미 == 한 바퀴 이상 돌았다는 의미.
                        piece.setPassedStartOnce(true);
                        checkFinishCondition(piece); // 업힌 말 포함, 끝내기 처리
                        return;
                    }
                }
                piece.moveTo(next);
                this.isCaptured = handleCapture(piece);
                handleGrouping(piece);
                checkFinishCondition(piece);

            }
        }
    }

    // 말 잡기 로직: 다른 플레이어 말이 있으면 잡고 원위치
    public Boolean handleCapture(Piece piece) {
        Cell cell = piece.getPosition();
        boolean captureOccurred = false; // 캡처 여부 플래그

        // 같은 셀에 있는 말을 순회하며 적인 말을 찾음
        List<Piece> copiedStack = new ArrayList<>(cell.getStackedPieces()); // ConcurrentModification 방지
        for (Piece other : copiedStack) {
            if (!other.getOwner().equals(piece.getOwner())) {
                // 상대방 말 캡처
                other.reset(); // 잡힌 말 원위치 처리
                other.resetGrouping();
                piece.setGroupLeader(null);
                captureOccurred = true; // 캡처 발생
            }
        }

        return captureOccurred; // 캡처 여부 반환
    }


    // 말 업기 로직: 같은 플레이어의 다른 말이 해당 칸에 있으면 업기 수행
    public void handleGrouping(Piece piece) {
        Cell cell = piece.getPosition();
        List<Piece> candidates = new ArrayList<>(cell.getStackedPieces());

        // 중앙 셀이라면 반대편 중앙 셀도 함께 조사
        if (cell.getId() == 22 || cell.getId() == 27) {
            int otherId = (cell.getId() == 22) ? 27 : 22;
            Cell otherCenter = board.getCells().get(otherId);
            candidates.addAll(otherCenter.getStackedPieces());
        }

        for (Piece other : candidates) {
            if (other.getOwner().equals(piece.getOwner()) && other != piece) {
                Piece leader1 = piece.getGroupLeaderOrSelf();
                Piece leader2 = other.getGroupLeaderOrSelf();

                if (leader1 == leader2) return;

                for (Piece p : leader1.getAllGroupedPieces()) {
                    leader2.addGroupingPiece(p);
                }
                break;
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

            p.getOwner().addScore(100); // 말이 들어올때마다 100점씩 추가
        }

        piece.getGroupingPieces().clear(); // 업은 목록 초기화
    }

    private void checkFinishCondition(Piece piece) {
        Stack<Integer> history = piece.getHistory();
        // 출발점에 도달했고, 한 바퀴 이상 돈 경우
        if (piece.getPosition().getId() == 0 && history.size() > 1) {
            finishPiece(piece); // 업힌 말 포함, 끝내기 처리
        }

        int zeroCount = getZeroCount(piece);
        if (zeroCount > 1) { // zeroCount가 1보다 크다면, 출발점을 두번 이상 밟았다는 의미 == 한 바퀴 이상 돌았다는 의미.
            finishPiece(piece); // 업힌 말 포함, 끝내기 처리
        }
    }

    private int getZeroCount(Piece piece) { // 해당 말이 (parameter로 들어온 piece) 출발점을 몇번 밟았는지 확인하는 메소드
        Stack<Integer> history = piece.getHistory();
        int zeroCount = 0; // history 내에서 0의 갯수 (출발점을 몇번 밟았는가?)
        for (int id : history) {
            if (id == 0) {
                zeroCount++; // 출발점을 한번 밟을 때마다 카운트가 증가
            }
        }
        return zeroCount;
    }

}
