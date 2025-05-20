package com.yutnori.view;

import com.yutnori.model.*;
import com.yutnori.viewInterface.BoardViewInterface;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BoardView extends JPanel implements BoardViewInterface {
    private Board board;
    public BoardView(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(800, 800)); // required for layout!
    }

    // 노드의 위치를 그리는 function
    protected void drawNodes(Graphics g) {
        for (Map.Entry<Integer, Point> entry : board.getNodePositions().entrySet()) {
            int id = entry.getKey();
            Point p = entry.getValue();

            boolean corner = board.isCorner(id);
            boolean centre = board.isCentre(id);
            int radius = corner || centre? Board.getCornerRadius() : Board.getRadius();

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

        }
    }

    // 노드 간의 연결을 그리는 function
    protected void drawLines(Graphics g) {
        g.setColor(Color.BLACK);

        java.util.List<int[]>[] allPaths = new java.util.List[]{board.getOuterPath(), board.getInnerPath()}; // outerPath, innerPath를 따로 for loop를 사용하지 않고 같이 그리기 위해 allPaths 배열로 묶음
        for (List<int[]> path : allPaths) {
            for (int[] pair : path) {
                Point p1 = board.getNodePositions().get(pair[0]);
                Point p2 = board.getNodePositions().get(pair[1]);
                if (p1 != null && p2 != null) {
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }
    }

    protected void drawPieces(Graphics g) {
        Set<Piece> rendered = new HashSet<>();

        for (Cell cell : board.getCells()) {
            int id = cell.getId();

            // 중앙 셀 병합 처리 (SquareBoard)
            List<Piece> piecesToDraw = new ArrayList<>(cell.getStackedPieces());
            if (id == 22 && board instanceof SquareBoard) {
                Cell altCenter = board.getCells().get(27);
                if (altCenter != null) {
                    piecesToDraw.addAll(altCenter.getStackedPieces());
                }
            }

            // 중앙 좌표 통일
            Point pos = (id == 27 && board instanceof SquareBoard)
                    ? board.getNodePosition(22)
                    : board.getNodePosition(id);

            if (pos == null) continue;

            // 그룹 리더 기준으로 하나만 처리하되, 업힌 말들도 순회하면서 모두 겹쳐 그림
            Set<Piece> renderedLeaders = new HashSet<>();
            for (Piece piece : piecesToDraw) {
                if (!piece.isOnBoard()) continue;

                Piece leader = piece.getGroupLeaderOrSelf();
                if (renderedLeaders.contains(leader)) continue; // 같은 그룹은 1번만 처리

                int offset = 0;
                for (Piece p : leader.getAllGroupedPieces()) {
                    g.setColor(p.getColor());
                    g.fillOval(pos.x - 10 + offset, pos.y - 10, 20, 20);

                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Arial", Font.BOLD, 12));
                    String label = String.valueOf(p.getId() + 1);
                    g.drawString(label, pos.x - 5 + offset, pos.y + 5);

                    offset += 5;
                }

                // 그룹 수 텍스트 (xN)
                if (leader.getAllGroupedPieces().size() > 1) {
                    g.drawString("x" + leader.getAllGroupedPieces().size(),
                            pos.x + 10 + offset, pos.y - 10);
                }

                renderedLeaders.add(leader);
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
        drawPieces(g);
    }
    private void drawCarriedCount(Graphics g, Cell cell, Point pos) {
        int count = 0;
        for (int i=0; i<cell.getStackedPieces().size(); i++) {
            count++; // 말이 쌓인 갯수를 셈
        }
        if (count > 1) { // 말이 한 cell 위에 업혀서 2개 이상 쌓였을 때
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("x" + count, pos.x + 10, pos.y - 10);
        }
    }

    @Override
    public void repaint() {
        super.repaint();
    }
}
