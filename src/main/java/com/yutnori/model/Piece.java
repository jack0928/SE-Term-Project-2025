package com.yutnori.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Piece {
    private int id;
    private Cell position;
    private int distance;
    public List<Piece> moveTogetherPiece;
    private Player owner;
    private boolean isOnBoard = false;

    public Piece(int id, Player owner) {
        this.id = id;
        this.owner = owner;
        this.distance = 0;
        this.moveTogetherPiece = new ArrayList<>();
    }

    // 여기서 moveTo의 역할이 뭔지 모르겠음 수정 필요.
    // Dependency 관계 생각하면 Board Cell을 attribute로 가져가 Board에서 move를 수행해야함
    // 지금처럼 하면 Piece에서 attribute로 Board를 갖다써야함. 이 부분 민규와 논의 필요.
    public void moveTo(Cell cell) {
        //여기도 moveTo에선 이동만. 이외 동작은 updatePosition에서 처리하게 바꿔야 함
        /*
        if (this.position != null) {
            this.position.removePiece(this);
        }

        this.position = cell;
        this.isOnBoard = true; // 말이 이동하면 보드에 올라감
        cell.addPiece(this);

        distance++; // 기본적으로 한 칸씩 이동했다고 가정
        */
    }

    public void updatePosition(Cell cell) {
        // 이전 Cell 에서 말 제거
        if (position != null) {
            position.removePiece(this);
        }
        // 새 위치 설정
        position = cell;
        if (cell == null) {
            isOnBoard = false;
            return;
        }
        isOnBoard = true;
        cell.addPiece(this);
    }

    public void finish() {
        position = null;
        distance = 0;
    }

    public void reset() {
        if (position != null) {
            position.removePiece(this);
        }
        position = null;
        distance = 0;
        moveTogetherPiece.clear();
    }

    public Cell getPosition() {
        return position;
    }

    public Player getOwner() {
        return owner;
    }

    public int getId() {
        return id;
    }

    public Color getColor() {
        return switch (owner.getId()) {
            case 1 -> Color.RED;
            case 2 -> Color.BLUE;
            case 3 -> Color.YELLOW;
            case 4 -> Color.GREEN;
            default -> Color.GRAY;
        };
    }

    public boolean isOnBoard() {
        return isOnBoard;
    }

    public void setOnBoard(boolean onBoard) {
        isOnBoard = onBoard;
    }
}
