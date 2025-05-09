package com.yutnori.view;

import com.yutnori.model.Piece;
import com.yutnori.model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class PlayerStatusView extends JPanel {

    private final List<JLabel> playerLabels = new java.util.ArrayList<>();

    public void render(List<Player> players) { // Player 점수 반환
        removeAll();
        setLayout(new GridLayout(players.size(), 1));

        playerLabels.clear();
        for (Player p : players) {
            JLabel label = new JLabel(p.getName() + " - 점수: " + p.getScore());
            label.setFont(new Font("NanumGothic", Font.PLAIN, 17));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            playerLabels.add(label);
            add(label);
        }

        revalidate();
        repaint();
    }

    public void updateFinishedPieces(List<Player> players) { // 완료된 말(들) 업데이트
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);

            // 완료된 말 수 + 번호들 구하기
            List<String> finishedIds = new ArrayList<>();
            for (Piece piece : p.getPieces()) {
                if (piece.isFinished()) {
                    finishedIds.add("말 " + (piece.getId() + 1));
                }
            }

            String finishedInfo = finishedIds.isEmpty()
                    ? "No finished pieces"
                    : String.join(", ", finishedIds);

            // 점수 표시
            String text = String.format(
                    "%s - 점수: %d | Finished: %s",
                    p.getName(), p.getScore(), finishedInfo
            );

            playerLabels.get(i).setText(text);
        }
    }

}
