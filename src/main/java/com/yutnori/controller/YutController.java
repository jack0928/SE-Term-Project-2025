package com.yutnori.controller;

import com.yutnori.model.Yut;
import com.yutnori.view.YutResultView;

import javax.swing.*;

public class YutController {
    private final Yut yut;
    private final YutResultView view;

    // Constructor
    public YutController(Yut yut, YutResultView view) {
        this.yut = yut;
        this.view = view;
        initEventHandlers();
    }

    // Method: 버튼 이벤트 설정
    private void initEventHandlers() {
        view.getThrowRandomButton().addActionListener(e -> performThrow(true));
        view.getSelectYutButton().addActionListener(e -> performThrow(false));
    }

    // Method: 윷 던지기 수행
    public void performThrow(boolean isRandom) {
        try {
            if (isRandom) {
                yut.throwRandomYut();
            } else {
                int selectedResult = view.getSelectedYutValue();
                yut.throwSelectYut(selectedResult);
            }
            updateView();
        } catch (IllegalArgumentException ex) {
            System.err.println("선택 오류: " + ex.getMessage());
        }
    }

    // Method: View 업데이트
    public void updateView() {
        view.setYutResult(yut);
    }
}
