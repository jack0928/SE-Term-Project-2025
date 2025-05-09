package com.yutnori.view;

import com.yutnori.controller.*;
import com.yutnori.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class GameView {
    private final JFrame frame;
    private final JPanel mainPanel;
    private final JLabel turnLabel;
    private final BoardView boardView;
    private final PlayerStatusView statusView;
    private final YutResultView resultView;

    private final Yut yut;
    private final YutController yutController;

    public GameView(Board board, List<Player> players) {
        // === 모델 연결 ===
        this.yut = new Yut();
        this.resultView = new YutResultView();
        this.yutController = new YutController(yut, resultView);
        this.boardView = new BoardView(board);
        this.statusView = new PlayerStatusView();
        this.turnLabel = new JLabel();
        this.turnLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // === 프레임 및 패널 구성 ===
        this.frame = new JFrame("Yut Game");
        this.mainPanel = new JPanel(new BorderLayout());

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(turnLabel);
        rightPanel.add(resultView);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(statusView);
        statusView.render(players);

        mainPanel.add(boardView, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public JFrame getFrame() {
        return this.frame;
    }

    public BoardView getBoardView() {
        return this.boardView;
    }

    public PlayerStatusView getStatusView() {
        return this.statusView;
    }

    public void render(Player currentPlayer, List<Player> allPlayers, Board board) {
        turnLabel.setText("현재 턴: " + currentPlayer.getName());
        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        turnLabel.setFont(new Font("NanumGothic", Font.BOLD, 25));
        boardView.repaint(); // 말 이동 등 상태 반영

        statusView.updatePlayers(allPlayers);
    }

    public void setThrowButtonListener(ActionListener listener) {
        resultView.getThrowRandomButton().addActionListener(listener);
    }

    public void setSelectButtonListener(ActionListener listener) {
        resultView.getSelectYutButton().addActionListener(listener);
    }

    public int throwYut(boolean isRandom) {
        yutController.performThrow(isRandom);
        return yut.getLastResult();
    }

    public int promptStepSelection(List<Integer> steps) {
        if (steps.size() == 1) return steps.get(0);

        String[] stepOptions = steps.stream()
                .map(YutResultView::getResultText)
                .toArray(String[]::new);

        int selected = JOptionPane.showOptionDialog(
                null,
                "사용할 윷 결과를 선택하세요:",
                "Step 선택",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                stepOptions,
                stepOptions[0]
        );

        return (selected == JOptionPane.CLOSED_OPTION) ? -999 : steps.get(selected);
    }

    public Piece promptPieceSelection(Player player, int step) {
        List<Piece> movable = player.getPieces().stream()
                .filter(p -> !p.isFinished())
                .toList();

        if (movable.isEmpty()) {
            JOptionPane.showMessageDialog(null, "이동 가능한 말이 없습니다.");
            return null;
        }

        if (movable.size() == 1 || movable.stream().noneMatch(Piece::isOnBoard)) {
            return movable.get(0); // 자동 선택
        }

        String[] options = new String[movable.size()];
        for (int i = 0; i < movable.size(); i++) {
            options[i] = "말 " + (movable.get(i).getId() + 1);
        }

        int selected = JOptionPane.showOptionDialog(
                null,
                "이동할 말을 선택하세요 (" + step + "칸):",
                "말 선택",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        return (selected == JOptionPane.CLOSED_OPTION) ? null : movable.get(selected);
    }


    public void promptPieceMove(
            Player player,
            List<Integer> steps,
            Board board,
            Runnable onTurnEnd
    ) {
        for (int step : steps) {
            List<Piece> movable = player.getPieces().stream()
                    .filter(p -> !p.isFinished())
                    .toList();

            if (movable.isEmpty()) {
                JOptionPane.showMessageDialog(null, "이동 가능한 말이 없습니다.");
                onTurnEnd.run();
                return;
            }

            String[] options = movable.stream()
                    .map(p -> "말 " + p.getId())
                    .toArray(String[]::new);

            int selected = JOptionPane.showOptionDialog(
                    null,
                    "이동할 말을 선택하세요 (" + step + "칸):",
                    "말 선택",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (selected >= 0 && selected < movable.size()) {
                Piece selectedPiece = movable.get(selected);
                PieceMoveController moveController = new PieceMoveController(board); // ✅
                moveController.movePiece(selectedPiece, step);
                boardView.repaint();
            }
        }

        onTurnEnd.run();
    }

}
