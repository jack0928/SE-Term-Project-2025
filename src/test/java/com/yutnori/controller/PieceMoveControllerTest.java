package com.yutnori.controller;

import com.yutnori.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PieceMoveControllerTest {

    private PieceMoveController controller;
    private SquareBoard board;
    private Player player;
    private Piece piece;

    @BeforeEach
    void setUp() {
        board = new SquareBoard();
        player = new Player("TestPlayer");
        piece = new Piece(0, player);
        player.addPiece(piece);
        controller = new PieceMoveController(board);
    }

    @Test
    void testFirstMove_startsCorrectly() {
        controller.movePiece(piece, 2); // 첫 이동

        assertTrue(piece.isOnBoard());
        assertNotNull(piece.getPosition());
        assertEquals(1, piece.getHistory().peek()); // 목적지 ID가 기록됨
    }

    @Test
    void testMoveWithBackDo_whenNotStarted_doesNothing() {
        controller.movePiece(piece, -1); // 빽도인데 아직 시작 안함
        assertFalse(piece.isOnBoard());
        assertNull(piece.getPosition());
    }

    @Test
    void testMoveWithBackDo_whenOnBoard_goesBack() {
        controller.movePiece(piece, 3); // 먼저 보드에 올림
        int before = piece.getPosition().getId();
        controller.movePiece(piece, -1); // 빽도 실행

        assertTrue(piece.isOnBoard());
        assertNotEquals(before, piece.getPosition().getId());
    }

    @Test
    void testCapture_enemyPieceIsReset() {
        // 적군 말 준비
        Player enemy = new Player("Enemy");
        Piece enemyPiece = new Piece(1, enemy);
        enemy.addPiece(enemyPiece);
        board.getCells().get(0).addPiece(enemyPiece);
        enemyPiece.setOnBoard(true);
        enemyPiece.moveTo(board.getCells().get(0));

        // 아군 말 이동
        controller.movePiece(piece, 1); // 0 → 1
        piece.moveTo(board.getCells().get(0)); // 같은 위치에 수동 배치 (충돌 유도)

        boolean captured = controller.handleCapture(piece);

        assertTrue(captured);
        assertFalse(enemyPiece.isOnBoard());
        assertNull(enemyPiece.getPosition());
    }

    @Test
    void testFinishCondition_pieceGetsFinished() {
        // 출발점 여러 번 통과하도록 강제 이동
        piece.setOnBoard(true);
        piece.moveTo(board.getCells().get(0)); // 첫 번째 도착
        piece.getHistory().push(0);
        piece.getHistory().push(5);
        piece.getHistory().push(0); // 두 번째 도착

        controller.movePiece(piece, 1); // 조건 만족되므로 finish

        assertTrue(piece.isFinished());
        assertFalse(piece.isOnBoard());
        assertNull(piece.getPosition());
        assertEquals(100, player.getScore());
    }

    @Test
    void testGrouping_whenSamePlayerExistsOnSameCell() {
        Piece other = new Piece(2, player);
        player.addPiece(other);

        Cell target = board.getCells().get(5);
        target.addPiece(other);
        other.setOnBoard(true);
        other.moveTo(target);

        piece.setOnBoard(true);
        piece.moveTo(target);

        controller.handleGrouping(piece);

        assertEquals(other, piece.getGroupLeader());
        assertTrue(other.getGroupingPieces().contains(piece));
    }
}