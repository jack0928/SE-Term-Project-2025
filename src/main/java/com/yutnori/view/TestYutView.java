package com.yutnori.view;

import com.yutnori.model.Yut;
import com.yutnori.view.YutResultView;
import com.yutnori.controller.YutController;

import javax.swing.*;
import java.awt.*;

public class TestYutView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 프레임 설정
            JFrame frame = new JFrame("윷 테스트");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 300);

            // 모델 & 뷰 & 컨트롤러 구성
            Yut yut = new Yut();
            YutResultView resultView = new YutResultView();
            new YutController(yut, resultView);  // 이벤트 연결

            // 뷰만 프레임에 추가
            frame.add(resultView);
            frame.setVisible(true);
        });
    }
}
