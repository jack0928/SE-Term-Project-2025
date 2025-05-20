package com.yutnori.FXview;

import com.yutnori.model.Piece;
import com.yutnori.model.Player;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class TestFXPlayerStatusView extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 테스트용 플레이어 생성
        Player player1 = new Player("Alice");
        Player player2 = new Player("Bob");

        // 각 플레이어 말 초기화
        for (int i = 0; i < 4; i++) {
            player1.addPiece(new Piece(i,player1));
            player2.addPiece(new Piece(i,player2));
        }

        // 점수 설정
        player1.addScore(3);
        player2.addScore(5);

        List<Player> players = Arrays.asList(player1, player2);

        // 뷰 생성 및 초기 렌더링
        FXPlayerStatusView statusView = new FXPlayerStatusView(players);
        statusView.render(players);

        // 씬 설정
        Scene scene = new Scene(statusView, 400, 300);
        primaryStage.setTitle("FXPlayerStatusView 테스트");
        primaryStage.setScene(scene);
        primaryStage.show();

        // 플레이어1 말 2개 완료 처리
        player1.getPieces().get(1).setFinished(true);
        player1.getPieces().get(3).setFinished(true);
        // JavaFX UI 스레드에서 갱신
        javafx.application.Platform.runLater(() -> {
            statusView.updateFinishedPieces(players);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}