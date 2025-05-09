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
    private boolean isRollingPhase = true;

    public GameController(Game game, GameView view) {
        this.game = game;
        this.view = view;
    }


    public void startGame() {
        renderGame();
        view.setThrowButtonListener(e -> handleYutThrow(true));
        view.setSelectButtonListener(e -> handleYutThrow(false));
    }

    public void setBoardType(String type) {
        // 필요 시 board type 변경 가능
        // 현재는 Game 인스턴스 생성 전에 board 선택됨
    }

    public void handleYutThrow(boolean isRandom) {
        if (!isRollingPhase) return;

        int result = view.throwYut(isRandom); // 뷰가 YutController 사용
        stepQueue.add(result);

        if (result == 4 || result == 5) {
            JOptionPane.showMessageDialog(null, "윷/모입니다! 한 번 더 던지세요.");
        } else {
            isRollingPhase = false;
            handleTurn();
        }
    }

    public void handleTurn() {
        boolean canTurn = true;
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
            view.getStatusView().updatePlayers(game.getPlayers());

            if (checkAndEndGame(current)) return;

            if (moveController.isCaptured) {
                JOptionPane.showMessageDialog(null, "상대방의 말을 잡았습니다!");
                canTurn = false;
            }

            view.render(current, game.getPlayers());  // UI 갱신
            steps.remove((Integer) selectedStep);  // step 제거
        }

        if (canTurn) {
            game.nextPlayer();
        }

        isRollingPhase = true;

        renderGame();

    }



    public boolean checkAndEndGame(Player currentPlayer) {
        long finished = currentPlayer.getPieces().stream().filter(Piece::isFinished).count();
        if (finished == game.getPieceNumPerPlayer()) {
            int choice = JOptionPane.showOptionDialog(null,
                    "🎉 " + currentPlayer.getName() + "님이 승리했습니다!\n게임을 다시 시작하시겠습니까?",
                    "게임 종료",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Restart", "Exit"},
                    "Restart");

            if (choice == JOptionPane.YES_OPTION) {
                YutnoriApplication.restartGame(view.getFrame());
            } else {
                System.exit(0);
            }
            return true;
        }
        return false;
    }


    public void renderGame() {
        Player currentPlayer = game.getCurrentPlayer();
        view.render(currentPlayer, game.getPlayers());
    }
}
