package com.yutnori.view;

import com.yutnori.model.PentagonBoard;

import javax.swing.*;

public class TestPentaBoardView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Yut Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);

            frame.add(new PentagonBoard());

            frame.setVisible(true);
        });
    }
}
