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
    void testThrowSelectYut_boundaryValidInputs() {
        // 가장자리 유효값 확인
        assertDoesNotThrow(() -> yut.throwSelectYut(-1));
        assertDoesNotThrow(() -> yut.throwSelectYut(1));
        assertDoesNotThrow(() -> yut.throwSelectYut(5));
    }

    @Test
    void testThrowRandomYut_distribution() {
        int[] counts = new int[7]; // -1~5 -> 인덱스 0~6 (0은 사용 안 함)
        for (int i = 0; i < 10000; i++) {
            yut = new Yut();
            yut.throwRandomYut();
            int result = yut.getLastResult();
            switch (result) {
                case -1 -> counts[0]++;
                case 1 -> counts[1]++;
                case 2 -> counts[2]++;
                case 3 -> counts[3]++;
                case 4 -> counts[4]++;
                case 5 -> counts[5]++;
                default -> fail("잘못된 윷 결과값: " + result);
            }
        }

        // 각 값이 최소 한 번은 나왔는지 확인 (확률 기반 확인)
        for (int i = 0; i <= 5; i++) {
            assertTrue(counts[i] > 0, "값 " + (i == 0 ? -1 : i) + "이 한 번도 나오지 않음");
        }
    }

    @Test
    void testGetResults_returnsSameList() {
        yut.throwSelectYut(2);
        List<Integer> results = yut.getResults();
        assertEquals(1, results.size());
        assertEquals(2, results.get(0));
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