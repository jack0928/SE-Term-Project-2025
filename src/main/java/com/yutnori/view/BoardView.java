package com.yutnori.view;

import com.yutnori.model.Board;
import com.yutnori.model.Cell;
import com.yutnori.model.Piece;
import com.yutnori.model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class BoardView extends JPanel{
    private Board board;
    public BoardView(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(800, 800)); // required for layout!
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Cell cell : board.getCells()) {
            Point pos = board.getNodePosition(cell.getId());
            if (pos == null) continue;

            // ✅ 코너 셀: double circle
            if (board.isCorner(cell.getId())) {
                g.setColor(Color.BLACK);
                g.drawOval(pos.x - 20, pos.y - 20, 40, 40); // 바깥 원
            }

            // ✅ 일반 셀
            g.setColor(Color.BLACK);
            g.drawOval(pos.x - 15, pos.y - 15, 30, 30);

            // ✅ 센터 or 출발 텍스트
            if (board.isCentre(cell.getId()) || cell.getId() == 0) {
                g.setColor(Color.BLACK);
                g.drawString("출발", pos.x - 15, pos.y - 20);
            }

            // ✅ 말 그리기
            int offset = 0;
            for (Piece piece : cell.getStackedPieces()) {
                g.setColor(piece.getColor());
                g.fillOval(pos.x - 10 + offset, pos.y - 10, 20, 20);
                offset += 5;
            }
        }
    }



}
