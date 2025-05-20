package com.yutnori.viewInterface;

import com.yutnori.model.Yut;

public interface YutResultViewInterface {
    int getSelectedYutValue();              // 유저가 선택한 윷 값
    void setYutResult(Yut currentYut);      // 윷 결과 시각화
}