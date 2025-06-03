package com.yutnori.model;

import java.util.ArrayList;
import java.util.List;

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
