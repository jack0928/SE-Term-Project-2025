package com.yutnori;

import com.yutnori.FXview.FXGameView;
import com.yutnori.controller.GameController;
import com.yutnori.viewInterface.GameViewInterface;
import javafx.application.Application;
import javafx.stage.Stage;

public class YutnoriFXApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        GameViewInterface fxView = new FXGameView(primaryStage);
        new GameController(fxView);
    }
}
