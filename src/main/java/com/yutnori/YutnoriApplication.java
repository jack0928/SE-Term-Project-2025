package com.yutnori;

import com.yutnori.FXview.FXGameView;
import com.yutnori.controller.GameController;
import com.yutnori.view.GameView;
import com.yutnori.viewInterface.GameViewInterface;
import javafx.application.Application;
import javafx.stage.Stage;


import javax.swing.*;

public class YutnoriApplication {
    public static void main(String[] args) {
        Object[] options = {"Swing", "JavaFX"};
        int selected = JOptionPane.showOptionDialog(
                null,
                "어떤 UI로 실행하시겠습니까?",
                "UI 선택",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (selected == 0) {
            // Swing 실행
            SwingUtilities.invokeLater(() -> {
                GameViewInterface swingView = new GameView();
                GameController controller = new GameController(swingView);
            });

        }

        else { // JavaFX 실행
            Application.launch(YutnoriFXApplication.class);
        }
    }
}

