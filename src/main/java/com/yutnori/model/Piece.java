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

    public void moveTo(Cell cell) {
        if (this.position != null) {
            this.position.removePiece(this);
        }

        if (cell == null) { // 처음에 빽도가 나오면 parameter인 cell이 null로 들어옴
            this.position = null; // 말이 보드에 없으므로 null로 설정
            this.isOnBoard = false; // 보드에 없으므로 false
            return;
        }

        this.position = cell;
        this.isOnBoard = true; // 말이 이동하면 보드에 올라감
        cell.addPiece(this);
        distance++; // 기본적으로 한 칸씩 이동했다고 가정
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
