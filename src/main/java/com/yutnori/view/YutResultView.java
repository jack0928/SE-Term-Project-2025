package com.yutnori.view;

import com.yutnori.model.Yut;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

// 윷의 결과를 시각화하는 클래스
public class YutResultView extends JPanel {

    private Yut currentYut;

    public void setYutResult(Yut currentYut) {
        this.currentYut = currentYut;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentYut == null || currentYut.isEmpty()) return;

        int result;
        try {
            result = currentYut.getLastResult();
        } catch (IllegalStateException e) {
            return;
        }

        List<Integer> faces = convertToFaces(result);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

        int startX = 50;
        int y = 50;
        int width = 50;
        int height = 80;
        int spacing = 20;

        // ====== '-' 표시 위치 결정 ======
        int dashIndex = -1;
        List<Integer> zeroIndices = new ArrayList<>();
        for (int i = 0; i < faces.size(); i++) {
            if (faces.get(i) == 0) {
                zeroIndices.add(i);
            }
        }

        if (!zeroIndices.isEmpty()) {
            if (result == -1) {
                // 빽도: 반드시 하나 표시
                dashIndex = zeroIndices.get(new Random().nextInt(zeroIndices.size()));
            } else {
                // 도~모: 50% 확률로 하나만 표시
                if (new Random().nextBoolean()) {
                    dashIndex = zeroIndices.get(new Random().nextInt(zeroIndices.size()));
                }
            }
        }

        // ====== 윷 4개 그리기 ======
        for (int i = 0; i < 4; i++) {
            int x = startX + i * (width + spacing);

            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(x, y, width, height);
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, width, height);

            if (faces.get(i) == 1) {
                drawThreeXs(g2, x, y, width, height);
            } else if (i == dashIndex) {
                g2.drawLine(x + 10, y + height / 2, x + width - 10, y + height / 2); // '-'
            }
        }

        // ====== 결과 텍스트 ======
        g2.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        g2.drawString("결과: " + getResultText(result), startX, y + height + 40);
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
        if (result == -1) return faces;

        List<Integer> positions = List.of(0, 1, 2, 3);
        List<Integer> shuffled = new ArrayList<>(positions);
        Collections.shuffle(shuffled);

        for (int i = 0; i < Math.min(result, 4); i++) {
            faces.set(shuffled.get(i), 1); // 무작위 위치에 앞면(X)
        }

        return faces;
    }

    private String getResultText(int result) {
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("윷 결과 시각화");
        YutResultView view = new YutResultView();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 300);
        frame.add(view);
        frame.setVisible(true);


        Yut yutFaces = new Yut();
        yutFaces.throwSelectYut(2);
        view.setYutResult(yutFaces);

    }
}
