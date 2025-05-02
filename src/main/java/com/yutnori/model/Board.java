package com.yutnori.model;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Board extends JPanel {
    protected List<Cell> cells;




    protected abstract void initializeCells();

    // 아래 3개의 function들은 추후 logic을 위해 사용될지도 몰라서 일단 추상화 해놓음; 나중에 logic 구현 시 필요하면 사용 / 필요 없을 시 파기.
//    public List<Cell> getCells() { return cells; }
//    public abstract Cell getNextCell(Cell current, int steps);
//    public abstract boolean isValidMove(int currentId, int steps);


    protected Map<Integer, Point> nodePositions = new HashMap<>(); // 노드의 위치를 저장하는 맵 (key: 노드 id, value: Point 객체)
    protected List<int[]> outerPath = new ArrayList<>(); // outerPath: 노드들 간의 연결을 저장하는 리스트 (각 연결은 int[]로 표현됨)
    protected List<int[]> innerPath = new ArrayList<>(); // innerPath: 노드들 간의 연결을 저장하는 리스트 (각 연결은 int[]로 표현됨)

    protected static final int RADIUS = 40; // 일반 노드의 반지름 (flexible)
    protected static final int CORNER_RADIUS = 60; // 코너 및 센터 노드의 반지름 (flexible)

    // 각 보드에서 코너 노드인지 / 센터 노드인지 판단하는 boolean type variable. 각 보드에 따라 다르게 정의되기 때문에 abstract method로 선언.
    protected abstract boolean isCorner(int id);
    protected abstract boolean isCentre(int id);

    protected abstract void initializeNodePositions();
    protected abstract void initializeOuterPath();
    protected abstract void initializeInnerPath();

    protected void generateDiagonalNodes(
            int centreX, int centreY,
            int[][] cornerInnerNodeId
    ) {
        for (int[] param : cornerInnerNodeId) {
            int cornerId = param[0];
            int innerId1 = param[1]; // 센터에 가까운 노드
            int innerId2 = param[2]; // 코너에 가까운 노드
            Point cornerPoint = nodePositions.get(cornerId);

            for (int j = 1; j <= 2; j++) {
                double ratio = j / 3.0;
                int x = (int) (centreX * (1 - ratio) + cornerPoint.x * ratio);
                int y = (int) (centreY * (1 - ratio) + cornerPoint.y * ratio);

                int finalId = (j == 1) ? innerId1 : innerId2;
                nodePositions.put(finalId, new Point(x, y));
            }
        }
    }

    // 노드의 위치를 그리는 function
    protected void drawNodes(Graphics g) {
        for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
            int id = entry.getKey();
            Point p = entry.getValue();

            boolean corner = isCorner(id);
            boolean centre = isCentre(id);
            int radius = corner || centre? CORNER_RADIUS : RADIUS;

            g.setColor(Color.WHITE);
            g.fillOval(p.x - radius / 2, p.y - radius / 2, radius, radius);
            g.setColor(Color.BLACK);
            g.drawOval(p.x - radius / 2, p.y - radius / 2, radius, radius);

            if (corner || centre) {
                g.drawOval(p.x - radius / 2 + 3, p.y - radius / 2 + 3, radius - 6, radius - 6);
            }

            if (id == 0) {
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth("출발");
                int textHeight = fm.getAscent();
                g.drawString("출발", p.x - textWidth / 2, p.y + textHeight / 2); // locating "출발" text in the centre of the starting node
            }
            else {
                FontMetrics fm = g.getFontMetrics();
                String label = String.valueOf(id);
                int textWidth = fm.stringWidth(label);
                int textHeight = fm.getAscent();
                g.drawString(label, p.x - textWidth / 2, p.y + textHeight / 2);
            }
        }
    }

    // 노드 간의 연결을 그리는 function
    protected void drawLines(Graphics g) {
        g.setColor(Color.BLACK);

        List<int[]>[] allPaths = new List[]{outerPath, innerPath}; // outerPath, innerPath를 따로 for loop를 사용하지 않고 같이 그리기 위해 allPaths 배열로 묶음
        for (List<int[]> path : allPaths) {
            for (int[] pair : path) {
                Point p1 = nodePositions.get(pair[0]);
                Point p2 = nodePositions.get(pair[1]);
                if (p1 != null && p2 != null) {
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }
    }



    // 그리는 function을 override하여 JPanel의 기본 그리기 기능을 사용함.
    // 위치 조정은 하위 class에서 수행 (drawLines, drawNodes function 호출)
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawLines(g);
        drawNodes(g);
    }
}
