package com.yutnori.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class HexagonBoard extends Board {

    public HexagonBoard() {
        initializeCells();
        initializeNodePositions();
        initializeOuterPath();
        initializeInnerPath();
    }
    @Override
    protected void initializeCells() {
        cells = new ArrayList<>();
        for (int id = 0; id <= 42; id++) {
            cells.add(new Cell(id, isCentre(id), isCorner(id)));
        }
    }


    @Override
    protected boolean isCorner(int id) {
        return id == 0 || id == 5 || id == 10 || id == 15 || id == 20 || id == 25; // corner 노드일때만 true 반환
    }

    @Override
    protected boolean isCentre(int id) {
        return id == 32; // centre 노드일때만 true 반환
    }

    @Override
    protected void initializeNodePositions() {
        int radius = 300; // Hexagon 전체의 radius
        int centreX = 400; // Hexagon의 중심 x좌표
        int centreY = 400; // Hexagon의 중심 y좌표

        nodePositions.put(32, new Point(centreX, centreY)); // centre node (센터 노드) 좌표 정의

        // i) Hexagon의 outerPath 구성
        // 1. 육각형 꼭짓점 (corner node)의 좌표값 계산
        Point[] cornerPoints = new Point[6];
        double angleOffset = - Math.PI / 2; // Leftmost corner node가 시작점이 되도록 조정

        for (int i = 0; i < 6; i++) {
            double angle = -2 * Math.PI * i / 6 - Math.PI / 2 + angleOffset;
            // Hexagon의 각 vertices는 60도 간격으로 배치 (PI/3 in radian)
            int x = (int) (centreX + radius * Math.cos(angle));
            int y = (int) (centreY + radius * Math.sin(angle));
            cornerPoints[i] = new Point(x, y); // 각 꼭짓점의 좌표값 저장
        }

        // 2. Corner node들을 연결하여 outerPath를 구성
        int nodeId = 0;
        for (int i = 0; i < 6; i++) {
            Point start = cornerPoints[i];
            Point end = cornerPoints[(i + 1) % 6];

            for (int j = 0; j < 5; j++) { // corner와 corner 사이를 비율을 만듬: 0.0, 0.2, 0.4, 0.6, 0.8
                double ratio = j / 5.0; // 구간비율: 0, 0.2, 0.4, 0.6, 0.8
                int x = (int) ((1 - ratio) * start.x + ratio * end.x); // calculation of x-coordinate using linear interpolation
                int y = (int) ((1 - ratio) * start.y + ratio * end.y); // calculation of y-coordinate in the same manner
                nodePositions.put(nodeId++, new Point(x, y)); // 각 노드 할당
            }
        }

        // ii) Hexagon의 innerPath 구성
        // 1. 대각선 노드들의 위치 정의

        int[][] cornerInnerNodeId = {
                // 첫번째 element: 코너 노드 id
                // 두번째 element: 센터에 가까운 노드 id
                // 세번째 element: 코너에 가까운 노드 id
                {0, 41, 42},   // (센터에서 기준) 증가
                {5, 31, 30},   // 감소
                {10, 36, 35},  // 감소
                {15, 40, 39},  // 감소
                {20, 37, 38},   // 증가
                {25, 33, 34}   // 증가
        };

        generateDiagonalNodes(centreX, centreY, cornerInnerNodeId); // 대각선 노드들의 위치를 정의하는 함수 호출
    }

    @Override
    protected void initializeOuterPath() {
        outerPath = new ArrayList<>();
        for (int i = 0; i < 29; i++) {
            outerPath.add(new int[]{i, i + 1});
        }
        outerPath.add(new int[]{29, 0}); // 마지막 -> 첫 노드 연결
    }

    @Override
    protected void initializeInnerPath() {
        innerPath = Arrays.asList(
                new int[]{0, 42}, new int[]{42, 41}, new int[]{41, 32}, // centre - 좌단 (0번) 연결 대각선
                new int[]{32, 31}, new int[]{31, 30}, new int[]{30, 5}, // centre - 좌하단 (5번) 연결 대각선
                new int[]{32, 36}, new int[]{36, 35}, new int[]{35, 10}, // centre - 우하단 (10번) 연결 대각선
                new int[]{32, 40}, new int[]{40, 39}, new int[]{39, 15}, // centre - 우단 (15번) 연결 대각선
                new int[]{32, 37}, new int[]{37, 38}, new int[]{38, 20}, // centre - 우상단 (20번) 연결 대각선
                new int[]{32, 33}, new int[]{33, 34}, new int[]{34, 25} // centre - 좌상단 (25번) 연결 대각선
        );
    }
}
