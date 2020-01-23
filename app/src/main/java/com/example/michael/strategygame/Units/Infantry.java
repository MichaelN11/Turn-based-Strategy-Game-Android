package com.example.michael.strategygame.Units;

import com.example.michael.strategygame.Unit;

public class Infantry extends Unit {
    private static final String NAME = "Infantry";
    private static final UnitSprite SPRITE = UnitSprite.INFANTRY;
    private static final MoveType MOVE_TYPE = MoveType.FOOT;
    private static final DamageType DAMAGE_TYPE = DamageType.GUN;
    private static final int MOVEMENT = 3;
    private static final int ATTACK_POWER = 1;
    private static final int DEFENSE = 1;
    private static final int VALUE = 1;
    private static final boolean INDIRECT = false;

    public Infantry(int team, int row, int col) {
        super(NAME, SPRITE, MOVE_TYPE, DAMAGE_TYPE, MOVEMENT, ATTACK_POWER, DEFENSE, VALUE, 1, 1, INDIRECT, team, row, col);
    }
}
