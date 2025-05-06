package com.yutnori.view;

import com.yutnori.model.HexagonBoard;
import com.yutnori.model.SquareBoard;

import javax.swing.*;

public class TestHexaBoardView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Yut Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);

            HexagonBoard board = new HexagonBoard();     // ✅ 모델 생성
            BoardView boardView = new BoardView(board); // ✅ 뷰 생성

            frame.add(boardView);                      // ✅ 뷰를 추가

            frame.setVisible(true);
        });
    }
}
