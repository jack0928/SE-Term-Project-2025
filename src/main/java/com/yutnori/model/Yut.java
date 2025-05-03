package com.yutnori.model;

import java.util.*;

public class Yut {


    private final List<Integer> results = new ArrayList<>(); // 윷 결과값

    private static final Random random = new Random();

    // 윷 결과 pool: 빽도(-1), 도(1), 개(2), 걸(3), 윷(4), 모(5)
    private static final int[] YUT_POOL = {-1, 1, 2, 3, 4, 5};

    // 윷 결과 반환
    public List<Integer> getResults() {
        return results;
    }

    // 테스트용: 선택한 윷 결과 추가
    public void throwSelectYut(int selectedResult) {
        if (selectedResult < -1 || selectedResult > 5 || selectedResult == 0) {
            throw new IllegalArgumentException("유효하지 않은 윷 결과입니다."); // 예외 처리 추가
        }
        results.add(selectedResult);
    }
    // 게임용: 무작위로 윷을 던짐
    public void throwRandomYut() {
        int rand = YUT_POOL[random.nextInt(YUT_POOL.length)];
        results.add(rand);
    }

    // 가장 마지막 결과만 반환
    public int getLastResult() {
        if (results.isEmpty()) {
            throw new IllegalStateException("아직 윷을 던지지 않았습니다."); // 예외 처리 추가
        }
        return results.getLast();
    }

    // 선택된 윷 결과 반환
    public int getResultAt(int index) {
        if (index < 0 || index >= results.size()) {
            throw new IndexOutOfBoundsException("요청한 윷 결과 인덱스가 범위를 벗어났습니다."); // 예외 처리 추가
        }
        return results.get(index);
    }

    // 모든 결과 초기화
    public void resetResults() {
        results.clear();
    }

    // 마지막 던진 결과가 윷(4) 또는 모(5)인지 확인
    public boolean lastIsExtraTurn() {
        int last = getLastResult();
        return last == 4 || last == 5; // 윷(4) 또는 모(5)일 경우 true 반환
    }

    // 텍스트 변환 (뷰에서 사용) , 사용할지는 모름
    public static String getResultText(int result) {
        return switch (result) {
            case -1 -> "빽도";
            case 1 -> "도";
            case 2 -> "개";
            case 3 -> "걸";
            case 4 -> "윷";
            case 5 -> "모";
            default -> "알 수 없음";
        };
    }



}
