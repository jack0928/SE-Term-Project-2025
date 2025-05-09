package com.yutnori;

import com.yutnori.controller.GameController;


import javax.swing.*;

public class YutnoriApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
        });
    }
}

