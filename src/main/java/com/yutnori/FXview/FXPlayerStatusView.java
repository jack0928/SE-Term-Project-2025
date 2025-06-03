package com.yutnori.FXview;

import com.yutnori.model.Piece;
import com.yutnori.model.Player;
import com.yutnori.viewInterface.PlayerStatusViewInterface;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class FXPlayerStatusView extends VBox implements PlayerStatusViewInterface {

    private final List<Label> playerLabels = new ArrayList<>();

    //기본 레이아웃 설정 , PlayerStatusView에서 setLayout()에 해당
    public FXPlayerStatusView(List<Player> players) {
        setSpacing(10);
        setAlignment(Pos.CENTER);
        render(players);
    }

    public void render(List<Player> players) {
        getChildren().clear();
        playerLabels.clear();

        for (Player p : players) {
            Label label = new Label(p.getName() + " - 점수: " + p.getScore());
            label.setFont(new Font("NanumGothic", 17));
            label.setAlignment(Pos.CENTER);
            playerLabels.add(label);
            getChildren().add(label);
        }
    }

    // 내부 구조는 변화가 없음
    @Override
    public void updateFinishedPieces(List<Player> players) {
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);

            List<String> finishedIds = new ArrayList<>();
            for (Piece piece : p.getPieces()) {
                if (piece.isFinished()) {
                    finishedIds.add("말 " + (piece.getId() + 1));
                }
            }

            String finishedInfo = finishedIds.isEmpty()
                    ? "No finished pieces"
                    : String.join(", ", finishedIds);

            String text = String.format(
                    "%s - 점수: %d | Finished: %s",
                    p.getName(), p.getScore(), finishedInfo
            );

            playerLabels.get(i).setText(text);
        }
    }
}