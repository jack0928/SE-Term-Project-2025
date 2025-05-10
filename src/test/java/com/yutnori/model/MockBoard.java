package com.yutnori.model;

import java.awt.*;
import java.util.ArrayList;

public class MockBoard extends Board {

    public MockBoard() {
        initializeCells();
        initializeNodePositions();
        initializeOuterPath();
        initializeInnerPath();
    }

    @Override
    protected void initializeCells() {
        this.cells = new ArrayList<>();
        // 테스트용으로 5개의 셀만 생성
        for (int i = 0; i < 5; i++) {
            cells.add(new Cell(i));
        }
    }

    @Override
    public Cell getDestinationCell(Cell current, int steps, Board board, Piece piece) {
        // 단순히 현재 위치에서 steps 더한 인덱스의 Cell 반환 (순환 포함)
        if (current == null || cells.isEmpty()) return null;
        int currentIndex = cells.indexOf(current);
        if (currentIndex == -1) return null;

        int destinationIndex = (currentIndex + steps) % cells.size();
        return cells.get(destinationIndex);
    }

    @Override
    public boolean isCorner(int id) {
        return id % 2 == 0; // 테스트용: 짝수 ID는 코너라고 가정
    }

    @Override
    public boolean isCentre(int id) {
        return id == 2; // 테스트용: ID 2번 셀을 센터로 지정
    }

    @Override
    protected void initializeNodePositions() {
        for (int i = 0; i < cells.size(); i++) {
            nodePositions.put(i, new Point(100 + i * 50, 100)); // 일렬 배치
        }
    }

    @Override
    protected void initializeOuterPath() {
        // 간단한 일렬 연결 구조
        outerPath.add(new int[]{0, 1});
        outerPath.add(new int[]{1, 2});
        outerPath.add(new int[]{2, 3});
        outerPath.add(new int[]{3, 4});
    }

    @Override
    protected void initializeInnerPath() {
        // innerPath는 비워둬도 테스트에는 무방
        innerPath.clear();
    }
}