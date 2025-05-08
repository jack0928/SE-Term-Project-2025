package com.yutnori.model;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Piece {
    private int id;
    private Cell position;
    private int distance; // 말이 이동한 거리 (나중에 finish할때 사용한다고 함, 참고하여 사용 바람.)
    public List<Piece> moveTogetherPiece;
    private Player owner;
    private boolean isOnBoard = false;
    private boolean isFinished = false; // 한바퀴 다 돌고 온 말인지 여부를 판별하기 위한 boolean
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

    public Piece(int id, Player owner) {
        this.id = id;
        this.owner = owner;
        this.distance = 0;
        this.moveTogetherPiece = new ArrayList<>();
    }

    // 여기서 moveTo의 역할이 뭔지 모르겠음 수정 필요.
    // Dependency 관계 생각하면 Board Cell을 attribute로 가져가 Board에서 move를 수행해야함
    // 지금처럼 하면 Piece에서 attribute로 Board를 갖다써야함. 이 부분 민규와 논의 필요.
    /* public void moveTo(Cell cell) {
        //여기도 moveTo에선 이동만. 이외 동작은 updatePosition에서 처리하게 바꿔야 함

        if (this.position != null) {
            this.position.removePiece(this);
        }

        this.position = cell;
        this.isOnBoard = true; // 말이 이동하면 보드에 올라감
        cell.addPiece(this);

        distance++; // 기본적으로 한 칸씩 이동했다고 가정

     }
    */
    // 위는 기존 moveTo. 사용처가 없어 주석 처리 완료. 밑의 moveTo()는 기존 updatePosition()이었으나 이 함수가 실제로 moveTo()의 원래 역할을 수행하므로 moveTo()로 개명.

    public void moveTo(Cell cell) {
        if (position != null) {
            position.removePiece(this);  // 이전 Cell에서 제거
        }

        position = cell;

        if (cell != null) {
            cell.addPiece(this);
            isOnBoard = true;
        } else {
            isOnBoard = false;
        }
    }


    public void finish() {
        // 현재 위치에서 제거
        if (position != null) {
            position.removePiece(this);
        }

        position = null;
        isFinished = true;
        isOnBoard = false;
        moveTogetherPiece.clear(); // 업힌 말 초기화
    }

    public void reset() {
        // 보드 상 위치 제거
        if (position != null) {
            position.removePiece(this);
        }

        position = null;
        isOnBoard = false;
        isFinished = false;
        distance = 0;
        moveTogetherPiece.clear(); // 업힌 말 초기화
    }


    public Cell getPosition() { // to get the position of the piece
        return position;
    }

    public Player getOwner() { // to get the owner of the piece
        return owner;
    }

    public int getId() { // to get the id of the piece
        return id;
    }

    public Color getColor() { // to get the color of the piece (Player1은 자동으로 빨간색, Player2는 파란색, Player3은 노란색, Player4는 초록색)
        return switch (owner.getId()) {
            case 1 -> Color.RED;
            case 2 -> Color.BLUE;
            case 3 -> Color.YELLOW;
            case 4 -> Color.GREEN;
            default -> Color.GRAY;
        };
    }

    public boolean isOnBoard() { // to check if the piece is on the board
        return isOnBoard;
    }

    // public void setOnBoard(boolean onBoard) { isOnBoard = onBoard; } // setter method. 현재 사용처 없음. 추후 필요시 사용 바람.

    public void addGroupingPiece(Piece p) { // 업기 기능을 위한 메소드. 현재 사용처 없음. 추후 필요시 사용 바람.
        moveTogetherPiece.add(p);
    }

    public List<Piece> getGroupingPieces() { // grouping 이후에 업힌 말들을 가져오는 메소드. 현재 사용처 없음. 추후 필요시 사용 바람.
        return moveTogetherPiece;
    }

    public int getGroupingPieceCount() { // grouping 이후, 업힌 말의 갯수를 가져옴
        return moveTogetherPiece.size();
    }

    public boolean isFinished() {
        return isFinished;
    }
}
