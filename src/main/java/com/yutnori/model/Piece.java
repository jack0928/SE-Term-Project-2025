package com.yutnori.model;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Piece {
    private final int id;
    private Cell position;
    public List<Piece> moveTogetherPiece;
    private Piece groupLeader = null;
    private final Player owner;
    private boolean isOnBoard = false;
    private boolean isFinished = false; // 한바퀴 다 돌고 온 말인지 여부를 판별하기 위한 boolean
    private boolean passedStartOnce = false; // 도착지점 (출발지점) 에 도착했는지 여부를 판별하기 위한 boolean
    public Stack<Integer> history = new Stack<Integer>() {
        @Override
        public Integer push(Integer item) {
            return super.push(item);
        }

        @Override
        public Integer pop() {
            return super.pop();
        }

        @Override
        public synchronized Integer peek() {
            return super.peek();
        }

        @Override
        public boolean isEmpty() {
            return super.isEmpty();
        }

        @Override
        public int size() {
            return super.size();
        }

    };

    public Stack<Integer> getHistory() {
        return history;
    }

    public Piece(int id, Player owner) {
        this.id = id;
        this.owner = owner;
        this.moveTogetherPiece = new ArrayList<>();
    }

    public void moveTo(Cell cell) {
        if (position != null) {
            position.removePiece(this);  // 이전 Cell에서 제거
        }
        position = cell;

        if (cell != null) {
            cell.addPiece(this);
            isOnBoard = true;
        }
        else { isOnBoard = false; }
    }

    public void reset() {
        // 보드 상 위치 제거
        if (position != null) {
            position.removePiece(this);
        }
        history.clear();
        position = null;
        isOnBoard = false;
        isFinished = false;
        moveTogetherPiece.clear(); // 업힌 말 초기화
        // TODO: passedStartOnce 초기화 해야 함 (그냥 아래 줄 주석해제 하면 됨)
        //  passedStartOnce = false;
    }

    public Cell getPosition() { // to get the position of the piece
        return position;
    }

    public void setPosition(Cell cell) { // to set the position of the piece
        this.position = cell;
    }

    public Player getOwner() { // to get the owner of the piece
        return owner;
    }

    public int getId() { // to get the id of the piece
        return id;
    }

    public Color getColor() { // to get the color of the piece (Player1은 자동으로 빨간색, Player2는 하늘색, Player3은 노란색, Player4는 초록색)
        return switch (owner.getId()) {
            case 1 -> Color.RED;
            case 2 -> new Color(135, 206, 250); // Sky Blue Colour. 원래는 파란색이었으나, Color.BLUE 색상은 말 위 숫자의 검은색과 너무 겹쳐서, 가독성을 위해 색을 바꿈.
            case 3 -> Color.YELLOW;
            case 4 -> Color.GREEN;
            default -> Color.GRAY;
        };
    }

    public boolean isOnBoard() { // to check if the piece is on the board
        return isOnBoard;
    }

    public void setOnBoard(boolean onBoard) { isOnBoard = onBoard; } // setter method for isOnBoard.

    public void setFinished(boolean finished) { isFinished = finished; } // setter method for isFinished.
    public void addGroupingPiece(Piece piece) {
        piece.setGroupLeader(this);
        if (moveTogetherPiece == null) {
            moveTogetherPiece = new ArrayList<>();
        }
        if (!moveTogetherPiece.contains(piece)) {
            moveTogetherPiece.add(piece);
        }
    }

    public List<Piece> getGroupingPieces() {
        return moveTogetherPiece != null ? moveTogetherPiece : new ArrayList<>();
    }

    // 말을 잡을 때, 기존 group 해제해야함.
    public void resetGrouping() {

        if (groupLeader != null) {
            groupLeader.getGroupingPieces().remove(this); // 리더의 목록에서도 제거
            groupLeader = null;
        }

        for (Piece p : moveTogetherPiece) {
            p.setGroupLeader(null); // 내가 업고 있던 애들의 groupLeader도 초기화
        }
        moveTogetherPiece.clear(); // 내가 업고 있던 애들 초기화
    }


    public boolean isFinished() {
        return isFinished;
    }

    public boolean hasPassedStartOnce() {
        return passedStartOnce;
    }

    public void setPassedStartOnce(boolean passed) {
        this.passedStartOnce = passed;
    }

    public Piece getGroupLeader() { return groupLeader; }

    public void setGroupLeader(Piece groupLeader) { this.groupLeader = groupLeader; }

    public Piece getGroupLeaderOrSelf() {
        return (groupLeader != null) ? groupLeader : this;
    }

    public List<Piece> getAllGroupedPieces() {
        List<Piece> all = new ArrayList<>(moveTogetherPiece);
        all.add(this);
        return all;
    }


}
