package com.yutnori.view;

import com.yutnori.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TestPieceView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. 보드 생성 (보드를 바꿔서 테스트 하려면 별도로 주석처리된 부분을 수정해야 작동: "!!! 여기서 보드 종류 설정" 이라고 주석처리된 부분)
            // Tip: Search '!!!' to easily find the places to change the board type.
            SquareBoard squareBoard = new SquareBoard();
            PentagonBoard pentagonBoard = new PentagonBoard();
            HexagonBoard hexagonBoard = new HexagonBoard();

            // 2. 플레이어 및 말 설정
            List<Player> players = new ArrayList<>();
            List<Piece> pieces = new ArrayList<>();

            for (int i = 1; i <= 2; i++) {
                Player player = new Player("Player " + i);  // ID는 자동 증가
                Piece piece = new Piece(0, player);
                piece.moveTo(pentagonBoard.getCells().get(0)); // !!! 여기서 보드 종류 설정
                player.addPiece(piece);

                players.add(player);
                pieces.add(piece);
            }

            // 3. 윷, 결과 라벨, 턴 관리 변수
            Yut yut = new Yut();
            JLabel resultLabel = new JLabel("윷 결과: -");
            JLabel turnLabel = new JLabel("현재 턴: Player 1");
            final int[] currentPlayerIndex = {0}; // 턴 순서 관리

            // 4. 버튼 이벤트
            JButton throwButton = new JButton("윷 던지고 이동");
            throwButton.addActionListener(e -> {
                Player currentPlayer = players.get(currentPlayerIndex[0]);
                Piece currentPiece = currentPlayer.getPieces().get(0);

                yut.throwRandomYut();
                int steps = yut.getLastResult();
                resultLabel.setText("윷 결과: " + steps);


                // ✅ 보드에 올라가지 않은 경우 → 출발점에 올려놓기만 함
                if (!currentPiece.isOnBoard()) {
                    // 무조건 시작 셀로
                    Cell startCell = pentagonBoard.getCells().get(0); // !!! 여기서 보드 종류 설정
                    currentPiece.moveTo(startCell);  // 내부에서 isOnBoard=true 처리됨
                    pentagonBoard.repaint(); // !!! 여기서 보드 종류 설정
                    return; // ❗더 이상 이동 안 하고 종료 (getNextCell 호출 안 함)
                }

                Cell current = currentPiece.getPosition();
                Cell next = pentagonBoard.getNextCell(current, steps); // !!! 여기서 보드 종류 설정
                current.removePiece(currentPiece);
                currentPiece.moveTo(next);

                pentagonBoard.repaint(); // !!! 여기서 보드 종류 설정


                // 추가 턴이 아닌 경우에만 턴 넘김
                if (!yut.lastIsExtraTurn()) {
                    currentPlayerIndex[0] = (currentPlayerIndex[0] + 1) % players.size();
                }

                turnLabel.setText("현재 턴: Player " + (currentPlayerIndex[0] + 1));
            });

            // 5. 프레임 구성
            JFrame frame = new JFrame("Two Player Game Test View");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 900);
            frame.setLayout(new BorderLayout());

            JPanel controlPanel = new JPanel(new GridLayout(2, 1));
            JPanel statusPanel = new JPanel();
            statusPanel.add(resultLabel);
            statusPanel.add(turnLabel);

            controlPanel.add(statusPanel);
            controlPanel.add(throwButton);

            frame.add(pentagonBoard, BorderLayout.CENTER); // !!! 여기서 보드 종류 설정
            frame.add(controlPanel, BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }
}
