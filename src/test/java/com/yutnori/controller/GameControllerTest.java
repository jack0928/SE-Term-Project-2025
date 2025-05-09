package com.yutnori.controller;

import com.yutnori.model.*;
import com.yutnori.view.BoardView;
import com.yutnori.view.GameView;
import com.yutnori.view.PlayerStatusView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import javax.swing.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameControllerTest {

    @Mock GameView mockView;
    @Mock BoardView mockBoardView;
    @Mock PlayerStatusView mockStatusView;

    Game game;
    Player player;
    GameController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 기본 Player & Board 구성
        player = new Player("Tester");
        for (int i = 0; i < 4; i++) {
            player.addPiece(new Piece(i, player));
        }
        game = new Game(List.of(player), new SquareBoard(), 4);

        // Mock 설정
        when(mockView.getBoardView()).thenReturn(mockBoardView);
        when(mockView.getStatusView()).thenReturn(mockStatusView);

        controller = new GameController(game, mockView);
    }

    @Test
    void testHandleYutThrow_normalStep_addsToQueueAndProceeds() {
        when(mockView.throwYut(true)).thenReturn(3); // 개

        controller.handleYutThrow(true);

        // isRollingPhase == false로 변경되며 handleTurn 호출됨
        verify(mockView, never()).render(any(), any()); // render는 handleTurn 끝나야 호출됨
    }

    @Test
    void testHandleYutThrow_yutShowsMessageAndKeepsPhase() {
        when(mockView.throwYut(true)).thenReturn(4); // 윷

        controller.handleYutThrow(true);

        // 턴 전환은 아직 안 일어남
        verify(mockView, never()).render(any(), any());
    }

//    추후 GameController 수정 필요
//    @Test
//    void testHandleTurn_movesPieceAndEndsTurn() {
//        Piece piece = player.getPieces().get(0);
//
//        // Step과 Piece 선택 mock 설정은 handleYutThrow 호출 이전에 해야 함
//        when(mockView.throwYut(true)).thenReturn(3); // 윷 결과 mock
//        when(mockView.promptStepSelection(anyList())).thenReturn(3); // step 선택 mock
//        when(mockView.promptPieceSelection(eq(player), eq(3))).thenReturn(piece); // piece 선택 mock
//
//        controller.handleYutThrow(true); // stepQueue에 값 추가됨
//        controller.handleTurn(); // 이동 수행
//
//        assertTrue(piece.isOnBoard(), "Piece should be on the board after move.");
//        verify(mockView).render(eq(player), anyList());
//    }

    @Test
    void testRenderGame_callsRender() {
        controller.renderGame();
        verify(mockView).render(eq(player), anyList());
    }

    @Test
    void testCheckAndEndGame_returnsFalseWhenNotFinished() {
        boolean ended = controller.checkAndEndGame(player);
        assertFalse(ended);
    }

    // GUI 상의 JOptionPane은 테스트에서 직접 확인 어려우므로 별도 GUI 테스트 필요
}