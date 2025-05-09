package com.yutnori;

import com.yutnori.controller.GameController;
import com.yutnori.model.*;
import com.yutnori.view.GameView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class YutnoriApplication {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            // 보드 선택
            String[] boardOptions = {"Square", "Pentagon", "Hexagon"};
            String selectedBoard = (String) JOptionPane.showInputDialog(
                    null,
                    "보드 형태를 선택하세요:",
                    "보드 선택",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    boardOptions,
                    boardOptions[0]
            );
            if (selectedBoard == null) return;

            Board board = switch (selectedBoard) {
                // case "Square" -> new SquareBoard(); default 케이스로 뺌
                case "Pentagon" -> new PentagonBoard();
                case "Hexagon" -> new HexagonBoard();
                default -> new SquareBoard();
            };

            // 플레이어 수 선택
            Integer[] playerCounts = {2, 3, 4};
            Integer selectedPlayerCount = (Integer) JOptionPane.showInputDialog(
                    null,
                    "플레이어 수를 선택하세요:",
                    "플레이어 수 선택",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    playerCounts,
                    playerCounts[0]
            );
            if (selectedPlayerCount == null) return;

            // 말 갯수 선택
            Integer[] pieceCounts = {2, 3, 4, 5};
            Integer selectedPieceCount = (Integer) JOptionPane.showInputDialog(
                    null,
                    "플레이어당 말 개수를 선택하세요:",
                    "말 개수 선택",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    pieceCounts,
                    pieceCounts[0]
            );
            if (selectedPieceCount == null) return;

            // 플레이어 말 생성
            List<Player> players = new ArrayList<>();
            for (int i = 1; i <= selectedPlayerCount; i++) {
                Player player = new Player("Player" + i);
                for (int j = 0; j < selectedPieceCount; j++) {
                    player.addPiece(new Piece(j, player));
                }
                players.add(player);
            }

            // 게임 시작
            Game game = new Game(players, board, selectedPieceCount);
            GameView view = new GameView(board, players);
            GameController controller = new GameController(game, view);

            controller.startGame();
        });
    }
}
