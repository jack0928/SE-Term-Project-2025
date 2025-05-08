package com.yutnori.model;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private int id;
    private boolean isCenter;
    private boolean isCorner;
    private List<Piece> piecesOnCell; // 해당 cell 위에 있는 말의 list. 같은 팀이면 업고 다른 팀이면 잡기 activate.
    // 연결리스트처럼 Cell들을 연결하기 위함 -> 로직 최적화에 필요하다 판단, 추가
    private Cell nextCell;
    private Cell previousCell;
    private Cell cornerNextCell;

    public Cell(int id, boolean isCenter, boolean isCorner) {
        this.id = id;
        this.isCenter = isCenter;
        this.isCorner = isCorner;
        this.piecesOnCell = new ArrayList<>();
    }

    public int getId() {
        return id;
    }
    /* // isCentre와 isCorner는 Board에서 있는 것을 사용하여 주기 바람. 사용 예시: board.isCorner(id);
    public boolean isCenter() {
        if (id == 4 * 5 + 2) { return true; } // n*5+2 -> 가운데 id 구하는 공식. 그래서 n각형 보드의 n값 전달 필요.
        else { return false; }

    }

    public boolean isCorner() {
        if ((id % 5) == 0){ return true; }
        else return false;
    }
*/
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
