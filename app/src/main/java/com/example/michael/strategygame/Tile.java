package com.example.michael.strategygame;

public class Tile {
    private int row, col;
    private Terrain terrain;
    private Unit unit;

    Tile(int row, int col, Terrain terrain, Unit unit) {
        this.row = row;
        this.col = col;
        this.terrain = terrain;
        this.unit = unit;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }
}
