package com.yutnori.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Board {
    protected List<Cell> cells; // 그래프의 모든 노드(Cell)
    protected Map<Integer, Point> nodePositions = new HashMap<>(); // 노드의 위치를 저장하는 맵 (key: 노드 id, value: Point 객체)
    private static final Map<Integer, Integer> nextPositionGeneral = new HashMap<>(); // 시작 점이 분기를 만드는 노드가 아닐 때의 이동 경로
    private static final Map<Integer, Integer> nextPositionSpecial = new HashMap<>();    // 시작 노드가 분기를 만드는 중앙/코너 노드일 때의 이동 경로
    private static final int RADIUS = 40; // 일반 노드의 반지름 (flexible)
    private static final int CORNER_RADIUS = 60; // 코너 및 센터 노드의 반지름 (flexible)
    protected List<int[]> outerPath = new ArrayList<>(); // outerPath: 노드들 간의 연결을 저장하는 리스트 (각 연결은 int[]로 표현됨)
    protected List<int[]> innerPath = new ArrayList<>(); // innerPath: 노드들 간의 연결을 저장하는 리스트 (각 연결은 int[]로 표현됨)

    protected abstract void initializeCells();

    public List<Cell> getCells() { return cells; }
    public void setCells(List<Cell> cells) { this.cells = cells; }
    public abstract Cell getDestinationCell(Cell current, int steps, Board board, Piece piece); // Cell의 NextCell과 겹치지 않게 이름 변경

    // getter method for nodePositions
    public Map<Integer, Point> getNodePositions() { return nodePositions; }

    public Point getNodePosition(int id) { return nodePositions.get(id); } // 노드 id에 해당하는 위치를 반환하는 메소드

    // getter method for outerPath and innerPath
    public List<int[]> getOuterPath() { return outerPath; }
    public List<int[]> getInnerPath() { return innerPath; }

    // getter method for RADIUS and CORNER_RADIUS
    public static int getRadius() { return RADIUS; }
    public static int getCornerRadius() { return CORNER_RADIUS; }

    // 각 보드에서 코너 노드인지 / 센터 노드인지 판단하는 boolean type variable. 각 보드에 따라 다르게 정의되기 때문에 abstract method로 선언.
    public abstract boolean isCorner(int id);

    public abstract boolean isCentre(int id);

    protected abstract void initializeNodePositions();

    protected abstract void initializeOuterPath();

    protected abstract void initializeInnerPath();

    protected void generateInnerNodes(
            int centreX, int centreY,
            int[][] cornerInnerNodeId
    ) {
        for (int[] param : cornerInnerNodeId) {
            int cornerId = param[0];
            int innerId1 = param[1]; // 센터에 가까운 노드
            int innerId2 = param[2]; // 코너에 가까운 노드
            Point cornerPoint = nodePositions.get(cornerId);

            // Using Linear Interpolation to find the coordinates of the inner nodes.
            for (int j = 1; j <= 2; j++) {
                double ratio = j / 3.0;
                int x = (int) (centreX * (1 - ratio) + cornerPoint.x * ratio);
                int y = (int) (centreY * (1 - ratio) + cornerPoint.y * ratio);

                int finalId = (j == 1) ? innerId1 : innerId2;
                nodePositions.put(finalId, new Point(x, y));
            }
        }
    }
}
