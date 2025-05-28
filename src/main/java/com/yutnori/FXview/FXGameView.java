package com.yutnori.FXview;

import com.yutnori.controller.YutController;
import com.yutnori.model.*;
import com.yutnori.view.YutResultView;
import com.yutnori.viewInterface.GameViewInterface;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class FXGameView implements GameViewInterface {
    private final Stage stage;
    private final Label turnLabel = new Label();

    private FXBoardView boardView;
    private FXPlayerStatusView statusView;
    private FXYutResultView resultView;

    private Yut yut;
    private YutController yutController;

    private Board board;
    private List<Player> players;

    public FXGameView(Stage stage) {
        this.stage = stage;
        promptAndInitialize();
        setupUI();
        stage.setTitle("Yutnori Game");
        stage.show();
    }

    private void promptAndInitialize() {
        ChoiceDialog<String> boardDialog = new ChoiceDialog<>("Square", "Square", "Pentagon", "Hexagon");
        boardDialog.setTitle("보드 선택");
        boardDialog.setHeaderText("보드 형태를 선택하세요:");
        String selectedBoard = boardDialog.showAndWait().orElse(null);
        if (selectedBoard == null) System.exit(0);

        board = switch (selectedBoard) {
            case "Pentagon" -> new PentagonBoard();
            case "Hexagon" -> new HexagonBoard();
            default -> new SquareBoard();
        };

        ChoiceDialog<Integer> playerDialog = new ChoiceDialog<>(2, 2, 3, 4);
        playerDialog.setTitle("플레이어 수 선택");
        playerDialog.setHeaderText("플레이어 수를 선택하세요:");
        int playerCount = playerDialog.showAndWait().orElse(2);

        ChoiceDialog<Integer> pieceDialog = new ChoiceDialog<>(2, 2, 3, 4, 5);
        pieceDialog.setTitle("말 개수 선택");
        pieceDialog.setHeaderText("플레이어당 말 개수를 선택하세요:");
        int pieceCount = pieceDialog.showAndWait().orElse(2);

        players = new ArrayList<>();
        for (int i = 1; i <= playerCount; i++) {
            Player player = new Player("Player" + i);
            for (int j = 0; j < pieceCount; j++) {
                player.addPiece(new Piece(j, player));
            }
            players.add(player);
        }
    }

    private void setupUI() {
        this.yut = new Yut();
        this.resultView = new FXYutResultView();
        this.yutController = new YutController(yut, resultView);
        this.boardView = new FXBoardView(board);
        this.statusView = new FXPlayerStatusView(players);

        VBox rightPanel = new VBox(10, turnLabel, resultView, statusView);
        HBox mainPanel = new HBox(20, boardView, rightPanel);

        turnLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Scene scene = new Scene(mainPanel, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Yut Game (JavaFX)");
        stage.show();
    }


    @Override
    public void render(Player current, List<Player> all) {
        turnLabel.setText("현재 턴: " + current.getName());

        // 플레이어 색상 따라 텍스트 색상 설정
        if (!current.getPieces().isEmpty()) {
            java.awt.Color awtColor = current.getPieces().get(0).getColor();
            javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(
                    awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue()
            );
            turnLabel.setTextFill(fxColor);
        } else {
            turnLabel.setTextFill(javafx.scene.paint.Color.BLACK);
        }

        boardView.render();
        statusView.updateFinishedPieces(all);
    }

    @Override public void setThrowRandomButtonListener(java.awt.event.ActionListener listener) {
        resultView.setThrowRandomAction(() -> listener.actionPerformed(null));
    }

    @Override public void setSelectButtonListener(java.awt.event.ActionListener listener) {
        resultView.setSelectYutAction(() -> listener.actionPerformed(null));
    }

    @Override public int throwYut(boolean isRandom) {
        yutController.performThrow(isRandom);
        return yut.getLastResult();
    }

    @Override
    public int promptStepSelection(Player player, List<Integer> steps) {
        // 말이 하나도 보드 위에 없으면 빽도는 무효 → 필터링
        List<Integer> filteredSteps = steps.stream()
                .filter(step -> !(step == -1 && player.getPieces().stream().noneMatch(Piece::isOnBoard)))
                .toList();

        // ✅ 자동 선택 가능한 경우
        if (filteredSteps.isEmpty()) return steps.get(0);           // 모두 invalid → skip
        if (filteredSteps.size() == 1) return filteredSteps.get(0); // 하나뿐이면 자동 선택

        // ✅ 2개 이상 → 사용자에게 선택 받기
        List<String> options = filteredSteps.stream()
                .map(YutResultView::getResultText)
                .toList();

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Step 선택");
        dialog.setHeaderText("사용할 윷 결과를 선택하세요:");
        String selected = dialog.showAndWait().orElse(options.get(0));

        int selectedIndex = options.indexOf(selected);
        return filteredSteps.get(selectedIndex);
    }

    @Override public Piece promptPieceSelection(Player p, int step) {
        List<Piece> movable = p.getPieces().stream()
                .filter(pi -> !pi.isFinished())
                .filter(pi -> step != -1 || pi.isOnBoard())
                .filter(pi -> pi.getGroupLeader() == null)
                .toList();

        if (movable.isEmpty()) {
            showMessage(step == -1
                    ? "보드 위에 말이 없어 빽도 이동이 불가능합니다."
                    : "이동 가능한 말이 없습니다.");
            return null;
        }

        if (movable.size() == 1 || movable.stream().noneMatch(Piece::isOnBoard)) {
            return movable.get(0);
        }

        List<String> labels = new ArrayList<>();
        for (Piece pi : movable) {
            StringBuilder label = new StringBuilder("말 " + (pi.getId() + 1));
            List<Piece> carried = pi.getGroupingPieces();
            if (!carried.isEmpty()) {
                label.append(" (업은 말: ");
                for (int j = 0; j < carried.size(); j++) {
                    label.append(carried.get(j).getId() + 1);
                    if (j < carried.size() - 1) label.append(", ");
                }
                label.append(")");
            }
            labels.add(label.toString());
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(labels.get(0), labels);
        dialog.setTitle("말 선택");
        dialog.setHeaderText("이동할 말을 선택하세요 (" + step + "칸):");
        String selected = dialog.showAndWait().orElse(null);
        if (selected == null) return null;

        int index = labels.indexOf(selected);
        return movable.get(index);
    }

    @Override public void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @Override public boolean askRestart(String winnerName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("게임 종료");
        alert.setHeaderText(winnerName + "님이 승리했습니다! 게임을 다시 시작할까요?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    @Override public void dispose() {
        stage.close();
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public FXBoardView getBoardView() {
        return boardView;
    }

    @Override
    public FXPlayerStatusView getStatusView() {
        return statusView;
    }

    @Override
    public void restartGame(Consumer<Integer> callback) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("UI 선택");
            alert.setHeaderText("어떤 UI로 재시작 하시겠습니까?");
            ButtonType swingBtn = new ButtonType("Swing");
            ButtonType fxBtn = new ButtonType("JavaFX");

            alert.getButtonTypes().setAll(swingBtn, fxBtn);
            Optional<ButtonType> result = alert.showAndWait();

            int selected = (result.isPresent() && result.get() == swingBtn) ? 0 : 1;
            callback.accept(selected);
        });
    }

}
