package com.yutnori.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Player {
    private static int nextId = 1; // 자동 증가 ID용 static 변수

    private String name;
    private List<Piece> pieces;
    private int score;
    private int id;

    public Player(String name) {
        this.name = name;
        this.pieces = new ArrayList<>();
        this.score = 0;
        this.id = nextId++; // ID 자동 할당
    }

    public void addScore(int points) {
        score += points;
    }

    public List<Piece> getActivePieces() {
        return pieces.stream()
                .filter(p -> p.getPosition() != null)  // 아직 완주하지 않은 말
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public void addPiece(Piece piece) {
        pieces.add(piece);
    }

    public int getId() {
        return id;
    }

    public static void resetCounter() {
        nextId = 1;
    }

}
