package com.yutnori.model;

import java.util.*;

public class Yut {


    private final List<Integer> results = new ArrayList<>(); // 윷 결과값

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
        double r = Math.random();  // 0.0 ~ 1.0
        int result;


        if (r < 1.0 / 16) result = -1;         // 빽도
        else if (r < 4.0 / 16) result = 1;     // 도
        else if (r < 10.0 / 16) result = 2;    // 개
        else if (r < 14.0 / 16) result = 3;    // 걸
        else if (r < 15.0/ 16) result = 4;    // 윷
        else result = 5;                       // 모
        
        results.add(result);
    }

    // 가장 마지막 결과만 반환
    public int getLastResult() {
        if (results.isEmpty()) {
            throw new IllegalStateException("아직 윷을 던지지 않았습니다."); // 예외 처리 추가
        }
        return results.getLast();
    }

    //view 작성중 필요하여 추가
    public boolean isEmpty() {
        return results.isEmpty(); // 결과 리스트가 비어있으면 true 반환
    }




}
