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


    public Cell(int id, boolean isCenter, boolean isCorner) {
        this.id = id;
        this.isCenter = isCenter;
        this.isCorner = isCorner;
        this.piecesOnCell = new ArrayList<>();
    }

    public int getId() {
        return id;
    } // Cell의 id를 return.


    public void addPiece(Piece piece) { // Cell 위에 Piece를 추가하는 메소드
        if (!piecesOnCell.contains(piece)) {
            piecesOnCell.add(piece);
        }
        for (Piece groupedPiece : piece.getGroupingPieces()) {
            if (!piecesOnCell.contains(groupedPiece)) {
                piecesOnCell.add(groupedPiece);
            }
        }

    }

    public void removePiece(Piece piece) { // Cell 위에 있는 Piece를 제거하는 메소드
        piecesOnCell.remove(piece);
        for (Piece groupedPiece : piece.getGroupingPieces()) {
            if (piecesOnCell.contains(groupedPiece)) {
                piecesOnCell.remove(groupedPiece);
                System.out.println("Grouped Piece " + groupedPiece.getId() + " also removed from Cell " + id);
            }

        }

    }

    public List<Piece> getStackedPieces() { // Cell 위에 있는 Piece들을 반환하는 메소드 (stackedPieces)
        return piecesOnCell;
    }


}
