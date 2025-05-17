package com.yutnori.FXview;

import com.yutnori.model.Board;
import com.yutnori.model.SquareBoard;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestFXSquareBoardView extends Application {

    @Override
    public void start(Stage stage) {
        Board board = new SquareBoard();

        FXBoardView boardView = new FXBoardView(board);
        Scene scene = new Scene(boardView, 850, 850);

        stage.setScene(scene);
        stage.setTitle("Test FX SquareBoard View");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
