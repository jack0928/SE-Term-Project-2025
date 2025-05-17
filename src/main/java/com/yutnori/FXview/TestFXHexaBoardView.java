package com.yutnori.FXview;

import com.yutnori.model.Board;
import com.yutnori.model.HexagonBoard;
import com.yutnori.model.SquareBoard;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class TestFXHexaBoardView extends Application {
    @Override
    public void start(Stage stage) {
        Board board = new HexagonBoard();

        FXBoardView boardView = new FXBoardView(board);
        Scene scene = new Scene(boardView, 850, 850);

        stage.setScene(scene);
        stage.setTitle("Test FX HexagonBoard View");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
