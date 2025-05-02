package com.yutnori.model;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private int id;
    private boolean isCenter;
    private boolean isCorner;
    private List<Piece> piecesOnCell;

    public Cell(int id, boolean isCenter, boolean isCorner) {
        this.id = id;
        this.isCenter = isCenter;
        this.isCorner = isCorner;
        this.piecesOnCell = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public boolean isCenter() {
        return isCenter;
    }

    public boolean isCorner() {
        return isCorner;
    }

    public void addPiece(Piece piece) {
        piecesOnCell.add(piece);
    }

    public void removePiece(Piece piece) {
        piecesOnCell.remove(piece);
    }

    public List<Piece> getStackedPieces() {
        return piecesOnCell;
    }
}
