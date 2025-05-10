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
    private Game game;
    private GameView view;
    private final Queue<Integer> stepQueue = new LinkedList<>();
    private boolean isRollingPhase = true; // true: 윷 던지기, false: 말 이동

    public GameController() {
        initializeGame(); // default constructor에서 initializeGame() 호출, initializes the game.
    }

    public void initializeGame() {
        // 1. 사용자 입력
        String[] boardOptions = {"Square", "Pentagon", "Hexagon"};
        String selectedBoard = (String) JOptionPane.showInputDialog(
                null, "보드 형태를 선택하세요:", "보드 선택",
                JOptionPane.PLAIN_MESSAGE, null, boardOptions, boardOptions[0]);
        if (selectedBoard == null) return;

        Board board = switch (selectedBoard) {
            case "Pentagon" -> new PentagonBoard();
            case "Hexagon" -> new HexagonBoard();
            default -> new SquareBoard();
        };

        Integer[] playerCounts = {2, 3, 4};
        Integer selectedPlayerCount = (Integer) JOptionPane.showInputDialog(
                null, "플레이어 수를 선택하세요:", "플레이어 수 선택",
                JOptionPane.PLAIN_MESSAGE, null, playerCounts, playerCounts[0]);
        if (selectedPlayerCount == null) return;

        Integer[] pieceCounts = {2, 3, 4, 5};
        Integer selectedPieceCount = (Integer) JOptionPane.showInputDialog(
                null, "플레이어당 말 개수를 선택하세요:", "말 개수 선택",
                JOptionPane.PLAIN_MESSAGE, null, pieceCounts, pieceCounts[0]);
        if (selectedPieceCount == null) return;

        // 2. 플레이어 및 말 생성
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= selectedPlayerCount; i++) {
            Player player = new Player("Player" + i);
            for (int j = 0; j < selectedPieceCount; j++) {
                player.addPiece(new Piece(j, player));
            }
            players.add(player);
        }

        // 3. 모델/뷰 생성 및 저장
        this.game = new Game(players, board, selectedPieceCount);
        this.view = new GameView(board, players);

        // 4. 게임 시작
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
