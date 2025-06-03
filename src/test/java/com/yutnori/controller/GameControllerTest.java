package com.yutnori.controller;

import com.yutnori.controller.GameController;
import com.yutnori.model.*;
import com.yutnori.viewInterface.BoardViewInterface;
import com.yutnori.viewInterface.GameViewInterface;
import com.yutnori.viewInterface.PlayerStatusViewInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameControllerTest {

    @Mock GameViewInterface mockView;
    @Mock BoardViewInterface mockBoardView;
    @Mock PlayerStatusViewInterface mockStatusView;

    Game game;
    Player player;
    GameController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        player = new Player("Tester");
        for (int i = 0; i < 4; i++) {
            player.addPiece(new Piece(i, player));
        }

        game = new Game(List.of(player), new SquareBoard(), 4);

        when(mockView.getBoard()).thenReturn(game.getBoard());
        when(mockView.getPlayers()).thenReturn(game.getPlayers());
        when(mockView.getBoardView()).thenReturn(mockBoardView);
        when(mockView.getStatusView()).thenReturn(mockStatusView);

        controller = new GameController(mockView);
    }

    @Test
    void testRenderGame_callsRender() {
        clearInvocations(mockView); // 테스트를 위해 초기 호출 제거. 기본적으로 controller 생성 시 renderGame()이 한 번 호출됨.

        controller.renderGame();
        verify(mockView).render(eq(player), anyList());
    }

    @Test
    void testCheckAndEndGame_returnsFalseWhenNotFinished() {
        boolean ended = controller.checkAndEndGame(player);
        assertFalse(ended);
    }


    @Test
    void testHandleTurn_turnEndsIfNoPieceSelected() throws Exception {
        // setup: 던진 윷 결과 등록
        Field field = GameController.class.getDeclaredField("stepQueue");
        field.setAccessible(true);
        ((Queue<Integer>) field.get(controller)).add(3);

        when(mockView.promptStepSelection(any(), anyList())).thenReturn(3);
        when(mockView.promptPieceSelection(any(), anyInt())).thenReturn(null); // 선택 불가

        controller.handleTurn();

        verify(mockView, atLeastOnce()).render(any(), any()); // 턴 넘겼는지 확인
    }

    @Test
    void testHandleTurn_movesPieceAndRendersUI() throws Exception {
        // stepQueue에 값 미리 세팅 (3칸 이동)
        Field field = GameController.class.getDeclaredField("stepQueue");
        field.setAccessible(true);
        Queue<Integer> queue = (Queue<Integer>) field.get(controller);
        queue.add(3);

        Piece testPiece = player.getPieces().get(0); // 첫 번째 말 사용

        // 사용자 선택 흐름 mocking
        when(mockView.promptStepSelection(eq(player), anyList())).thenReturn(3);
        when(mockView.promptPieceSelection(eq(player), eq(3))).thenReturn(testPiece);

        controller.handleTurn();

        // 검증 1: render()가 한 번 이상 호출됨
        verify(mockView, atLeastOnce()).render(eq(player), anyList());

        // 검증 2: 말의 위치가 바뀌었는지 (Cell ID 기준)
        assertEquals(3, testPiece.getPosition().getId());
    }

    @Test
    void testHandleTurn_noCapture_noExtraTurn() throws Exception {
        Field field = GameController.class.getDeclaredField("stepQueue");
        field.setAccessible(true);
        Queue<Integer> queue = (Queue<Integer>) field.get(controller);
        queue.add(1);

        Piece piece = player.getPieces().get(0);
        when(mockView.promptStepSelection(any(), anyList())).thenReturn(1);
        when(mockView.promptPieceSelection(any(), eq(1))).thenReturn(piece);

        controller.handleTurn();

        // "잡았습니다" 메시지가 호출되지 않음
        verify(mockView, never()).showMessage(contains("잡았습니다"));
    }

    @Test
    void testHandleYutThrow_normalResult_triggersHandleTurn() throws Exception {
        // isRollingPhase = true 보장
        Field isRollingPhaseField = GameController.class.getDeclaredField("isRollingPhase");
        isRollingPhaseField.setAccessible(true);
        isRollingPhaseField.setBoolean(controller, true);

        // throwYut()가 3을 반환하도록 설정 (윷/모 아님)
        when(mockView.throwYut(false)).thenReturn(3);

        //step 선택 및 말 선택 mocking
        Piece testPiece = player.getPieces().get(0);
        when(mockView.promptStepSelection(any(), anyList())).thenReturn(3);
        when(mockView.promptPieceSelection(any(), eq(3))).thenReturn(null); // 턴 종료를 유도

        // 실행
        controller.handleYutThrow(false);

        // 검증: render()가 적어도 한 번 이상 호출됐는지 확인 (handleTurn 실행됨을 간접 확인)
        verify(mockView, atLeastOnce()).render(any(), any());
    }

    @Test
    void testHandleYutThrow_addsResultAndSkipsHandleTurn_onYutOrMo() throws Exception {
        when(mockView.throwYut(false)).thenReturn(4); // 윷: 추가 턴

        controller.handleYutThrow(false);

        Field field = GameController.class.getDeclaredField("stepQueue");
        field.setAccessible(true);
        Queue<Integer> queue = (Queue<Integer>) field.get(controller);

        assertEquals(1, queue.size());
        assertEquals(4, queue.peek());
    }

    @Test
    void testHandleTurn_multipleCaptures_showsCorrectMessage_realistic() throws Exception {
        // stepQueue 준비
        Field stepQueueField = GameController.class.getDeclaredField("stepQueue");
        stepQueueField.setAccessible(true);
        Queue<Integer> queue = (Queue<Integer>) stepQueueField.get(controller);
        queue.add(0); // 0번 셀로 이동 (테스트용 목적지)

        // 현재 플레이어의 말
        Piece mainPiece = player.getPieces().get(0);
        mainPiece.setOnBoard(true);
        mainPiece.moveTo(game.getBoard().getCells().get(0));

        // 적 플레이어 추가 및 말 두 개 배치 (같은 셀에)
        Player enemy = new Player("Enemy");
        Piece enemy1 = new Piece(10, enemy);
        Piece enemy2 = new Piece(11, enemy);
        enemy1.setOnBoard(true);
        enemy2.setOnBoard(true);

        Cell sharedCell = game.getBoard().getCells().get(0);
        enemy1.moveTo(sharedCell);
        enemy2.moveTo(sharedCell);

        sharedCell.addPiece(mainPiece); // 테스트 말도 같은 칸에

        game.getPlayers().add(enemy); // 게임에 플레이어 추가

        // mockView 입력 흐름 구성
        when(mockView.promptStepSelection(any(), anyList())).thenReturn(0);
        when(mockView.promptPieceSelection(any(), eq(0))).thenReturn(mainPiece);

        controller.handleTurn();

        // 잡은 말 메시지 확인 (정확히 2개가 잡히므로)
        verify(mockView).showMessage(contains("2개 잡았습니다"));
    }

    @Test
    void testHandleTurn_additionalTurns_granted() throws Exception {
        Field stepField = GameController.class.getDeclaredField("stepQueue");
        stepField.setAccessible(true);
        Queue<Integer> queue = (Queue<Integer>) stepField.get(controller);
        queue.add(3);

        Piece testPiece = player.getPieces().get(0);

        when(mockView.promptStepSelection(any(), anyList())).thenReturn(3);
        when(mockView.promptPieceSelection(any(), anyInt())).thenReturn(testPiece);

        // capturedCount를 강제로 설정하여 추가 턴 발생시키기 위해
        Field addTurnField = GameController.class.getDeclaredField("remainingAdditionalTurns");
        addTurnField.setAccessible(true);
        addTurnField.setInt(controller, 1);

        controller.handleTurn();

        verify(mockView).showMessage(contains("번 더 던질 수 있습니다"));
    }

    @Test
    void testHandleYutThrow_earlyReturn_whenNotRollingPhase() {
        // 강제로 isRollingPhase = false 설정
        Field field;
        try {
            field = GameController.class.getDeclaredField("isRollingPhase");
            field.setAccessible(true);
            field.setBoolean(controller, false);
        } catch (Exception e) {
            fail("Failed to set isRollingPhase to false");
            return;
        }

        controller.handleYutThrow(false);
        verify(mockView, never()).throwYut(anyBoolean());
    }

    @Test
    void testCheckAndEndGame_restartFlow() {
        for (Piece piece : player.getPieces()) {
            piece.setFinished(true); // 모든 말 완료 처리
        }

        // Mock restart
        when(mockView.askRestart(anyString())).thenReturn(true);

        boolean result = controller.checkAndEndGame(player);

        assertTrue(result);
        verify(mockView).dispose();
    }

}