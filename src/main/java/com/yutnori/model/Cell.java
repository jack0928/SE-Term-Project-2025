package com.yutnori.model;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Stack;

public class Cell {
    private int id;
    private boolean isCenter;
    private boolean isCorner;
    private List<Piece> piecesOnCell;
    //graph로 구현

    // private Cell nextCell;
    // private Cell previousCell;
    // private Cell nextBranchCell;  // 코너 이외에도 다음 노드의 id가 바뀌는 부분 저장.


    public Cell(int id, boolean isCenter, boolean isCorner) {
        this.id = id;
        this.isCenter = isCenter;
        this.isCorner = isCorner;
        this.piecesOnCell = new ArrayList<>();
    }

    public int getId() {
        return id;
    }
    public boolean isCenter() { return isCenter; }

    public void setCenter(boolean center) { isCenter = center; }

    public boolean isCorner() { return isCorner; }

    public void setCorner(boolean corner) { isCorner = corner; }


    public void addPiece(Piece piece) {
        if (!piecesOnCell.contains(piece)) {
            piecesOnCell.add(piece);
        }
        for (Piece groupedPiece : piece.getGroupingPieces()) {
            if (!piecesOnCell.contains(groupedPiece)) {
                piecesOnCell.add(groupedPiece);
            }
        }

    }

    public void removePiece(Piece piece) {
        piecesOnCell.remove(piece);
        for (Piece groupedPiece : piece.getGroupingPieces()) {
            if (piecesOnCell.contains(groupedPiece)) {
                piecesOnCell.remove(groupedPiece);
                System.out.println("Grouped Piece " + groupedPiece.getId() + " also removed from Cell " + id);
            }

        }

    }

    public List<Piece> getStackedPieces() {
        return piecesOnCell;
    }


}
