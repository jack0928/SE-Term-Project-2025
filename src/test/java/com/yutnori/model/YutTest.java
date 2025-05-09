package com.yutnori.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class YutTest {

    private Yut yut;

    @BeforeEach
    void setUp() {
        yut = new Yut();
    }

    @Test
    void testThrowSelectYut_validInput_shouldBeAddedToResult() {
        yut.throwSelectYut(1); // 도
        yut.throwSelectYut(5); // 모

        List<Integer> results = yut.getResults();
        assertEquals(2, results.size());
        assertEquals(1, results.get(0));
        assertEquals(5, results.get(1));
    }

    @Test
    void testThrowSelectYut_invalidInput_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> yut.throwSelectYut(0));
        assertThrows(IllegalArgumentException.class, () -> yut.throwSelectYut(-2));
        assertThrows(IllegalArgumentException.class, () -> yut.throwSelectYut(6));
    }

    @Test
    void testThrowRandomYut_shouldAddResultInValidRange() {
        yut.throwRandomYut();

        List<Integer> results = yut.getResults();
        assertEquals(1, results.size());

        int result = results.getFirst();
        assertTrue(result >= -1 && result <= 5 && result != 0,
                "결과는 -1, 1~5 사이여야 합니다. 현재 결과: " + result);
    }

    @Test
    void testGetLastResult_shouldReturnLastResult() {
        yut.throwSelectYut(3);  // 걸
        yut.throwSelectYut(4);  // 윷

        int last = yut.getLastResult();
        assertEquals(4, last);
    }

    @Test
    void testGetLastResult_whenEmpty_shouldThrowException() {
        assertThrows(IllegalStateException.class, () -> yut.getLastResult());
    }

    @Test
    void testIsEmpty_initialState_shouldReturnTrue() {
        assertTrue(yut.isEmpty());
    }

    @Test
    void testIsEmpty_afterThrow_shouldReturnFalse() {
        yut.throwSelectYut(2);  // 개
        assertFalse(yut.isEmpty());
    }
}