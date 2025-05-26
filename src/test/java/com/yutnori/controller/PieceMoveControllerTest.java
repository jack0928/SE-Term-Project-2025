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
    void testMoveToStartTwice_triggersImmediateFinish() {
        piece.setOnBoard(true);
        piece.moveTo(board.getCells().get(2)); // 시작점은 아니지만
        piece.getHistory().push(0);
        piece.getHistory().push(3);
        piece.getHistory().push(0); // 출발점 2번 통과

        controller.movePiece(piece, 1); // 어디로 가든 zeroCount > 1 만족

        assertTrue(piece.isFinished());
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
    void testMovePiece_doesNothingWhenAlreadyFinished() {
        piece.setFinished(true);
        controller.movePiece(piece, 2);

        // 상태가 그대로 유지되는지 확인
        assertFalse(piece.isOnBoard());
        assertNull(piece.getPosition());
    }

    @Test
    void testMovePiece_doesNothingWhenNextIsNull() {
        piece.setOnBoard(true);
        Cell original = board.getCells().get(board.getCells().size() - 1); // 마지막 칸
        piece.moveTo(original);

        // history에 1개만 넣어서 finish 조건 방지
        piece.getHistory().push(original.getId());

        // 너무 큰 음수 step → 목적지를 찾을 수 없음
        controller.movePiece(piece, -100);

        // 상태 검증
        assertEquals(original, piece.getPosition());
        assertTrue(piece.isOnBoard());
        assertFalse(piece.isFinished());
    }

    @Test
    void testMovePiece_withGroupedPiece_movesLeader() {
        Piece leader = new Piece(10, player);
        player.addPiece(leader);
        leader.setOnBoard(true);
        leader.moveTo(board.getCells().get(0));
        piece.setGroupLeader(leader); // piece는 업힌 말로 설정

        controller.movePiece(piece, 1); // 업힌 말 기준으로 호출

        assertEquals(1, leader.getPosition().getId()); // 리더가 이동했는지 확인
    }

    @Test
    void testMovePiece_withGroupLeader_movesLeader() {
        // 리더 말 준비
        Piece leader = new Piece(100, player);
        player.addPiece(leader);
        controller = new PieceMoveController(board); // 새 말 추가됐으므로 controller 재생성 가능

        // 리더를 보드에 올려놓고 위치와 history 세팅
        Cell start = board.getCells().get(0);
        leader.setOnBoard(true);
        leader.moveTo(start);
        leader.getHistory().push(0);
        leader.moveTo(board.getCells().get(1));

        // piece가 리더에게 업힘
        piece.setGroupLeader(leader);

        // 이동 수행
        controller.movePiece(piece, 1); // piece는 leader 기준으로 이동

        // 검증: 실제 이동은 leader 기준으로 되었는가
        assertNotNull(leader.getPosition());
        assertEquals(2, leader.getPosition().getId()); // 1 → 2 이동 확인

        // 업힌 말은 여전히 같은 리더를 따라야 함
        assertEquals(leader, piece.getGroupLeader());
    }

    @Test
    void testHandleGrouping_sameGroupLeader_skipsMerge() {
        // 두 말이 같은 리더를 공유
        Piece leader = new Piece(200, player);
        player.addPiece(leader);

        Piece p1 = new Piece(201, player);
        Piece p2 = new Piece(202, player);
        player.addPiece(p1);
        player.addPiece(p2);

        p1.setGroupLeader(leader);
        p2.setGroupLeader(leader);
        leader.addGroupingPiece(p1);
        leader.addGroupingPiece(p2);

        // 같은 셀에 배치
        Cell shared = board.getCells().get(10);
        shared.addPiece(p1);
        shared.addPiece(p2);
        p1.setOnBoard(true);
        p2.setOnBoard(true);
        p1.moveTo(shared);
        p2.moveTo(shared);

        controller.handleGrouping(p1); // 이미 같은 그룹 → 아무 일 없음

        // 그대로 유지
        assertEquals(2, leader.getGroupingPieces().size()); // 기존 두 명
    }

    @Test
    void testBackDo_finishesWhenHistoryEmptyAndPassedStartOnce() {
        piece.setOnBoard(true);
        piece.moveTo(board.getCells().get(0));
        piece.setPassedStartOnce(true); // 출발점 한 바퀴 돈 상태
        controller.movePiece(piece, -1);

        assertTrue(piece.isFinished());
        assertEquals(100, player.getScore());
    }

    @Test
    void testBackDo_whenHistoryEmptyAndNotPassedStart_doesNothing() {
        piece.setOnBoard(true);
        piece.moveTo(board.getCells().get(0)); // 출발점
        // history 비워둠
        piece.setPassedStartOnce(false); // 출발점 한 바퀴 안 돌았음

        controller.movePiece(piece, -1);

        assertFalse(piece.isFinished());
        assertNotNull(piece.getPosition()); // 그대로 유지
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


        int captured = controller.handleCapture(piece);

        assertEquals(1, captured);
        assertFalse(enemyPiece.isOnBoard());
        assertNull(enemyPiece.getPosition());
    }

    @Test
    void testCapture_whenNoEnemyPieces_returnsZero() {
        piece.setOnBoard(true);
        piece.moveTo(board.getCells().get(0));

        Piece friend = new Piece(3, player);
        friend.setOnBoard(true);
        friend.moveTo(board.getCells().get(0)); // 같은 셀에 아군

        int captured = controller.handleCapture(piece);

        assertEquals(0, captured);
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
    void testCheckFinishCondition_triggersFinishWhenAtStartWithHistory() {
        piece.setOnBoard(true);
        piece.moveTo(board.getCells().get(0));
        piece.getHistory().push(0);
        piece.getHistory().push(5);  // history.size() > 1

        controller.movePiece(piece, 0);  // 0이면 이동은 없지만 상태 평가

        assertTrue(piece.isFinished());
    }

    @Test
    void testFinishTrigger_whenOnSpecialCellAfterTwoZeros() {
        piece.setOnBoard(true);
        piece.moveTo(board.getCells().get(2)); // 2번 셀 (0이 아님)
        piece.getHistory().push(0);
        piece.getHistory().push(3);
        piece.getHistory().push(0); // 출발점 두 번 방문

        controller.movePiece(piece, 1); // → 3번 셀

        assertTrue(piece.isFinished());
        assertFalse(piece.isOnBoard());
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

    @Test
    void testHandleGrouping_centralCellPairMergedInSquareBoard() {
        // 22번 셀에 piece, 27번 셀에 같은 플레이어 말 배치
        Piece other = new Piece(2, player);
        player.addPiece(other);

        Cell cell22 = board.getCells().get(22);
        Cell cell27 = board.getCells().get(27);

        cell22.addPiece(piece);
        cell27.addPiece(other);
        piece.setOnBoard(true);
        piece.moveTo(cell22);
        other.setOnBoard(true);
        other.moveTo(cell27);

        controller.handleGrouping(piece);

        assertEquals(other, piece.getGroupLeader());
        assertTrue(other.getGroupingPieces().contains(piece));
    }

    @Test
    void testGrouping_skipsWhenAlreadyGrouped() {
        Piece other = new Piece(2, player);
        player.addPiece(other);

        Cell target = board.getCells().get(5);
        target.addPiece(other);
        target.addPiece(piece);
        other.setOnBoard(true);
        piece.setOnBoard(true);
        other.moveTo(target);
        piece.moveTo(target);

        // 미리 같은 그룹으로 설정
        other.addGroupingPiece(piece);

        controller.handleGrouping(piece);

        // 아무 일도 일어나지 않아야 함
        assertEquals(other, piece.getGroupLeader());
        assertEquals(1, other.getGroupingPieces().size());
    }

    @Test
    void testGrouping_whenNoCandidates_doesNothing() {
        piece.setOnBoard(true);
        piece.moveTo(board.getCells().get(5));

        // 혼자 있음
        controller.handleGrouping(piece);

        assertNull(piece.getGroupLeader());
    }

    @Test
    void testBackDo_setsPassedStartOnce_whenLandingOnStart() {
        piece.setOnBoard(true);
        piece.moveTo(board.getCells().get(1)); // 일단 시작점 아닌 곳에 위치
        piece.getHistory().push(0); // 이동 시 pop → 0번 셀로 이동 유도

        controller.movePiece(piece, -1); // 빽도 실행

        assertEquals(0, piece.getPosition().getId());
        assertTrue(piece.hasPassedStartOnce()); // 이 줄이 커버 포인트
    }

}