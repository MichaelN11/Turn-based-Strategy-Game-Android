package com.example.michael.strategygame.Units;

import com.example.michael.strategygame.Unit;

public class Artillery extends Unit {
    private static final String NAME = "Artillery";
    private static final Unit.UnitSprite SPRITE = UnitSprite.ARTILLERY;
    private static final Unit.MoveType MOVE_TYPE = MoveType.TIRE;
    private static final Unit.DamageType DAMAGE_TYPE = Unit.DamageType.EXPLOSIVE;
    private static final int MOVEMENT = 6;
    private static final int ATTACK_POWER = 4;
    private static final int DEFENSE = 4;
    private static final int VALUE = 3;
    private static final boolean INDIRECT = true;
    private static final int MIN_RANGE = 2;
    private static final int MAX_RANGE = 3;

    public Artillery(int team, int row, int col) {
        super(NAME, SPRITE, MOVE_TYPE, DAMAGE_TYPE, MOVEMENT, ATTACK_POWER, DEFENSE, VALUE, MIN_RANGE, MAX_RANGE, INDIRECT, team, row, col);
    }
}
