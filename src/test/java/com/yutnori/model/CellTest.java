package com.yutnori.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    private Cell cell;
    private Player player;

    @BeforeEach
    void setUp() {
        Player.resetCounter();
        cell = new Cell(0);
        player = new Player("Tester");
    }

    @Test
    void testGetId() {
        assertEquals(0, cell.getId());
    }

    @Test
    void testAddSinglePiece() {
        Piece piece = new Piece(1, player);
        cell.addPiece(piece);

        List<Piece> pieces = cell.getStackedPieces();
        assertEquals(1, pieces.size());
        assertTrue(pieces.contains(piece));
    }

    @Test
    void testAddGroupedPiecesTogether() {
        Piece leader = new Piece(1, player);
        Piece follower = new Piece(2, player);

        leader.addGroupingPiece(follower);
        cell.addPiece(leader);

        List<Piece> pieces = cell.getStackedPieces();
        assertEquals(2, pieces.size());
        assertTrue(pieces.contains(leader));
        assertTrue(pieces.contains(follower));
    }

    @Test
    void testAddGroupedPieceAlreadyInCell_doesNotDuplicate() {
        Piece leader = new Piece(1, player);
        Piece follower = new Piece(2, player);
        leader.addGroupingPiece(follower);

        // 선행적으로 follower를 수동 추가
        cell.addPiece(follower);
        cell.addPiece(leader); // 이후 leader 추가 시 follower가 중복되면 안됨

        List<Piece> pieces = cell.getStackedPieces();
        assertEquals(2, pieces.size()); // 중복 없이 두 개만 존재
    }

    @Test
    void testRemoveSinglePiece() {
        Piece piece = new Piece(1, player);
        cell.addPiece(piece);
        cell.removePiece(piece);

        List<Piece> pieces = cell.getStackedPieces();
        assertEquals(0, pieces.size());
    }

    @Test
    void testRemoveGroupedPiecesTogether() {
        Piece leader = new Piece(1, player);
        Piece follower = new Piece(2, player);

        leader.addGroupingPiece(follower);
        cell.addPiece(leader);
        cell.removePiece(leader);

        List<Piece> pieces = cell.getStackedPieces();
        assertFalse(pieces.contains(leader));
        assertFalse(pieces.contains(follower));
        assertEquals(0, pieces.size());
    }


    @Test
    void testRemoveGroupedPieceThatIsNotInCell_doesNotThrowOrChange() {
        Piece leader = new Piece(1, player);
        Piece follower = new Piece(2, player);
        leader.addGroupingPiece(follower);

        cell.addPiece(leader);  // follower는 명시적으로 넣지 않음

        // follower가 없는 상태에서 제거 시도
        cell.removePiece(follower); // 예외 없이 작동해야 함

        List<Piece> pieces = cell.getStackedPieces();
        assertEquals(1, pieces.size()); // leader만 남아 있어야 함
        assertTrue(pieces.contains(leader));
    }

    @Test
    void testRemoveGroupedPieceNotInCell_skipsRemovalGracefully() {
        Piece leader = new Piece(1, player);
        Piece follower = new Piece(2, player);
        leader.addGroupingPiece(follower);

        // follower는 cell에 추가하지 않음
        cell.addPiece(leader);
        cell.getStackedPieces().remove(follower); // 혹시라도 addPiece에서 들어갔을 수 있으므로 제거

        // 이제 follower는 cell에 없고, leader만 있는 상태
        cell.removePiece(leader);

        // 테스트: 예외 없이 정상 실행되며, follower는 존재하지 않아 아무 일도 안 생김
        assertFalse(cell.getStackedPieces().contains(follower));
        assertFalse(cell.getStackedPieces().contains(leader));
        assertEquals(0, cell.getStackedPieces().size());
    }

}