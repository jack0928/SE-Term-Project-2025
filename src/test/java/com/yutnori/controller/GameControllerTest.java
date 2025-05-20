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

    // 더 복잡한 테스트는 이후에 추후에 GameController 수정 후 추가
}