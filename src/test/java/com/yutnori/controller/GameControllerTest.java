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
    void testHandleYutThrow_addsResultAndSkipsHandleTurn_onYutOrMo() throws Exception {
        when(mockView.throwYut(false)).thenReturn(4); // 윷: 추가 턴

        controller.handleYutThrow(false);

        Field field = GameController.class.getDeclaredField("stepQueue");
        field.setAccessible(true);
        Queue<Integer> queue = (Queue<Integer>) field.get(controller);

        assertEquals(1, queue.size());
        assertEquals(4, queue.peek());
    }

    // NOTE:
    // restartGame()은 UI 창을 새로 여는 JavaFX/Swing 관련 로직이므로 단위 테스트에서 제외합니다.
    // 이 메서드는 통합 테스트나 수동 QA로 검증 대상입니다.

}