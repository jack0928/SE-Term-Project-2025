package com.yutnori.controller;

import com.yutnori.FXview.FXGameView;
import com.yutnori.YutnoriApplication;
import com.yutnori.YutnoriFXApplication;
import com.yutnori.model.*;
import com.yutnori.view.GameView;
import com.yutnori.viewInterface.GameViewInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.*;

public class GameController {
    private Game game;
    private GameViewInterface view;
    private final Queue<Integer> stepQueue = new LinkedList<>();
    private boolean isRollingPhase = true; // true: 윷 던지기, false: 말 이동
    private int remainingAdditionalTurns = 0; // 잡은 말 수에 따라 추가 턴 수를 저장하는 변수

    public GameController(GameViewInterface view) {
        this.view = view;
        initializeGame(view); // default constructor에서 initializeGame() 호출, initializes the game.
    }

    public void initializeGame(GameViewInterface view) {
        // 1. user input (View에게 일임)

        Board board = view.getBoard();
        List<Player> players = view.getPlayers();
        int pieceCount = players.get(0).getPieces().size();

        this.game = new Game(players, board, pieceCount);
        startGame();

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
            view.showMessage("윷/모입니다! 한 번 더 던지세요.");
        } else {
            isRollingPhase = false;
            handleTurn();
        }
    }

    public void handleTurn() { // 턴을 넘길 수 있는지 확인하고 넘길 수 있다면 턴을 넘기는 method.
        int totalCaptured = 0;
        List<Integer> steps = new ArrayList<>(stepQueue);
        stepQueue.clear();

        Player current = game.getCurrentPlayer();

        while (!steps.isEmpty()) {
            int selectedStep = view.promptStepSelection(current, steps);
            Piece selectedPiece = view.promptPieceSelection(current, selectedStep);

            if (selectedPiece == null) { // 이동할 말이 없을 경우, 바로 턴 넘기고 return.
                game.nextPlayer();
                isRollingPhase = true;
                renderGame();
                return;
            }

            PieceMoveController moveController = new PieceMoveController(game.getBoard());
            moveController.movePiece(selectedPiece, selectedStep);

            totalCaptured += moveController.capturedCount; // 잡은 말 수 업데이트


            view.getBoardView().repaint();
            view.getStatusView().updateFinishedPieces(game.getPlayers());

            if (checkAndEndGame(current)) return;

            view.render(current, game.getPlayers());  // UI 갱신
            steps.remove((Integer) selectedStep);  // step 제거

        }

        if (totalCaptured > 0) {
            remainingAdditionalTurns += totalCaptured;
            String message = (totalCaptured == 1)
                    ? "상대방의 말을 잡았습니다!"
                    : "상대방의 말을 " + totalCaptured + "개 잡았습니다!";
            view.showMessage(message);

        }

        // 잡은 말이 있어서 추가 턴이 있을 경우,  현재 플레이어에게 윷 던지기 기회 부여
        if (remainingAdditionalTurns > 0) {
            view.showMessage("윷을 " + remainingAdditionalTurns + "번 더 던질 수 있습니다!");
            isRollingPhase = true;
            renderGame();
            remainingAdditionalTurns--;
            return;
        }

        // 턴 넘기기 (잡은 말도 없고 step도 끝났을 때만)
        game.nextPlayer();
        isRollingPhase = true;
        renderGame();

    }



    public boolean checkAndEndGame(Player currentPlayer) {
        long finished = currentPlayer.getPieces().stream().filter(Piece::isFinished).count();
        if (finished == game.getPieceNumPerPlayer()) {
            boolean restart = view.askRestart(currentPlayer.getName());
            if (restart) {
                view.dispose();
                Player.resetCounter();
                restartGame();
            } else {
                System.exit(0);
            }
            return true;
        }
        return false;
    }

    public void renderGame() { // 게임 상태를 렌더링하는 method
        Player currentPlayer = game.getCurrentPlayer();
        view.render(currentPlayer, game.getPlayers());
    }

    private void restartGame() {
        if (view instanceof FXGameView) { // 현재 JavaFX로 실행되었을 경우
            Platform.runLater(() -> {
                Stage newStage = new Stage();
                GameViewInterface fxView = new FXGameView(newStage);
                new GameController(fxView);
            });
        } else { // 현재 Swing으로 실행되었을 경우
            SwingUtilities.invokeLater(() -> {
                GameView newView = new GameView();
                new GameController(newView);
            });
        }
    }


}
