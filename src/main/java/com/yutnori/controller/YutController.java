package com.yutnori.controller;

import com.yutnori.model.Yut;
import com.yutnori.view.YutResultView;

import javax.swing.*;

public class YutController {
    private final Yut yut;
    private final YutResultView view;

    public YutController(Yut yut, YutResultView view, JButton throwButton) {
        this.yut = yut;
        this.view = view;

        throwButton.addActionListener(e -> {
            yut.throwRandomYut();       // 모델: 윷 던지기
            view.setYutResult(yut);     // 뷰 갱신
        });
    }
}
