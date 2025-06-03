package com.yutnori.viewInterface;

import com.yutnori.model.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;

public interface GameViewInterface {
    BoardViewInterface getBoardView();
    PlayerStatusViewInterface getStatusView();

    void render(Player currentPlayer, List<Player> allPlayers);

    void setThrowRandomButtonListener(ActionListener listener);
    void setSelectButtonListener(ActionListener listener);

    int throwYut(boolean isRandom);
    int promptStepSelection(Player player, List<Integer> steps);
    Piece promptPieceSelection(Player player, int step);

    boolean askRestart(String winnerName);
    void showMessage(String message);
    void dispose();

    Board getBoard();
    List<Player> getPlayers();



}
