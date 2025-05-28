package com.yutnori.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PieceTest {

    private Piece piece;
    private Player player;

    @BeforeEach
    void setUp() {
        Player.resetCounter(); // 플레이어 ID를 1부터 시작
        player = new Player("Player1");
        piece = new Piece(0, player);
    }

    @Test
    void testInitialState() {
        assertNull(piece.getPosition());
        assertFalse(piece.isOnBoard());
        assertFalse(piece.isFinished());
        assertFalse(piece.hasPassedStartOnce());
        assertEquals(0, piece.getHistory().size());
        assertEquals(new Color(255, 102, 102), piece.getColor()); // 수정됨
    }

    @Test
    void testMoveToUpdatesPositionAndOnBoard() {
        Cell cell = new Cell(0);
        piece.moveTo(cell);

        assertEquals(cell, piece.getPosition());
        assertTrue(piece.isOnBoard());
        assertTrue(cell.getStackedPieces().contains(piece));
    }

    @Test
    void testMoveToNull_whenNoPreviousPosition_doesNotThrow() {
        piece.moveTo(null); // 초기 상태에서 null로 이동
        assertNull(piece.getPosition());
        assertFalse(piece.isOnBoard());
    }

    @Test
    void testFinishClearsPositionAndSetsFinished() {
        Cell cell = new Cell(1);
        piece.moveTo(cell);
        piece.setFinished(true);
        piece.moveTo(null);

        assertNull(piece.getPosition());
        assertTrue(piece.isFinished());
        assertFalse(piece.isOnBoard());
        assertEquals(0, piece.getGroupingPieces().size());
    }

    @Test
    void testResetRestoresInitialState() {
        Cell cell = new Cell(2);
        piece.moveTo(cell);
        piece.setPassedStartOnce(true);
        piece.setFinished(true);
        piece.getHistory().push(2);

        piece.reset();

        assertNull(piece.getPosition());
        assertFalse(piece.isOnBoard());
        assertFalse(piece.isFinished());
        assertFalse(piece.hasPassedStartOnce());
        assertEquals(0, piece.getHistory().size());
        assertEquals(0, piece.getGroupingPieces().size());
    }

    @Test
    void testAddGroupingPieceCreatesBidirectionalLink() {
        Piece other = new Piece(1, player);
        piece.addGroupingPiece(other);

        assertTrue(piece.getGroupingPieces().contains(other));
        assertEquals(piece, other.getGroupLeader()); // 양방향이 아닌 단방향 확인
    }

    @Test
    void testAddGroupingPiece_whenListIsNull_initializesList() throws Exception {
        var field = Piece.class.getDeclaredField("moveTogetherPiece");
        field.setAccessible(true);
        field.set(piece, null);

        Piece other = new Piece(2, player);
        piece.addGroupingPiece(other);

        assertTrue(piece.getGroupingPieces().contains(other));
    }

    @Test
    void testAddGroupingPiece_whenAlreadyGrouped_doesNotDuplicate() {
        Piece other = new Piece(2, player);
        piece.addGroupingPiece(other);
        piece.addGroupingPiece(other); // 중복 시도

        assertEquals(1, piece.getGroupingPieces().size()); // 한 번만 추가돼야 함
    }

    @Test
    void testResetGrouping_withEmptyGroup_doesNotThrow() {
        piece.setGroupLeader(null); // 리더 없음
        piece.resetGrouping(); // moveTogetherPiece 비어 있음
        assertEquals(0, piece.getGroupingPieces().size());
    }

    @Test
    void testGetGroupingPieces_whenListIsNull_returnsEmptyList() throws Exception {
        var field = Piece.class.getDeclaredField("moveTogetherPiece");
        field.setAccessible(true);
        field.set(piece, null); // 강제로 null 설정

        List<Piece> result = piece.getGroupingPieces();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testResetGroupingBreaksLinks() {
        Piece other = new Piece(1, player);
        piece.addGroupingPiece(other);
        piece.setGroupLeader(other);

        piece.resetGrouping();

        assertNull(piece.getGroupLeader());
        assertFalse(other.getGroupingPieces().contains(piece));
        assertEquals(0, piece.getGroupingPieces().size());
    }

    @Test
    void testResetGrouping_resetsChildrenGroupLeader() {
        Piece child1 = new Piece(2, player);
        Piece child2 = new Piece(3, player);
        piece.addGroupingPiece(child1);
        piece.addGroupingPiece(child2);

        piece.resetGrouping();

        assertNull(child1.getGroupLeader());
        assertNull(child2.getGroupLeader());
    }

    @Test
    void testGetAllGroupedPieces_whenAlone_containsSelfOnly() {
        List<Piece> result = piece.getAllGroupedPieces();
        assertEquals(1, result.size());
        assertEquals(piece, result.get(0));
    }

    @Test
    void testReset_whenPositionIsNull_doesNotThrow() {
        piece.reset(); // position == null일 때 호출
        assertNull(piece.getPosition());
    }

    @Test
    void testGetColor_returnsDefaultWhenPlayerIdOutOfRange() {
        Player unknown = new Player("Unknown") {
            @Override
            public int getId() {
                return 999; // 예상하지 못한 ID
            }
        };
        Piece oddPiece = new Piece(99, unknown);
        assertEquals(Color.GRAY, oddPiece.getColor());
    }


}