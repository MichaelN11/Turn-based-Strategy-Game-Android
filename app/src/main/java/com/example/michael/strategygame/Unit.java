package com.example.michael.strategygame;

public abstract class Unit {

    public enum UnitSprite {
        NONE,
        INFANTRY,
        TANK,
        ARTILLERY,
        RPG
    }

    public enum MoveType {
        FOOT,
        TIRE,
        TREAD
    }

    public enum DamageType {
        GUN,
        EXPLOSIVE,
        ANTI_VEHICLE
    }

    // How to display the unit
    private UnitSprite sprite;

    // Which team (1-4) the unit belongs to
    private int team;

    private String name;
    private MoveType moveType;
    private DamageType damageType;
    private int maxMovement;
    private int attackPower;
    private int defense;
    private int value;
    private boolean indirect;
    private int minRange;
    private int maxRange;
    private int health = 100;
    private int row, col;
    private int remainingMovement;
    private boolean active = true;
    private boolean dead = false;

    public Unit(String name, UnitSprite sprite, MoveType moveType, DamageType damageType,
                int movement, int attackPower, int defense, int value, int minRange, int maxRange, boolean indirect,
                int team, int row, int col) {
        this.name = name;
        this.sprite = sprite;
        this.moveType = moveType;
        this.damageType = damageType;
        this.maxMovement = movement;
        remainingMovement = movement;
        this.attackPower = attackPower;
        this.defense = defense;
        this.value = value;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.indirect = indirect;
        this.team = team;
        this.row = row;
        this.col = col;
    }

    public void setRowAndCol(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public UnitSprite getSprite() {
        return sprite;
    }

    public int getTeam() {
        return team;
    }

    public String getUnitName() {
        return name;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public int getMaxMovement() {
        return maxMovement;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getDefense() { return defense; }

    public int getMinRange() { return minRange; }

    public int getMaxRange() {
        return maxRange;
    }

    public boolean isIndirect() {
        return indirect;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getRemainingMovement() {
        return remainingMovement;
    }

    public void setRemainingMovement(int remainingMovement) {
        this.remainingMovement = remainingMovement;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public int getValue() {
        return value;
    }
}
