package com.yutnori.view;

import com.yutnori.controller.*;
import com.yutnori.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
        // 모델 연결
        this.yut = new Yut();
        this.resultView = new YutResultView();
        this.yutController = new YutController(yut, resultView);
        this.boardView = new BoardView(board);
        this.statusView = new PlayerStatusView();
        this.turnLabel = new JLabel();
        this.turnLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 프레임 및 패널 구성
        this.frame = new JFrame("Yut Game");
        this.mainPanel = new JPanel(new BorderLayout());

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(turnLabel);
        rightPanel.add(resultView);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(statusView);
        statusView.render(players);

        // 보드뷰 및 상태뷰 추가
        mainPanel.add(boardView, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // 프레임 설정
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public JFrame getFrame() { // 프레임 반환
        return this.frame;
    }

    public BoardView getBoardView() { // 보드뷰 반환
        return this.boardView;
    }

    public PlayerStatusView getStatusView() { // 플레이어 상태뷰 반환
        return this.statusView;
    }

    public void render(Player currentPlayer, List<Player> allPlayers) { // 현재 턴 플레이어 및 전체 플레이어 상태 업데이트 (rendering)
        turnLabel.setText("현재 턴: " + currentPlayer.getName());
        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        turnLabel.setFont(new Font("NanumGothic", Font.BOLD, 25)); // 폰트 변경 및 크기 조정

        if (!currentPlayer.getPieces().isEmpty()) {
            Color playerColor = currentPlayer.getPieces().get(0).getColor(); // 플레이어의 말 색상으로 색깔을 설정.
            turnLabel.setForeground(playerColor);
        }
        else {
            turnLabel.setForeground(Color.BLACK);
        }

        boardView.repaint(); // 말 이동 등 상태 반영

        statusView.updateFinishedPieces(allPlayers);
    }

    public void setThrowRandomButtonListener(ActionListener listener) { // 랜덤 윷 던지기 버튼
        resultView.getThrowRandomButton().addActionListener(listener);
    }

    public void setSelectButtonListener(ActionListener listener) { // 선택된 윷 던지기 버튼
        resultView.getSelectYutButton().addActionListener(listener);
    }

    public int throwYut(boolean isRandom) {
        yutController.performThrow(isRandom);
        return yut.getLastResult();
    }

    public int promptStepSelection(List<Integer> steps) { // 이동할 칸 수 선택
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

        if (selected == JOptionPane.CLOSED_OPTION) return steps.get(0); // fallback
        return steps.get(selected);// 선택된 step 반환
    }

    public Piece promptPieceSelection(Player player, int step) { // 이동할 말 선택
        List<Piece> movable = player.getPieces().stream()
                .filter(p -> !p.isFinished())
                .filter(p -> step != -1 || p.isOnBoard())
                .filter(p -> p.getGroupLeader() == null)
                .toList();


        if (movable.isEmpty()) {
            JOptionPane.showMessageDialog(null, (step == -1 ? "보드 위에 말이 없어 빽도 이동이 불가능합니다." : "이동 가능한 말이 없습니다."));
            return null;
        }

        if (movable.size() == 1 || movable.stream().noneMatch(Piece::isOnBoard)) {
            return movable.get(0); // 자동 선택 (이동 가능한 말이 하나뿐인 경우)
        }

        String[] options = new String[movable.size()];
        for (int i = 0; i < movable.size(); i++) {
            Piece p = movable.get(i);
            StringBuilder label = new StringBuilder("말 " + (p.getId() + 1));

            // 업고 있는 말들 정보 추가
            List<Piece> carried = p.getGroupingPieces();
            if (carried != null && !carried.isEmpty()) {
                label.append(" (업은 말: ");
                for (int j = 0; j < carried.size(); j++) {
                    label.append(carried.get(j).getId() + 1);  // 말 번호 1부터
                    if (j != carried.size() - 1) label.append(", ");
                }
                label.append(")");
            }
            options[i] = label.toString();
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

}
