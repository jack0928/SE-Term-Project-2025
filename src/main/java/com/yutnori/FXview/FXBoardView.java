package com.yutnori.FXview;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import com.yutnori.model.*;

import java.awt.Point;
import java.util.*;

public class FXBoardView extends Pane {
    private final Canvas canvas;
    private final Board board;

    public FXBoardView(Board board) {
        this.board = board;
        this.canvas = new Canvas(800, 800); // layout size
        getChildren().add(canvas);
        render();
    }

    public void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawLines(gc);
        drawNodes(gc);
        drawPieces(gc);
    }

    private void drawLines(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);

        List<int[]>[] allPaths = new List[]{board.getOuterPath(), board.getInnerPath()};
        for (List<int[]> path : allPaths) {
            for (int[] pair : path) {
                Point p1 = board.getNodePosition(pair[0]);
                Point p2 = board.getNodePosition(pair[1]);
                if (p1 != null && p2 != null) {
                    gc.strokeLine(p1.x, p1.y, p2.x, p2.y);
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

            gc.setFill(Color.WHITE);
            gc.fillOval(p.x - radius / 2, p.y - radius / 2, radius, radius);
            gc.setStroke(Color.BLACK);
            gc.strokeOval(p.x - radius / 2, p.y - radius / 2, radius, radius);

            if (isCorner || isCentre) {
                gc.strokeOval(p.x - radius / 2 + 3, p.y - radius / 2 + 3, radius - 6, radius - 6);
            }

            if (id == 0) {
                gc.setFont(Font.font("Arial", 12));
                gc.setFill(Color.BLACK);
                gc.fillText("출발", p.x - 12, p.y + 5);
            }

            else { // TODO: 현재 이 else문은 노드의 ID를 표시함. 디버깅 후 이 else문을 꼭 지워야 함
                javafx.scene.text.Text text = new javafx.scene.text.Text(String.valueOf(id));
                text.setFont(Font.font("Arial", 12));
                double textWidth = text.getLayoutBounds().getWidth();
                double textHeight = text.getLayoutBounds().getHeight();

                gc.setFont(text.getFont());
                gc.setFill(Color.GRAY);
                gc.fillText(String.valueOf(id), (float)(p.x - textWidth / 2), (float)(p.y + textHeight / 4));
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
                    java.awt.Color awtColor = p.getColor();
                    javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(
                            awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());

                    gc.setFill(fxColor);
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
