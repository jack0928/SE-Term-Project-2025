package com.yutnori.model;

import java.awt.*;
import java.util.*;
import java.util.stream.IntStream;

public class SquareBoard extends Board {
    private static final int[] CORNERS = {0, 5, 10, 15};    // 사각 윷판의 코너 ID
    private static final int CENTRE = 22;                   // 코드 가독성을 위한 리팩토링

    private static final Map<Integer,Integer> CORNER_BRANCH;
    static {
        Map<Integer,Integer> m = new HashMap<>();
        m.put( 5,  20);  // 5 -> 20
        m.put(10,  25);  // 10 -> 25
        m.put(22,  27);  // 22 -> 27
        CORNER_BRANCH = Collections.unmodifiableMap(m);
    }
    public SquareBoard() {
        initializeCells();
        initializeNodePositions();
        initializeOuterPath();
        initializeInnerPath();
    }


    @Override
    protected void initializeCells() {
        cells = new ArrayList<>();
        for (int id = 0; id <= 24; id++) {
            cells.add(new Cell(id, isCentre(id), isCorner(id)));
        }
    }
    @Override
    public boolean isCorner(int id) { return IntStream.of(CORNERS).anyMatch(c -> c == id); }

    @Override
    public boolean isCentre(int id) { return id == CENTRE; }

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

    //이런게 링크드리스트 꼴로 연결되어야 할 거 같은데 이 array들로 다음 path를 어떻게 찾을 건지 잘 모르겠음
    @Override
    protected void initializeInnerPath() {
        innerPath = Arrays.asList(
                new int[]{10, 25}, new int[]{25, 26}, new int[]{26, 22}, new int[]{22, 27}, new int[]{27, 28}, new int[]{28, 0}, // 좌상단 - 우하단 대각선 (각 노드 연결)
                new int[]{5, 20}, new int[]{20, 21}, new int[]{21, 22}, new int[]{22, 23}, new int[]{23, 24}, new int[]{24, 15} // 우상단 - 좌하단 대각선 (각 노드 연결)
        );
    }

    @Override
    public Cell getNextCell(Cell current, int steps) {
        // 1. 기본 이동
        int id = (current == null ? 0 : current.getId() + steps);
        if (id < 0) id = 0;
        if (id >= cells.size()) id = cells.size() - 1;

        // 2. “딱 코너에 착지했다면” 안쪽으로 이동
        if (isCorner(id)) {
            Integer branchId = CORNER_BRANCH.get(id);
            if (branchId != null) {
                return cells.get(branchId);
            }
        }

        // 3,  그 외는 그대로
        return cells.get(id);
    }




}
