package com.yutnori.view;

import com.yutnori.model.PentagonBoard;

import javax.swing.*;

public class TestPentaBoardView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Yut Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);

            PentagonBoard board = new PentagonBoard();     // ✅ 모델 생성
            BoardView boardView = new BoardView(board); // ✅ 뷰 생성

            frame.add(boardView);                      // ✅ 뷰를 추가

            frame.setVisible(true);
        });
    }
}
