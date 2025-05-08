package com.yutnori.view;

import com.yutnori.controller.PieceMoveController;
import com.yutnori.controller.YutController;
import com.yutnori.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class TestPieceView {
    private static final Queue<Integer> stepQueue = new LinkedList<>();
    private static boolean isRollingPhase = true; // 윷 던지기 가능한 상태인지

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            // ===== 보드 선택 =====
            String[] boardOptions = {"Square", "Pentagon", "Hexagon"};
            String selectedBoard = (String) JOptionPane.showInputDialog(
                    null,
                    "보드 형태를 선택하세요:",
                    "보드 선택",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    boardOptions,
                    boardOptions[0]
            );
            if (selectedBoard == null) System.exit(0);

            Board board = switch (selectedBoard) {
                case "Pentagon" -> new PentagonBoard();
                case "Hexagon" -> new HexagonBoard();
                default -> new SquareBoard();
            };

            // ===== 플레이어 수 선택 =====
            Integer[] playerCounts = {2, 3, 4};
            Integer selectedPlayerCount = (Integer) JOptionPane.showInputDialog(
                    null,
                    "플레이어 수를 선택하세요:",
                    "플레이어 수 선택",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    playerCounts,
                    playerCounts[0]
            );
            if (selectedPlayerCount == null) System.exit(0);

            // ===== 말 개수 선택 =====
            Integer[] pieceCounts = {2, 3, 4, 5};
            Integer selectedPieceCount = (Integer) JOptionPane.showInputDialog(
                    null,
                    "플레이어당 말 개수를 선택하세요:",
                    "말 개수 선택",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    pieceCounts,
                    pieceCounts[0]
            );
            if (selectedPieceCount == null) System.exit(0);

            // ===== 플레이어 및 말 생성 =====
            List<Player> players = new ArrayList<>();
            for (int i = 1; i <= selectedPlayerCount; i++) {
                Player player = new Player("Player" + i);
                for (int j = 0; j < selectedPieceCount; j++) {
                    Piece piece = new Piece(j, player);
                    player.addPiece(piece);
                }
                players.add(player);
            }

            // ===== 뷰 및 모델 생성 =====
            BoardView boardView = new BoardView(board);
            Yut yut = new Yut();
            YutResultView yutResultView = new YutResultView();
            yutResultView.setPreferredSize(new Dimension(400, 150));
            PieceMoveController mover = new PieceMoveController(board);
            YutController yutController = new YutController(yut, yutResultView);

            PlayerStatusView statusView = new PlayerStatusView();
            statusView.render(players);

            // ===== 현재 플레이어 상태 =====
            final int[] currentPlayerIndex = {0};

            JLabel turnLabel = new JLabel("현재 턴: " + players.get(currentPlayerIndex[0]).getName());
            turnLabel.setHorizontalAlignment(SwingConstants.CENTER);

            yutResultView.getThrowRandomButton().addActionListener(e -> {
                if (!isRollingPhase) return;

                yutController.performThrow(true);
                int result = yut.getLastResult();
                stepQueue.add(result);

                if (result == 4 || result == 5) {
                    JOptionPane.showMessageDialog(null, "윷/모입니다! 한 번 더 던지세요.");
                } else {
                    isRollingPhase = false;
                    processNextMove(players, currentPlayerIndex, board, boardView, turnLabel, statusView);
                }
            });

            yutResultView.getSelectYutButton().addActionListener(e -> {
                if (!isRollingPhase) return;

                yutController.performThrow(false);
                int result = yut.getLastResult();
                stepQueue.add(result);

                if (result == 4 || result == 5) {
                    JOptionPane.showMessageDialog(null, "윷/모입니다! 한 번 더 던지세요.");
                } else {
                    isRollingPhase = false;
                    processNextMove(players, currentPlayerIndex, board, boardView, turnLabel, statusView);
                }
            });

            // ===== 오른쪽 패널 구성 =====
            JPanel rightPanel = new JPanel();
            rightPanel.add(turnLabel, 0);  // 맨 위에 추가됨

            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.add(yutResultView);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            rightPanel.add(statusView);


            // ===== 전체 레이아웃 구성 =====
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(boardView, BorderLayout.CENTER);
            mainPanel.add(rightPanel, BorderLayout.EAST);

            JFrame frame = new JFrame("Yut Piece Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(mainPanel);
            frame.pack();
            frame.setVisible(true);
        });
    }

    private static void processNextMove(
            List<Player> players,
            int[] currentPlayerIndex,
            Board board,
            BoardView boardView,
            JLabel turnLabel,
            PlayerStatusView statusView
    ) {

        List<Integer> stepList = new ArrayList<>(stepQueue);
        stepQueue.clear();

        Player currentPlayer = players.get(currentPlayerIndex[0]);

        while (!stepList.isEmpty()) {
            // === 1. 이동 가능한 말 리스트 확보 ===
            List<Piece> movable = new ArrayList<>();
            for (Piece p : currentPlayer.getPieces()) {
                if (!p.isFinished()) movable.add(p);
            }

            if (movable.isEmpty()) {
                JOptionPane.showMessageDialog(null, "이동 가능한 말이 없습니다.");
                break;
            }
            int selectedStep;
            int stepIndex = 0;

            if (stepList.size() == 1) {selectedStep = stepList.get(0);}
            else {
                // === 2. 사용자에게 이동할 step 선택 제공 ===
                // YutResultView에서 getResultText() 메서드를 정의하여 각 결과에 대한 텍스트를 반환하도록 함.
                // 예: -1 -> "빽도", 1 -> "도", 2 -> "개", 3 -> "걸", 4 -> "윷", 5 -> "모"
                String[] stepOptions = stepList.stream()
                        .map(YutResultView::getResultText) // 👈 아래에 정의할 함수
                        .toArray(String[]::new);

                stepIndex = JOptionPane.showOptionDialog(
                        null,
                        "사용할 윷 결과를 선택하세요:",
                        "Step 선택",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        stepOptions,
                        stepOptions[0]
                );
                if (stepIndex == JOptionPane.CLOSED_OPTION) return;
                selectedStep = stepList.get(stepIndex);
            }
            // === 3. 사용자에게 말 선택 제공 ===
            Piece selectedPiece;
            boolean anyOnBoard = movable.stream().anyMatch(Piece::isOnBoard);
            if (!anyOnBoard || movable.size() == 1) {
                selectedPiece = movable.get(0);
            } else {
                String[] pieceOptions = new String[movable.size()];
                for (int i = 0; i < movable.size(); i++) {
                    pieceOptions[i] = "말 " + (movable.get(i).getId() + 1);
                }
                int pieceIndex = JOptionPane.showOptionDialog(
                        null,
                        "이동할 말을 선택하세요 (" + selectedStep + "칸):",
                        "말 선택",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        pieceOptions,
                        pieceOptions[0]
                );
                if (pieceIndex == JOptionPane.CLOSED_OPTION) return;
                selectedPiece = movable.get(pieceIndex);
            }

            // === 4. 이동 실행 ===
            PieceMoveController pmController = new PieceMoveController(board);
            // 여기다 잡기 로직 추가턴 로직 추가함. 근데 현재 잡은 후 바로 한 번 더 던지는 건 아님.
            // ex) 상대방 cell5에 있음 -> 모 도 나옴-> 모 이동 -> 잡고 한 번 더 던지기 이게 아니라
            //                      -> 모 도 나옴 -> 모 이동 -> 도 이동-> 잡았으니 한 번 더
            // 이렇게 작동함
            pmController.movePiece(selectedPiece, selectedStep);
            if (pmController.isCaptured){
                currentPlayerIndex[0] = (currentPlayerIndex[0] + 1) % players.size();
                turnLabel.setText("현재 턴: " + players.get(currentPlayerIndex[0]).getName());
                pmController.isCaptured = false;
            }
            boardView.repaint();
            statusView.updatePlayers(players);

            stepList.remove(stepIndex);  // ✅ 선택된 step 제거
        }

        // === 5. 턴 종료 처리 ===
        isRollingPhase = true;
        currentPlayerIndex[0] = (currentPlayerIndex[0] + 1) % players.size();
        turnLabel.setText("현재 턴: " + players.get(currentPlayerIndex[0]).getName());

    }



}
