package com.example.michael.strategygame.Units;

import com.example.michael.strategygame.Unit;

public class RPG extends Unit {
    private static final String NAME = "RPG";
    private static final UnitSprite SPRITE = UnitSprite.RPG;
    private static final MoveType MOVE_TYPE = MoveType.FOOT;
    private static final DamageType DAMAGE_TYPE = DamageType.ANTI_VEHICLE;
    private static final int MOVEMENT = 2;
    private static final int ATTACK_POWER = 2;
    private static final int DEFENSE = 2;
    private static final int VALUE = 2;
    private static final boolean INDIRECT = false;

    public RPG(int team, int row, int col) {
        super(NAME, SPRITE, MOVE_TYPE, DAMAGE_TYPE, MOVEMENT, ATTACK_POWER, DEFENSE, VALUE, 1, 1, INDIRECT, team, row, col);
    }
}
