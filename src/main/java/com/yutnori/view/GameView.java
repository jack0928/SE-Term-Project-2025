package com.yutnori.view;

import com.yutnori.controller.YutController;
import com.yutnori.model.*;
import com.yutnori.viewInterface.GameViewInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GameView implements GameViewInterface {
    private final JFrame frame;
    private final JPanel mainPanel;
    private final JLabel turnLabel;
    private final BoardView boardView;
    private final PlayerStatusView statusView;
    private final YutResultView resultView;

    private final Yut yut;
    private final YutController yutController;

    private Board board;
    private List<Player> players;

    public GameView() {
        promptAndInitialize(); // 내부에서 prompt 처리

        // 모델 생성
        this.yut = new Yut();
        this.resultView = new YutResultView();
        this.yutController = new YutController(yut, resultView);
        this.boardView = new BoardView(board);
        this.statusView = new PlayerStatusView();
        this.turnLabel = new JLabel();
        this.turnLabel.setHorizontalAlignment(SwingConstants.CENTER);


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

    private void promptAndInitialize() {
        // 1. 보드 형태 선택
        String[] boardOptions = {"Square", "Pentagon", "Hexagon"};
        String selectedBoard = (String) JOptionPane.showInputDialog(
                null, "보드 형태를 선택하세요:", "보드 선택",
                JOptionPane.PLAIN_MESSAGE, null, boardOptions, boardOptions[0]);

        if (selectedBoard == null) System.exit(0); // 혹은 예외 처리

        this.board = switch (selectedBoard) {
            case "Pentagon" -> new PentagonBoard();
            case "Hexagon" -> new HexagonBoard();
            default -> new SquareBoard();
        };

        // 2. 플레이어 수 및 말 개수 선택
        Integer[] playerCounts = {2, 3, 4};
        Integer selectedPlayerCount = (Integer) JOptionPane.showInputDialog(
                null, "플레이어 수를 선택하세요:", "플레이어 수 선택",
                JOptionPane.PLAIN_MESSAGE, null, playerCounts, playerCounts[0]);
        if (selectedPlayerCount == null) System.exit(0);

        Integer[] pieceCounts = {2, 3, 4, 5};
        Integer selectedPieceCount = (Integer) JOptionPane.showInputDialog(
                null, "플레이어당 말 개수를 선택하세요:", "말 개수 선택",
                JOptionPane.PLAIN_MESSAGE, null, pieceCounts, pieceCounts[0]);
        if (selectedPieceCount == null) System.exit(0);

        // 플레이어 및 말 생성
        this.players = new ArrayList<>();
        for (int i = 1; i <= selectedPlayerCount; i++) {
            Player player = new Player("Player" + i);
            for (int j = 0; j < selectedPieceCount; j++) {
                player.addPiece(new Piece(j, player));
            }
            players.add(player);
        }
    }

    // getters for board and players if needed externally
    public Board getBoard() { return board; }
    public List<Player> getPlayers() { return players; }


    @Override
    public BoardView getBoardView() { // 보드뷰 반환
        return this.boardView;
    }

    @Override
    public PlayerStatusView getStatusView() { // 플레이어 상태뷰 반환
        return this.statusView;
    }

    @Override
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

    @Override
    public void setThrowRandomButtonListener(ActionListener listener) { // 랜덤 윷 던지기 버튼
        resultView.getThrowRandomButton().addActionListener(listener);
    }

    @Override
    public void setSelectButtonListener(ActionListener listener) { // 선택된 윷 던지기 버튼
        resultView.getSelectYutButton().addActionListener(listener);
    }

    @Override
    public int throwYut(boolean isRandom) {
        yutController.performThrow(isRandom);
        return yut.getLastResult();
    }

    @Override
    public int promptStepSelection(Player player, List<Integer> steps) {
        // 말이 위에 하나도 없을 때에는 빽도가 invalid함. 다른 step 이 있을 시 그것이 먼저 사용되어야 함.
        List<Integer> filteredSteps = steps.stream()
                .filter(step -> !(step == -1 && player.getPieces().stream().noneMatch(Piece::isOnBoard)))
                .toList();

        if (filteredSteps.isEmpty()) return steps.get(0); // 다른스텝이 없으면 invalid하기 때문에 자동으로 턴 스킵
        if (filteredSteps.size() == 1) return filteredSteps.get(0); // 다른 스텝이 하나뿐이면 그걸 사용

        String[] stepOptions = filteredSteps.stream()
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

        if (selected == JOptionPane.CLOSED_OPTION) return filteredSteps.get(0);
        return filteredSteps.get(selected);
    }

    @Override
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

    @Override
    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }

    @Override
    public boolean askRestart(String winnerName) {
        int choice = JOptionPane.showOptionDialog(null,
                winnerName + "님이 승리했습니다!\n게임을 다시 시작하시겠습니까?",
                "게임 종료",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"재시작", "종료"},
                "재시작");
        return choice == JOptionPane.YES_OPTION;
    }

    @Override
    public void dispose() {
        frame.dispose();
    }


}
