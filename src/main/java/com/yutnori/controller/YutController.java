package com.yutnori.controller;

import com.yutnori.model.Yut;
import com.yutnori.view.YutResultView;
import com.yutnori.viewInterface.YutResultViewInterface;

public class YutController {
    private final Yut yut;
    private final YutResultViewInterface view;

    // Constructor
    public YutController(Yut yut, YutResultViewInterface view) {
        this.yut = yut;
        this.view = view;

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
