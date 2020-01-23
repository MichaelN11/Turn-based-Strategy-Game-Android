package com.example.michael.strategygame;

import java.util.LinkedList;

public class MoveDestination {
    private LinkedList<Tile> path;
    private int movement;
    private Tile tile;
    private Tile attackTile;
    private int attackDamage = 0;

    public MoveDestination(LinkedList<Tile> path, int movement, Tile tile){
        this(path, movement, tile, null, 0);
    }

    public MoveDestination(LinkedList<Tile> path, int movement, Tile tile, Tile attackTile, int attackDamage) {
        this.path = path;
        this.movement = movement;
        this.tile = tile;
        this.attackTile = attackTile;
        this.attackDamage = attackDamage;
    }

    public LinkedList<Tile> getPath() {
        return path;
    }

    public int getMovement() {
        return movement;
    }

    public Tile getTile() {
        return tile;
    }

    public Tile getAttackTile() {
        return attackTile;
    }

    public int getAttackDamage() {
        return attackDamage;
    }
}