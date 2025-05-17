package com.yutnori.FXview;

import com.yutnori.model.Yut;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXYutTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        Yut yut = new Yut();  // 모델 생성
        FXYutResultView view = new FXYutResultView();

        // 버튼 이벤트 연결
        view.getThrowRandomButton().setOnAction(e -> {
            yut.throwRandomYut();
            view.setYutResult(yut);
        });

        view.getSelectYutButton().setOnAction(e -> {
            try {
                int selected = view.getSelectedYutValue();
                yut.throwSelectYut(selected);
                view.setYutResult(yut);
            } catch (IllegalArgumentException ex) {
                System.err.println("선택 오류: " + ex.getMessage());
            }
        });

        Scene scene = new Scene(view, 500, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("FXYutResultView 테스트");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}