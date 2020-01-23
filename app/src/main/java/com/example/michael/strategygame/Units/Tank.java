package com.example.michael.strategygame.Units;

import com.example.michael.strategygame.Unit;

public class Tank extends Unit {
    private static final String NAME = "Tank";
    private static final UnitSprite SPRITE = UnitSprite.TANK;
    private static final MoveType MOVE_TYPE = MoveType.TREAD;
    private static final DamageType DAMAGE_TYPE = DamageType.EXPLOSIVE;
    private static final int MOVEMENT = 6;
    private static final int ATTACK_POWER = 3;
    private static final int DEFENSE = 5;
    private static final int VALUE = 4;
    private static final boolean INDIRECT = false;

    public Tank(int team, int row, int col) {
        super(NAME, SPRITE, MOVE_TYPE, DAMAGE_TYPE, MOVEMENT, ATTACK_POWER, DEFENSE, VALUE, 1, 1, INDIRECT, team, row, col);
    }
}
