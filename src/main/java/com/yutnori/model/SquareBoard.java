package com.yutnori.model;

import java.awt.*;
import java.util.*;
import java.util.stream.IntStream;

public class SquareBoard extends Board {
    private static final int[] CORNERS = {0, 5, 10, 15};    // 사각 윷판의 코너 ID
    private static final int[] CENTRE = {22, 27};                   // 코드 가독성을 위한 리팩토링

    private static final Map<Integer, Integer> nextPositionGeneral;
    static {
        Map<Integer,Integer> m = new HashMap<>();
        for (int i = 0; i < 19; i++) {
            m.put(i,i+1);
        }
        m.put(19,0);
        m.put(20,21); m.put(21,22); m.put(22,23); m.put(23,24); m.put(24,15);
        m.put(25,26); m.put(26,27); m.put(27,28); m.put(28,29); m.put(29,0);
        nextPositionGeneral = Collections.unmodifiableMap(m);
    }
    private static final Map<Integer, Integer> nextPositionSpecial;
    static {
        Map<Integer,Integer> m = new HashMap<>();
        for (int i = 0; i < 19; i++) {
            if (i == 5) {m.put(i,20); continue; }
            if (i == 10) {m.put(i,25); continue;}
            m.put(i,(i+1));
        }
        m.put(19,0);
        m.put(20,21); m.put(21,22); m.put(22,28); m.put(23,24); m.put(24,15);
        m.put(25,26); m.put(26,27); m.put(27,28); m.put(28,29); m.put(29,0);

        nextPositionSpecial = Collections.unmodifiableMap(m);
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
        for (int id = 0; id <= 29; id++) {
            cells.add(new Cell(id));
        }
    }
    @Override
    public boolean isCorner(int id) { return IntStream.of(CORNERS).anyMatch(c -> c == id); }

    @Override
    public boolean isCentre(int id) { return id == CENTRE[0] || id == CENTRE[1]; }

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
        nodePositions.put(27, new Point(centreX, centreY));


        // 대각선 노드들의 위치 정의.

        int[][] cornerInnerNodeId = {
                // 첫번째 element: 코너 노드 id
                // 두번째 element: 센터에 가까운 노드 id
                // 세번째 element: 코너에 가까운 노드 id
                {0, 28, 29},   // (센터에서 기준) 증가
                {5, 21, 20},   // 감소
                {10, 26, 25},  // 감소
                {15, 23, 24},  // 감소
        };

        generateInnerNodes(centreX, centreY, cornerInnerNodeId); // 대각선 노드들의 위치를 정의하는 함수 호출

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
                new int[]{5, 20}, new int[]{20, 21}, new int[]{21, 22}, new int[]{22, 23}, new int[]{23, 24}, new int[]{24, 15}, // 우상단 - 좌하단 대각선 (각 노드 연결)
                new int[] {22, 28}
        );
    }

    @Override
    public Cell getDestinationCell(Cell current, int steps, Board board, Piece piece) {
        if (current == null) return cells.get(0);
        Cell destCell = current;

        for (int i = 0; i < steps; i++){
            piece.history.push(destCell.getId());
            System.out.println("history : " + piece.history);
            if (i==0) destCell =  board.getCells().get(nextPositionSpecial.get(destCell.getId()));
            else destCell =  board.getCells().get(nextPositionGeneral.get(destCell.getId()));
        }


        return destCell;
    }




}
