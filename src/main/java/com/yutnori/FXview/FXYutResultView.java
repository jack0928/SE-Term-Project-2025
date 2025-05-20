package com.yutnori.FXview;

import com.yutnori.model.Yut;
import com.yutnori.viewInterface.YutResultViewInterface;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.*;

public class FXYutResultView extends VBox implements YutResultViewInterface {

    private final ComboBox<String> selectYutCombo;
    private final Button selectYutButton;
    private final Button throwRandomButton;
    private final Canvas canvas;

    private Yut currentYut;
    private int dashIndex = -1;

    public FXYutResultView() {
        setSpacing(10);
        setPadding(new Insets(15));
        setStyle("-fx-background-color: #F0F0F0;");

        // 드롭다운, 버튼
        selectYutCombo = new ComboBox<>();
        selectYutCombo.getItems().addAll("빽도", "도", "개", "걸", "윷", "모");
        selectYutCombo.setValue("도");

        selectYutButton = new Button("선택 윷 던지기");
        throwRandomButton = new Button("랜덤 윷 던지기");

        HBox controlPanel = new HBox(10, selectYutCombo, selectYutButton, throwRandomButton);
        controlPanel.setPadding(new Insets(10));

        // 캔버스
        canvas = new Canvas(400, 200);
        drawYutResult();  // 초기 화면

        getChildren().addAll(canvas, controlPanel);
    }

    public Button getSelectYutButton() {
        return selectYutButton;
    }

    public Button getThrowRandomButton() {
        return throwRandomButton;
    }

    @Override
    public int getSelectedYutValue() {
        String selected = selectYutCombo.getValue();
        return switch (selected) {
            case "빽도" -> -1;
            case "도" -> 1;
            case "개" -> 2;
            case "걸" -> 3;
            case "윷" -> 4;
            case "모" -> 5;
            default -> 0;
        };
    }

    @Override
    public void setYutResult(Yut currentYut) {
        this.currentYut = currentYut;
        drawYutResult();
    }

    private void drawYutResult() {
        GraphicsContext g2 = canvas.getGraphicsContext2D();
        g2.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (currentYut == null || currentYut.isEmpty()) return;

        int result;
        try {
            result = currentYut.getLastResult();
        } catch (IllegalStateException e) {
            return;
        }

        List<Integer> faces = convertToFaces(result);
        determineDashIndex(result, faces);

        int startX = 50;
        int y = 30;
        int width = 50;
        int height = 80;
        int spacing = 20;

        for (int i = 0; i < 4; i++) {
            int x = startX + i * (width + spacing);

            g2.setFill(Color.LIGHTGRAY);
            g2.fillRect(x, y, width, height);
            g2.setStroke(Color.BLACK);
            g2.strokeRect(x, y, width, height);

            if (faces.get(i) == 1) {
                drawThreeXs(g2, x + width / 2, y, height);
            } else if (i == dashIndex) {
                g2.strokeLine(x + 10, y + height / 2, x + width - 10, y + height / 2);
            }
        }

        g2.setFont(new Font("맑은 고딕", 18));
        g2.setFill(Color.BLACK);
        g2.fillText("결과: " + getResultText(result), startX, y + height + 40);
    }

    private void drawThreeXs(GraphicsContext g2, int cx, int y, int height) {
        int size = 8;
        drawX(g2, cx, y + 20, size);
        drawX(g2, cx, y + height / 2, size);
        drawX(g2, cx, y + height - 20, size);
    }

    private void drawX(GraphicsContext g2, int cx, int cy, int size) {
        g2.strokeLine(cx - size, cy - size, cx + size, cy + size);
        g2.strokeLine(cx - size, cy + size, cx + size, cy - size);
    }

    private void determineDashIndex(int result, List<Integer> faces) {
        dashIndex = -1;
        List<Integer> zeroIndices = new ArrayList<>();
        for (int i = 0; i < faces.size(); i++) {
            if (faces.get(i) == 0) zeroIndices.add(i);
        }

        if (!zeroIndices.isEmpty()) {
            Random rand = new Random();
            if (result == -1) {
                dashIndex = zeroIndices.get(rand.nextInt(zeroIndices.size()));
            } else if (result == 2 || result == 3 || result == 4) {
                if (rand.nextBoolean()) {
                    dashIndex = zeroIndices.get(rand.nextInt(zeroIndices.size()));
                }
            }
        }
    }

    private List<Integer> convertToFaces(int result) {
        List<Integer> faces = new ArrayList<>(List.of(0, 0, 0, 0));
        int xCount = switch (result) {
            case -1, 1 -> 3;
            case 2 -> 2;
            case 3 -> 1;
            case 5 -> 4;
            default -> 0;
        };
        List<Integer> indices = new ArrayList<>(List.of(0, 1, 2, 3));
        Collections.shuffle(indices);
        for (int i = 0; i < xCount && i < 4; i++) {
            faces.set(indices.get(i), 1);
        }
        return faces;
    }

    public static String getResultText(int result) {
        return switch (result) {
            case -1 -> "빽도";
            case 1 -> "도";
            case 2 -> "개";
            case 3 -> "걸";
            case 4 -> "윷";
            case 5 -> "모";
            default -> "알 수 없음";
        };
    }

    public void setThrowRandomAction(Runnable handler) {
        throwRandomButton.setOnAction(e -> handler.run());
    }

    public void setSelectYutAction(Runnable handler) {
        selectYutButton.setOnAction(e -> handler.run());
    }

}