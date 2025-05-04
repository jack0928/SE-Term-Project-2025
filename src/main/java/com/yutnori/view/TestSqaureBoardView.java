package com.yutnori.view;

import com.yutnori.model.SquareBoard;

import javax.swing.*;

public class TestSqaureBoardView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Yut Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(750, 750);

            frame.add(new SquareBoard());

            frame.setVisible(true);
        });

    }
}
