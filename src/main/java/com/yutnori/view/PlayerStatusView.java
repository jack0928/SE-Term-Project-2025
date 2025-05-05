package com.yutnori.view;

import com.yutnori.model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class PlayerStatusView extends JPanel {

    private final List<JLabel> playerLabels = new java.util.ArrayList<>();

    public void render(List<Player> players) {
        removeAll();
        setLayout(new GridLayout(players.size(), 1));

        playerLabels.clear();
        for (Player p : players) {
            JLabel label = new JLabel(p.getName() + " - 점수: " + p.getScore());
            playerLabels.add(label);
            add(label);
        }

        revalidate();
        repaint();
    }

    public void updatePlayers(List<Player> players) {
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            playerLabels.get(i).setText(p.getName() + " - 점수: " + p.getScore());
        }
    }

}
