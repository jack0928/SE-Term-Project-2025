package com.yutnori.view;

import com.yutnori.model.Yut;
import com.yutnori.viewInterface.YutResultViewInterface;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

// 윷의 결과를 시각화하는 클래스
public class YutResultView extends JPanel implements YutResultViewInterface {

    private Yut currentYut;
    private int dashIndex = -1;
    private final JComboBox<String> selectYutDropdown = new JComboBox<>(
            new String[]{"빽도", "도", "개", "걸", "윷", "모"});
    private final JButton selectYutButton = new JButton("선택 윷 던지기");
    private final JButton throwRandomButton = new JButton("랜덤 윷 던지기");

    @Override
    public void setYutResult(Yut currentYut) {
        this.currentYut = currentYut;
        repaint();
    }

    public YutResultView() {
        setLayout(new BorderLayout());

        // 중앙 그림 패널
        JPanel centerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawYutResult(g);
            }
        };
        centerPanel.setPreferredSize(new Dimension(400, 200));
        add(centerPanel, BorderLayout.CENTER);

        // 컨트롤 패널
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(selectYutDropdown);
        controlPanel.add(selectYutButton);
        controlPanel.add(throwRandomButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    public JButton getSelectYutButton() {
        return selectYutButton;
    }

    public JButton getThrowRandomButton() {
        return throwRandomButton;
    }


    // 드롭다운에서 선택된 윷 결과를 정수 값으로 반환
    @Override
    public int getSelectedYutValue() {
        String selected = (String) selectYutDropdown.getSelectedItem();
        return switch (selected) {
            case "빽도" -> -1;
            case "도" -> 1;
            case "개" -> 2;
            case "걸" -> 3;
            case "윷" -> 4;
            case "모" -> 5;
            default -> throw new IllegalArgumentException("알 수 없는 윷 결과: " + selected);
        };
    }

    private void drawYutResult(Graphics g) {
        if (currentYut == null || currentYut.isEmpty()) return;

        int result;
        try {
            result = currentYut.getLastResult();
        } catch (IllegalStateException e) {
            return;
        }

        List<Integer> faces = convertToFaces(result);
        determineDashIndex(result, faces);

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

        int startX = 50;
        int y = 50;
        int width = 50;
        int height = 80;
        int spacing = 20;

        for (int i = 0; i < 4; i++) {
            int x = startX + i * (width + spacing);

            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(x, y, width, height);
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, width, height);

            if (faces.get(i) == 1) {
                drawThreeXs(g2, x, y, width, height);
            } else if (i == dashIndex) {
                g2.drawLine(x + 10, y + height / 2, x + width - 10, y + height / 2);
            }
        }

        g2.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        g2.drawString("결과: " + getResultText(result), startX, y + height + 40);
    }

    private void determineDashIndex(int result, List<Integer> faces) {
        dashIndex = -1;
        List<Integer> zeroIndices = new ArrayList<>();
        for (int i = 0; i < faces.size(); i++) {
            if (faces.get(i) == 0) zeroIndices.add(i);
        }

        if (!zeroIndices.isEmpty()) {
            if (result == -1) {
                dashIndex = zeroIndices.get(new Random().nextInt(zeroIndices.size()));
            } else if (result == 2 || result == 3 || result == 4) {
                if (new Random().nextBoolean()) {
                    dashIndex = zeroIndices.get(new Random().nextInt(zeroIndices.size()));
                }
            }
        }
    }

    private void drawThreeXs(Graphics2D g2, int x, int y, int width, int height) {
        int size = 8;
        drawX(g2, x + width / 2, y + 20, size);
        drawX(g2, x + width / 2, y + height / 2, size);
        drawX(g2, x + width / 2, y + height - 20, size);
    }

    private void drawX(Graphics2D g2, int cx, int cy, int size) {
        g2.drawLine(cx - size, cy - size, cx + size, cy + size);
        g2.drawLine(cx - size, cy + size, cx + size, cy - size);
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
}
