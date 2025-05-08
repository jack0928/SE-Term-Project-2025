package com.yutnori.model;

import java.util.ArrayList;
import java.util.List;

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
        piecesOnCell.add(piece);
    }

    public void removePiece(Piece piece) {
        piecesOnCell.remove(piece);
    }

    public List<Piece> getStackedPieces() {
        return piecesOnCell;
    }
    /*
    public Cell getNextCell() { return nextCell; }

    public void setNextCell(Cell nextCell) { this.nextCell = nextCell; }

    public Cell getPreviousCell() { return previousCell; }

    public void setPreviousCell(Cell previousCell) { this.previousCell = previousCell; }

    public Cell getNextBranchCell() { return nextBranchCell; }

    public void setNextBranchCell(Cell nextBranchCell) { this.nextBranchCell = nextBranchCell; }
     */
}



