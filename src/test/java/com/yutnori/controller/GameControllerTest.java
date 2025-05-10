package com.yutnori.controller;

import com.yutnori.controller.GameController;
import com.yutnori.model.*;
import com.yutnori.view.BoardView;
import com.yutnori.view.GameView;
import com.yutnori.view.PlayerStatusView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Field;
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
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // 기본 플레이어/게임 세팅
        player = new Player("Tester");
        for (int i = 0; i < 4; i++) {
            player.addPiece(new Piece(i, player));
        }
        game = new Game(List.of(player), new SquareBoard(), 4);

        // 뷰 mock 설정
        when(mockView.getBoardView()).thenReturn(mockBoardView);
        when(mockView.getStatusView()).thenReturn(mockStatusView);

        controller = new GameController(); // 기본 생성자 사용

        // 리플렉션으로 game, view 주입
        Field gameField = GameController.class.getDeclaredField("game");
        gameField.setAccessible(true);
        gameField.set(controller, game);

        Field viewField = GameController.class.getDeclaredField("view");
        viewField.setAccessible(true);
        viewField.set(controller, mockView);
    }

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

    // 더 복잡한 테스트는 이후에 추후에 GameController 수정 후 추가
}