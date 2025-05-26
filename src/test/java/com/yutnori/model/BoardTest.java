package com.yutnori.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new MockBoard(); // 테스트용 구현체
        board.initializeNodePositions(); // innerNode 좌표 생성 포함
    }

    @Test
    void testGetAndSetCells() {
        List<Cell> newCells = Arrays.asList(new Cell(0), new Cell(1));
        board.setCells(newCells);
        assertEquals(newCells, board.getCells());
    }

    @Test
    void testGetNodePositions() {
        Map<Integer, Point> map = board.getNodePositions();
        assertNotNull(map);
        assertTrue(map.containsKey(0)); // MockBoard는 기본 노드 추가함
    }

    @Test
    void testGetNodePositionById() {
        Point point = board.getNodePosition(0);
        assertNotNull(point);
    }

    @Test
    void testGetOuterAndInnerPath() {
        assertNotNull(board.getOuterPath());
        assertNotNull(board.getInnerPath());
    }

    @Testㅌㅋ
    void testRadiusConstants() {
        assertEquals(40, Board.getRadius());
        assertEquals(60, Board.getCornerRadius());
    }
}