package com.yutnori.controller;

import com.yutnori.YutnoriApplication;
import com.yutnori.model.*;
import com.yutnori.view.GameView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class GameController {
    private final Game game;
    private final GameView view;
    private final Queue<Integer> stepQueue = new LinkedList<>();
    private boolean isRollingPhase = true; // true: 윷 던지기, false: 말 이동

    public GameController(Game game, GameView view) {
        this.game = game;
        this.view = view;
    }


    public void startGame() { // 게임 시작 시 초기화 및 이벤트 리스너 설정
        renderGame();
        view.setThrowRandomButtonListener(e -> handleYutThrow(true));
        view.setSelectButtonListener(e -> handleYutThrow(false));
    }

    public void handleYutThrow(boolean isRandom) { // 윷 던지기 이벤트 처리
        if (!isRollingPhase) return;

        int result = view.throwYut(isRandom); // 뷰가 YutController 사용
        stepQueue.add(result);

        if (result == 4 || result == 5) { // 윷 혹은 모일 경우
            JOptionPane.showMessageDialog(null, "윷/모입니다! 한 번 더 던지세요.");
        } else {
            isRollingPhase = false;
            handleTurn();
        }
    }

    public void handleTurn() { // 턴을 넘길 수 있는지 확인하고 넘길 수 있다면 턴을 넘기는 method.
        boolean canTurn = true; // 턴을 넘길 수 있는지 확인하는 boolean 변수.
        List<Integer> steps = new ArrayList<>(stepQueue);
        stepQueue.clear();

        Player current = game.getCurrentPlayer();

        while (!steps.isEmpty()) {
            int selectedStep = view.promptStepSelection(steps);

            Piece selectedPiece = view.promptPieceSelection(current, selectedStep);
            if (selectedPiece == null) return;

            PieceMoveController moveController = new PieceMoveController(game.getBoard());
            moveController.movePiece(selectedPiece, selectedStep);

            view.getBoardView().repaint();
            view.getStatusView().updateFinishedPieces(game.getPlayers());

            if (checkAndEndGame(current)) return;

            if (moveController.isCaptured) {
                JOptionPane.showMessageDialog(null, "상대방의 말을 잡았습니다!");
                canTurn = false; // 상대방의 말을 잡았다면 턴을 넘길 수 없음.
            }

            view.render(current, game.getPlayers());  // UI 갱신
            steps.remove((Integer) selectedStep);  // step 제거
        }

        if (canTurn) { // 턴을 넘길 수 있다면 다음 플레이어로 이동.
            game.nextPlayer();
        }

        isRollingPhase = true;

        renderGame();

    }



    public boolean checkAndEndGame(Player currentPlayer) { // 게임 종료 조건 체크 후 재시작 혹은 종료
        long finished = currentPlayer.getPieces().stream().filter(Piece::isFinished).count();
        if (finished == game.getPieceNumPerPlayer()) {
            int choice = JOptionPane.showOptionDialog(null,
                    currentPlayer.getName() + "님이 승리했습니다!\n게임을 다시 시작하시겠습니까?",
                    "게임 종료",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"재시작", "종료"},
                    "재시작");

            if (choice == JOptionPane.YES_OPTION) { // 재시작 선택 시
                view.getFrame().dispose();
                Player.resetCounter();
                SwingUtilities.invokeLater(() -> YutnoriApplication.main(null)); // 새로운 게임 시작 (YutnoriApplication 의 main() 함수 호출)
            } else {
                System.exit(0); // 종료 선택 시 프로그램 종료
            }
            return true; // 게임 종료 O
        }
        return false; // 게임 종료 X
    }


    public void renderGame() { // 게임 상태를 렌더링하는 method
        Player currentPlayer = game.getCurrentPlayer();
        view.render(currentPlayer, game.getPlayers());
    }
}
