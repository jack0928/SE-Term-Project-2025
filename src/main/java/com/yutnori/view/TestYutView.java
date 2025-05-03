package com.yutnori.view;

import com.yutnori.model.Yut;
import com.yutnori.controller.YutController;

import javax.swing.*;
import java.awt.*;
public class TestYutView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("윷놀이 시각화");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLayout(new BorderLayout());

            Yut yut = new Yut();
            YutResultView resultView = new YutResultView();
            JButton throwButton = new JButton("윷 던지기");

            // 컨트롤러 연결
            new YutController(yut, resultView, throwButton);

            // 프레임 구성
            frame.add(resultView, BorderLayout.CENTER);
            frame.add(throwButton, BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }
}
