package com.yutnori.controller;

import com.yutnori.model.Yut;
import com.yutnori.view.YutResultView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

class YutControllerTest {

    private Yut yut;
    private YutResultView view;
    private YutController controller;

    @BeforeEach
    void setUp() {
        yut = mock(Yut.class);
        view = mock(YutResultView.class);
        controller = new YutController(yut, view);
    }

    @Test
    void testPerformThrowRandom_callsThrowRandomYutAndUpdatesView() {
        controller.performThrow(true);

        verify(yut, times(1)).throwRandomYut();
        verify(view, times(1)).setYutResult(yut);
    }

    @Test
    void testPerformThrowSelect_callsThrowSelectYutWithCorrectValue() {
        when(view.getSelectedYutValue()).thenReturn(2);

        controller.performThrow(false);

        verify(yut, times(1)).throwSelectYut(2);
        verify(view, times(1)).setYutResult(yut);
    }

    @Test
    void testPerformThrowSelect_invalidValue_doesNotThrow() {
        when(view.getSelectedYutValue()).thenReturn(-2);

        // 예외 던지도록 설정
        doThrow(new IllegalArgumentException("유효하지 않은 윷 결과입니다."))
                .when(yut).throwSelectYut(-2);

        controller.performThrow(false);

        verify(yut).throwSelectYut(-2); // 예외는 발생해도 호출 자체는 됨
        verify(view, never()).setYutResult(any());
    }
}