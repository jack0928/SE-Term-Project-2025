package com.yutnori.view;

import com.yutnori.controller.PieceMoveController;
import com.yutnori.model.*;
import javax.swing.*;
import java.awt.*;

public class TestPieceView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // ===== 1. 보드 선택 =====
            String[] options = {"Square", "Pentagon", "Hexagon"};
            String selected = (String) JOptionPane.showInputDialog(
                    null,
                    "보드 형태를 선택하세요:",
                    "보드 선택",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );
            if (selected == null) System.exit(0);

            Board board = switch (selected) {
                case "Pentagon" -> new PentagonBoard();
                case "Hexagon" -> new HexagonBoard();
                default -> new SquareBoard();
            };

            // ===== 2. 모델 생성 =====
            Player player = new Player("Player1");
            Piece piece = new Piece(0, player);
            player.addPiece(piece);
            piece.moveTo(board.getCells().get(0));  // 시작 위치

            // ===== 3. 컨트롤러 생성 =====
            PieceMoveController mover = new PieceMoveController(board);

            // ===== 4. 뷰 생성 =====
            BoardView boardView = new BoardView(board);

            Yut yut = new Yut();
            YutResultView yutResultView = new YutResultView();
            yutResultView.setPreferredSize(new Dimension(400, 150));


            JButton throwButton = new JButton("윷 던지기");
            throwButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            throwButton.addActionListener(e -> {
                yut.throwRandomYut();
                int steps = yut.getLastResult();

                // 결과 업데이트
                yutResultView.setYutResult(yut);

                // 말 이동
                mover.movePiece(piece, steps);

                boardView.repaint();
            });

            // ===== 5. 우측 패널 (윷 시각화 + 결과 텍스트 + 버튼) =====
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.add(yutResultView);
            rightPanel.add(throwButton);


            // ===== 6. 전체 패널 =====
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(boardView, BorderLayout.CENTER);     // 보드
            panel.add(rightPanel, BorderLayout.EAST);      // 윷 결과

            // ===== 7. 프레임 설정 =====
            JFrame frame = new JFrame("Yut Piece Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(panel);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
