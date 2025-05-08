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
                    processNextMove(players, currentPlayerIndex, board, boardView, turnLabel);
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
                    processNextMove(players, currentPlayerIndex, board, boardView, turnLabel);
                }
            });

            // ===== 오른쪽 패널 구성 =====
            JPanel rightPanel = new JPanel();
            rightPanel.add(turnLabel, 0);  // 맨 위에 추가됨

            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.add(yutResultView);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));



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

    private static void processMove(
            List<Player> players,
            int[] currentPlayerIndex,
            Board board,
            BoardView boardView,
            JLabel turnLabel,
            Yut yut
    ) {
        Player currentPlayer = players.get(currentPlayerIndex[0]);
        int steps = yut.getLastResult();

        List<Piece> movable = new ArrayList<>();
        for (Piece p : currentPlayer.getPieces()) {
            if (!p.isFinished()) {
                movable.add(p);
            }
        }

        if (movable.isEmpty()) {
            JOptionPane.showMessageDialog(null, "이동 가능한 말이 없습니다.");
        } else {
            Piece selectedPiece;
            boolean anyOnBoard = movable.stream().anyMatch(Piece::isOnBoard);
            if ((!anyOnBoard || movable.size() == 1)) {
                selectedPiece = movable.get(0);
            } else {
                String[] choices = new String[movable.size()];
                for (int i = 0; i < movable.size(); i++) {
                    choices[i] = "말 " + (i + 1);
                }
                int selected = JOptionPane.showOptionDialog(
                        null,
                        "이동할 말을 선택하세요:",
                        "말 선택",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        choices,
                        choices[0]
                );
                if (selected == JOptionPane.CLOSED_OPTION) return;
                selectedPiece = movable.get(selected);
            }

            PieceMoveController mover = new PieceMoveController(board);
            mover.movePiece(selectedPiece, steps);
            boardView.repaint();

            if (steps != 4 && steps != 5) {
                currentPlayerIndex[0] = (currentPlayerIndex[0] + 1) % players.size();
                turnLabel.setText("현재 턴: " + players.get(currentPlayerIndex[0]).getName());
            }
        }
    }

    private static void processNextMove(
            List<Player> players,
            int[] currentPlayerIndex,
            Board board,
            BoardView boardView,
            JLabel turnLabel
    ) {
        if (stepQueue.isEmpty()) {
            isRollingPhase = true; // 다시 윷 던지기 가능
            currentPlayerIndex[0] = (currentPlayerIndex[0] + 1) % players.size();
            turnLabel.setText("현재 턴: " + players.get(currentPlayerIndex[0]).getName());
            return;
        }

        int steps = stepQueue.peek();

        Player currentPlayer = players.get(currentPlayerIndex[0]);
        List<Piece> movable = new ArrayList<>();
        for (Piece p : currentPlayer.getPieces()) {
            if (!p.isFinished()) movable.add(p);
        }

        if (movable.isEmpty()) {
            JOptionPane.showMessageDialog(null, "이동 가능한 말이 없습니다.");
            processNextMove(players, currentPlayerIndex, board, boardView, turnLabel); // 다음 step
            return;
        }

        Piece selectedPiece;
        boolean anyOnBoard = movable.stream().anyMatch(Piece::isOnBoard);
        if ((!anyOnBoard || movable.size() == 1) && stepQueue.size() == 1) {
            selectedPiece = movable.get(0);
        } else {
            String[] choices = new String[movable.size()];
            for (int i = 0; i < movable.size(); i++) {
                choices[i] = "말 " + (i + 1);
            }
            int selected = JOptionPane.showOptionDialog(
                    null,
                    "이동할 말을 선택하세요 (" + steps + "칸):",
                    "말 선택",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    choices,
                    choices[0]
            );
            if (selected == JOptionPane.CLOSED_OPTION) return;

            selectedPiece = movable.get(selected);
        }
        stepQueue.poll();
        new PieceMoveController(board).movePiece(selectedPiece, steps);
        boardView.repaint();

        // 다음 step도 처리
        SwingUtilities.invokeLater(() -> {
            processNextMove(players, currentPlayerIndex, board, boardView, turnLabel);
        });
    }



}
