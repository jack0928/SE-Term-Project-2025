package com.yutnori.model;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private int id;
    private List<Piece> piecesOnCell;


    public Cell(int id) {
        this.id = id;
        this.piecesOnCell = new ArrayList<>();
    }

    public int getId() {
        return id;
    }



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
            }
        }
    }

    public List<Piece> getStackedPieces() { // Cell 위에 있는 Piece들을 반환하는 메소드 (stackedPieces)
        return piecesOnCell;
    }


}
