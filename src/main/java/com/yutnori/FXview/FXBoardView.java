package com.yutnori.FXview;

import com.yutnori.viewInterface.BoardViewInterface;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import com.yutnori.model.*;

import java.awt.Point;
import java.util.*;

public class FXBoardView extends Pane implements BoardViewInterface { // JPanel 대신 JavaFX의 Pane을 사용
    private final Canvas canvas;
    private final Board board;

    public FXBoardView(Board board) {
        this.board = board;
        this.canvas = new Canvas(800, 800); // BoardView의 크기에 대응됨.
        getChildren().add(canvas);
        render(); // BoardView에서 painComponent로 했던 것과 달리, render()로 수동 호출.
    }

    public void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D(); // JavaFX의 GraphicsContext 사용
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawLines(gc);
        drawNodes(gc);
        drawPieces(gc);
    }

    @Override
    public void repaint() {
        render();  // 또는 JavaFX의 적절한 재렌더링 로직
    }

    private void drawLines(GraphicsContext gc) {
        gc.setStroke(Color.BLACK); // setColor에 대응되는 JavaFX의 setStroke 사용

        List<int[]>[] allPaths = new List[]{board.getOuterPath(), board.getInnerPath()};
        for (List<int[]> path : allPaths) {
            for (int[] pair : path) {
                Point p1 = board.getNodePosition(pair[0]);
                Point p2 = board.getNodePosition(pair[1]);
                if (p1 != null && p2 != null) {
                    gc.strokeLine(p1.x, p1.y, p2.x, p2.y); // g.drawLine -> gc.strokeLine으로 대응
                }
            }
        }
    }

    private void drawNodes(GraphicsContext gc) {
        for (Map.Entry<Integer, Point> entry : board.getNodePositions().entrySet()) {
            int id = entry.getKey();
            Point p = entry.getValue();

            boolean isCorner = board.isCorner(id);
            boolean isCentre = board.isCentre(id);
            int radius = isCorner || isCentre ? Board.getCornerRadius() : Board.getRadius();

            gc.setFill(Color.WHITE); // g.setColor(Color.WHITE) -> gc.setFill(Color.WHITE)으로 대응
            gc.fillOval(p.x - radius / 2, p.y - radius / 2, radius, radius);
            gc.setStroke(Color.BLACK); // g.setColor(Color.BLACK) -> gc.setStroke(Color.BLACK)으로 대응
            gc.strokeOval(p.x - radius / 2, p.y - radius / 2, radius, radius);

            if (isCorner || isCentre) {
                gc.strokeOval(p.x - radius / 2 + 3, p.y - radius / 2 + 3, radius - 6, radius - 6);
            }

            if (id == 0) { // Swing에서는 FontMetrics와 g.drawString을 사용하는 것과 달리, JavaFx에서는 직접적으로 Font를 설정하고 위치 조정
                gc.setFont(Font.font("Arial", 12));
                gc.setFill(Color.BLACK);
                gc.fillText("출발", p.x - 12, p.y + 5);
            }

        }
    }

    private void drawPieces(GraphicsContext gc) {
        Set<Piece> renderedLeaders = new HashSet<>();

        for (Cell cell : board.getCells()) {
            int id = cell.getId();

            // SquareBoard 중앙 셀 병합 처리
            List<Piece> piecesToDraw = new ArrayList<>(cell.getStackedPieces());
            if (id == 22 && board instanceof SquareBoard) {
                Cell altCenter = board.getCells().get(27);
                if (altCenter != null) {
                    piecesToDraw.addAll(altCenter.getStackedPieces());
                }
            }

            // 위치 결정
            Point pos = (id == 27 && board instanceof SquareBoard)
                    ? board.getNodePosition(22)
                    : board.getNodePosition(id);

            if (pos == null) continue;

            for (Piece piece : piecesToDraw) {
                if (!piece.isOnBoard()) continue;

                Piece leader = piece.getGroupLeaderOrSelf();
                if (renderedLeaders.contains(leader)) continue;

                int offset = 0;
                for (Piece p : leader.getAllGroupedPieces()) {
                    java.awt.Color awtColor = p.getColor(); // piece.getColor() returns java.awt.Color
                    javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb( // convert java.awt.Color to javafx.scene.paint.Color
                            awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());

                    gc.setFill(fxColor); // set the fill color in javafx.scene.paint.Color (differ from java.awt.Color)
                    gc.fillOval(pos.x - 10 + offset, pos.y - 10, 20, 20);

                    gc.setFill(Color.BLACK);
                    gc.setFont(Font.font("Arial", 12));
                    gc.fillText(String.valueOf(p.getId() + 1), pos.x - 5 + offset, pos.y + 5);

                    offset += 5;
                }

                if (leader.getAllGroupedPieces().size() > 1) {
                    gc.setFill(Color.BLACK);
                    gc.fillText("x" + leader.getAllGroupedPieces().size(), pos.x + 10, pos.y - 10);
                }

                renderedLeaders.add(leader);
            }
        }
    }
}
