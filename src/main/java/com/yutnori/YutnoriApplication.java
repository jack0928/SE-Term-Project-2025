package com.yutnori;

import com.yutnori.controller.GameController;


import javax.swing.*;
import java.awt.*;

public class YutnoriApplication {
    public static void main(String[] args) {
        // Game Controller 생성 및 실행
        SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
        });
    }
}

