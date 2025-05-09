package com.yutnori.model;

import java.awt.*;
import java.util.*;
import java.util.stream.IntStream;

public class PentagonBoard extends Board {
    private static final int[] CORNERS = {0, 5, 10, 15, 20};    // 사각 윷판의 코너 ID
    private static final int CENTRE = 27;                   // 코드 가독성을 위한 리팩토링

    private static final Map<Integer,Integer> nextPositionGeneral;
    static {
        Map<Integer,Integer> m = new HashMap<>();
        // 바깥쪽
        for (int i = 0; i < 24; i++) {
            m.put(i,i+1);
        }
        m.put(25,26); m.put(26,27); m.put(30,31); m.put(31,27);
        m.put(32,33); m.put(33,27); m.put(27,28); m.put(28,29);
        m.put(29,20); m.put(34,35); m.put(35,0); m.put(24,0);
        nextPositionGeneral = Collections.unmodifiableMap(m);
    }
    private static final Map<Integer,Integer> nextPositionSpecial;
    static {
        Map<Integer,Integer> m = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            if (i == 5) {m.put(i,25); continue; }
            if (i == 10) {m.put(i,30); continue;}
            if (i == 15) {m.put(i,32); continue;}
            m.put(i,(i+1));
        }
        m.put(25,26); m.put(26,27); m.put(30,31); m.put(31,27);
        m.put(32,33); m.put(33,27); m.put(27,34); m.put(28,29);
        m.put(29,20); m.put(34,35); m.put(35,0); m.put(24,0);
        nextPositionSpecial = Collections.unmodifiableMap(m);
    }


    public PentagonBoard() {
        initializeCells();
        initializeNodePositions();
        initializeOuterPath();
        initializeInnerPath();
    }

    @Override
    protected void initializeCells() {
        cells = new ArrayList<>();
        for (int id = 0; id <= 35; id++) {
            cells.add(new Cell(id, isCentre(id), isCorner(id)));
        }
    }

    @Override
    public boolean isCorner(int id) { return IntStream.of(CORNERS).anyMatch(c -> c == id); }

    @Override
    public boolean isCentre(int id) { return id == CENTRE; }

    @Override
    protected void initializeNodePositions() {
        // centre node
        int radius = 300;
        int centreX = 400;
        int centreY = 400;

        nodePositions.put(27, new Point(centreX, centreY)); // centre node (센터 노드)


        // coordinate of the pentagon vertices (5개의 꼭짓점의 좌표)

        Point[] cornerPoints = new Point[5];
        double angleOffset = -4 * (Math.PI / 10); // -4π/10 라디안만큼 회전시켜 왼쪽 위가 시작점이 되도록 조정
        for (int i = 0; i < 5; i++) {
            double angle = -2 * Math.PI * i / 5 - Math.PI / 2 + angleOffset; // 각 꼭짓점의 각도 (라디안)
            int x = (int) (centreX + radius * Math.cos(angle));
            int y = (int) (centreY + radius * Math.sin(angle));
            cornerPoints[i] = new Point(x, y);
        }


        // outerPath에 있는 노드들의 위치 정의.
        int nodeId = 0;

        for (int i = 0; i < 5; i++) {
            Point start = cornerPoints[i];
            Point end = cornerPoints[(i + 1) % 5]; // 다음 꼭짓점으로 연결
            for (int j = 0; j < 5; j++) {
                double ratio = j / 5.0;
                int x = (int) (start.x + ratio * (end.x - start.x));
                int y = (int) (start.y + ratio * (end.y - start.y));
                nodePositions.put(nodeId++, new Point(x, y)); // 각 꼭짓점 사이의 노드들
            }
        }

        // 대각선 노드들의 위치 정의.
        int[][] cornerInnerNodeId = {
                // 첫번째 element: 코너 노드 id
                // 두번째 element: 센터에 가까운 노드 id
                // 세번째 element: 코너에 가까운 노드 id
                {0, 34, 35},   // (센터에서 기준) 증가
                {5, 26, 25},   // 감소
                {10, 31, 30},  // 감소
                {15, 33, 32},  // 감소
                {20, 28, 29}   // 증가
        };

        generateInnerNodes(centreX, centreY, cornerInnerNodeId); // 대각선 노드들의 위치를 정의하는 함수 호출

    }

    @Override
    protected void initializeOuterPath() {
        outerPath = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            outerPath.add(new int[]{i, i + 1});
        }
        outerPath.add(new int[]{24, 0}); // 마지막 -> 첫 노드 연결
    }
    @Override
    protected void initializeInnerPath() {
        innerPath = Arrays.asList(
                new int[]{0, 35}, new int[]{35, 34}, new int[]{34, 27}, // centre - 좌상단 (0번) 연결 대각선
                new int[]{27, 33}, new int[]{33, 32}, new int[]{32, 15}, // centre - 우상단 (15번) 연결 대각선
                new int[]{5, 25}, new int[]{25, 26}, new int[]{26, 27}, // centre - 좌하단 (5번) 연결 대각선
                new int[]{27, 28}, new int[]{28, 29}, new int[]{29, 20}, // centre - 상단 (20번) 연결 대각선
                new int[]{10, 30}, new int[]{30, 31}, new int[]{31, 27} // centre - 우하단 (10번) 연결 대각선
        );
    }

    @Override
    public Cell getDestinationCell(Cell current, int steps, Board board, Piece piece) {
        if (current == null) return cells.get(0);
        Cell destCell = current;

        for (int i = 0; i < steps; i++){
            piece.history.push(destCell.getId());
            if (i==0) destCell =  board.getCells().get(nextPositionSpecial.get(destCell.getId()));
            else destCell =  board.getCells().get(nextPositionGeneral.get(destCell.getId()));
        }


        return destCell;

    }


}
