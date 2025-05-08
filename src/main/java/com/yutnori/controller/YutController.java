package com.yutnori.controller;

import com.yutnori.model.Yut;
import com.yutnori.view.YutResultView;

import javax.swing.*;

public class YutController {
    private final Yut yut;
    private final YutResultView view;

    public YutController(Yut yut, YutResultView view) {
        this.yut = yut;
        this.view = view;
        // 랜덤 윷 던지기 버튼 이벤트
        view.getThrowRandomButton().addActionListener(e -> {
            yut.throwRandomYut();
            view.setYutResult(yut);
        });
        // 선택 윷 던지기 버튼 이벤트
        view.getSelectYutButton().addActionListener(e -> {
            try {
                int selectedResult = view.getSelectedYutValue();
                yut.throwSelectYut(selectedResult);
                view.setYutResult(yut);
            } catch (IllegalArgumentException ex) {
                System.err.println("선택 오류: " + ex.getMessage());
            }
        });
    }
}
