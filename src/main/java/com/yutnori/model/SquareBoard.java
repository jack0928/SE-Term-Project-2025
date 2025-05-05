package com.yutnori.model;

import java.awt.*;
import java.util.*;

public class SquareBoard extends Board {

    public SquareBoard() {
        initializeCells();
        initializeNodePositions();
        initializeOuterPath();
        initializeInnerPath();
    }
    @Override
    public boolean isCorner(int id) {
        return id == 0 || id == 5 || id == 10 || id == 15; // 그림에 따라 각각 코너를 정의
    }

    @Override
    public boolean isCentre(int id) {
        return id == 22; // 그림에 따라 센터 노드를 정의
    }

    @Override
    protected void initializeCells() {
        cells = new ArrayList<>();
        for (int id = 0; id <= 24; id++) {
            cells.add(new Cell(id, isCentre(id), isCorner(id)));
        }
    }

    @Override
    protected void initializeNodePositions() {
        // starting from the beginning node (counter-clockwise)
        int centreX = 350;
        int centreY = 350;
        int nodeId = 0;
        nodePositions.put(nodeId, new Point(600, 600)); // beginning node

        for (int i = 1; i <= 5; i++) {
            nodePositions.put(++nodeId, new Point(600, 600- (i * 100))); // right-side nodes (우측 노드들)
        }

        for (int i = 1; i <= 5; i++) {
            nodePositions.put(++nodeId, new Point(600 - (i * 100), 100)); // upper-side nodes (상단 노드들)
        }

        for (int i = 1; i <= 5; i++) {
            nodePositions.put(++nodeId, new Point(100, 100 + (i * 100))); // left-side nodes (좌측 노드들)
        }

        for (int i = 1; i <= 5; i++) {
            nodePositions.put(++nodeId, new Point(100 + (i * 100), 600)); // bottom-side nodes (하단 노드들)
        }


        nodePositions.put(22, new Point(centreX, centreY)); // center node (센터 노드)


        // 대각선 노드들의 위치 정의.

        int[][] cornerInnerNodeId = {
                // 첫번째 element: 코너 노드 id
                // 두번째 element: 센터에 가까운 노드 id
                // 세번째 element: 코너에 가까운 노드 id
                {0, 27, 28},   // (센터에서 기준) 증가
                {5, 21, 20},   // 감소
                {10, 26, 25},  // 감소
                {15, 23, 24},  // 감소
        };

        generateDiagonalNodes(centreX, centreY, cornerInnerNodeId); // 대각선 노드들의 위치를 정의하는 함수 호출

    }

    @Override
    protected void initializeOuterPath() {
        for (int i = 0; i < 19; i++) {
            outerPath.add(new int[]{i, i + 1});
        }
        outerPath.add(new int[]{19, 0}); // 마지막 -> 첫 노드 연결
    }

    @Override
    protected void initializeInnerPath() {
        innerPath = Arrays.asList(
                new int[]{10, 25}, new int[]{25, 26}, new int[]{26, 22}, new int[]{22, 27}, new int[]{27, 28}, new int[]{28, 0}, // 좌상단 - 우하단 대각선 (각 노드 연결)
                new int[]{5, 20}, new int[]{20, 21}, new int[]{21, 22}, new int[]{22, 23}, new int[]{23, 24}, new int[]{24, 15} // 우상단 - 좌하단 대각선 (각 노드 연결)
        );
    }

    @Override
    public Cell getNextCell(Cell current, int steps) {
        int id = current.getId() + steps;
        if (id < 0) id = 0;
        if (id >= cells.size()) id = cells.size() - 1;

        return cells.get(id);
    }




}
