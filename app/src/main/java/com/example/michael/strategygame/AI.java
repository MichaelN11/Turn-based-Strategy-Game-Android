package com.example.michael.strategygame;


import android.graphics.Point;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/*

 */
public class AI implements Runnable {
    private Game game;
    private GameView gameView;
    private GameController gameController;
    private Level level;
    private int team;
    private Unit selectedUnit;
    private TileCheck[][] selectedUnitMoves;
    private Unit target;
    private boolean unitMoved = false;
    private TileCheck[][] attackRange;
    private Handler handler;
    private Iterator<Unit> unitIterator;
    private boolean startTurn = true;

    public AI(Game game, GameView gameView, GameController gameController, Level level, int team) {
        this.game = game;
        this.gameView = gameView;
        this.gameController = gameController;
        this.level = level;
        this.team = team;
        handler = new Handler();
        unitIterator = level.getUnitList().iterator();
    }

    @Override
    public void run() {
        // Uses iterator to iterate through units and move them
        if (startTurn) {
            unitIterator = level.getUnitList().iterator();
            startTurn = false;
        }
        if (game.getTeamTurn() == team) {
            // Does next move and sets a delay so the player can watch the AI move
            long delay = nextMove();
            handler.postDelayed(this, delay);
        }
    }

    private void selectUnit(Unit unit) {
        selectedUnit = unit;
        selectedUnitMoves = game.findValidMoveTiles(unit);
        gameView.setTileChecks(selectedUnitMoves);
    }

    public void deselectUnit() {
        selectedUnit = null;
        selectedUnitMoves = null;
        gameView.setTileChecks(null);
        target = null;
    }

    private void endTurn() {
        game.endTurn();
        startTurn = true;
        if (game.getTeamTurn() == GameController.getPlayerTeam())
            gameController.startTurn();
    }

    // Finds attack range and attempts to set target to enemy in range that is best to attack
    private boolean attackInRange(Unit unit) {
        attackRange = game.findAttackRange(unit);
        int highestDamage = 0;
        for (int i = 0; i < level.getNumRows(); i++)
            for (int j = 0; j < level.getNumCols(); j++) {
                if (attackRange[i][j] == TileCheck.ATTACK_TARGET) {
                    Unit enemy = level.getLevelTiles()[i][j].getUnit();

                    // Checks damage dealt and value of the unit to find the best attack
                    int damage = game.getDamage(unit, enemy) * enemy.getValue();
                    if (damage > highestDamage) {
                        highestDamage = damage;
                        target = enemy;
                    }
                }
            }
        if (highestDamage > 0)
            return true;
        else
            return false;
    }

    // Moves towards center of friendly units and to defensive terrain
    private Tile defenseMove(Unit unit) {
        // First, get the average row and column for other units on your team
        int friendlyUnitCount = 0;
        int colTotal = 0;
        int rowTotal = 0;
        for (Unit friendlyUnit : level.getUnitList()) {
            if (friendlyUnit != unit & friendlyUnit.getTeam() == unit.getTeam()) {
                friendlyUnitCount += 1;
                colTotal += friendlyUnit.getCol();
                rowTotal += friendlyUnit.getRow();
            }
        }
        int colAverage = colTotal / friendlyUnitCount;
        int rowAverage = rowTotal / friendlyUnitCount;

        // Median
/*        ArrayList<Integer> friendlyCols = new ArrayList<Integer>();
        ArrayList<Integer> friendlyRows = new ArrayList<Integer>();
        for (Unit friendlyUnit : level.getUnitList()) {
            if (friendlyUnit != unit & friendlyUnit.getTeam() == unit.getTeam()) {
                friendlyCols.add(friendlyUnit.getCol());
                friendlyRows.add(friendlyUnit.getRow());
            }
        }
        Collections.sort(friendlyCols);
        Collections.sort(friendlyRows);
        int colMedian = friendlyCols.get(friendlyCols.size() / 2);
        int rowMedian = friendlyRows.get(friendlyRows.size() / 2);*/

        TileCheck[][] possibleMoves = game.findValidMoveTiles(unit);
        Tile bestMove = null;
        int bestMoveScore = 0;
        Tile[][] levelTiles = level.getLevelTiles();
        for (int i = 0; i < level.getNumRows(); i++)
            for (int j = 0; j < level.getNumCols(); j++) {
                if (possibleMoves[i][j] == TileCheck.VALID_MOVE) {
                    Tile tile = levelTiles[i][j];
                    int row = i + 1;
                    int col = j + 1;
                    int terrainBonus = game.getDefenseBonus(tile.getTerrain().getTerrainType()) * 2;

                    // Try to move next to friendly non-artilley units
                    int nearUnitBonus = 0;
                    if (i - 1 >= 0) {
                        Unit neighbor = levelTiles[i - 1][j].getUnit();
                        if (neighbor != null && neighbor.getTeam() == unit.getTeam() && !neighbor.isIndirect())
                            nearUnitBonus += 5;
                    }

                    if (i + 1 < level.getNumRows()) {
                        Unit neighbor = levelTiles[i + 1][j].getUnit();
                        if (neighbor != null && neighbor.getTeam() == unit.getTeam() && !neighbor.isIndirect())
                            nearUnitBonus += 5;
                    }

                    if (j - 1 >= 0) {
                        Unit neighbor = levelTiles[i][j - 1].getUnit();
                        if (neighbor != null && neighbor.getTeam() == unit.getTeam() && !neighbor.isIndirect())
                            nearUnitBonus += 5;
                    }

                    if (j + 1 < level.getNumCols()) {
                        Unit neighbor = levelTiles[i][j + 1].getUnit();
                        if (neighbor != null && neighbor.getTeam() == unit.getTeam() && !neighbor.isIndirect()) {
                            nearUnitBonus += 5;
                        }
                    }

                    int moveScore = 0;
                    // Finds difference between distance of unit's current position and tested position
                    moveScore += (Math.abs(unit.getRow() - rowAverage) - Math.abs(row - rowAverage));
                    moveScore += (Math.abs(unit.getCol() - colAverage) - Math.abs(col - colAverage));
                    moveScore += terrainBonus;
                    moveScore += nearUnitBonus;
                    if (bestMove == null || (moveScore > bestMoveScore)) {
                        bestMoveScore = moveScore;
                        bestMove = tile;
                    }
                }
            }

        return bestMove;
    }

    // Makes next move and returns delay after move in milliseconds
    private long nextMove() {
        // Select next friendly unit if one isn't selected
        if (selectedUnit == null) {
            while (unitIterator.hasNext()) {
                Unit unit = unitIterator.next();
                if (unit.getTeam() == team && unit.isActive() && !unit.isDead()) {
                    selectUnit(unit);
                    target = null;
                    unitMoved = false;
                    return 2000;
                }
            }
        }
        // Unit is already selected
        else if (selectedUnit.isActive()) {
            // Unit is indirect (artillery)
            if (selectedUnit.isIndirect()) {
                // No current target
                if (target == null) {
                    // Selected unit hasn't moved
                    if (!unitMoved) {
                        // Try to find a target
                        if (attackInRange(selectedUnit)) {
                            gameView.setTileChecks(attackRange);
                            return 2000;
                        }
                        // If no target in range, move
                        else {
                            unitMoved = true;
                            Tile moveTile = defenseMove(selectedUnit);
                            if (moveTile != null) {
                                game.moveUnit(selectedUnit, moveTile);
                                selectUnit(selectedUnit);
                            }
                            deselectUnit();
                            return 2000;
                        }
                    }
                    else {
                        deselectUnit();
                        return 1000;
                    }
                }
                // Have a target, attack it
                else {
                    game.attackUnit(selectedUnit, target);
                    deselectUnit();
                    gameController.checkGameOver();
                    return 2000;
                }
            }
            // Unit has direct attack (not artillery)
            else {
                if (!unitMoved) {
                    unitMoved = true;
                    // Find attacks that can be made either this turn or in the future, and find the paths to them
                    List<MoveDestination> attackMoves = game.findAttackMoves(selectedUnit);
                    MoveDestination bestAttack = null;
                    // Find the best possible attack by comparing damage dealt and value of target unit
                    for (MoveDestination moveDestination : attackMoves) {
                        if (bestAttack != null) {
                            int bestAttackValue = bestAttack.getAttackDamage() * bestAttack.getAttackTile().getUnit().getValue();
                            int attackValue = moveDestination.getAttackDamage() * moveDestination.getAttackTile().getUnit().getValue();
                            if (bestAttack.getTile().getUnit() != null ||
                                    (bestAttackValue < attackValue && moveDestination.getTile().getUnit() == null))
                                bestAttack = moveDestination;
                        }
                        else
                            bestAttack = moveDestination;
                    }
                    Tile moveTile = null;
                    if (bestAttack != null) {
                        float[] moveCosts = game.getMoveCostArray(selectedUnit.getMoveType());
                        int movement = selectedUnit.getRemainingMovement();
                        // Find tile to move to using path of best attack
                        for (Tile tile : bestAttack.getPath()) {
                            float moveCost = moveCosts[tile.getTerrain().getTerrainType().ordinal()];
                            if (movement >= moveCost)
                                movement -= moveCost;
                            else
                                break;
                            if (tile.getUnit() == null)
                                moveTile = tile;
                        }
                    }
                    // No attacks, move to defensive position
                    else
                        moveTile = defenseMove(selectedUnit);
                    if (moveTile != null) {
                        game.moveUnit(selectedUnit, moveTile);
                        selectUnit(selectedUnit);
                    }
                    return 1000;
                }
                // If no target, try to find one in range
                else if (target == null) {
                    if (attackInRange(selectedUnit)) {
                        gameView.setTileChecks(attackRange);
                        return 2000;
                    } else {
                        deselectUnit();
                        return 1000;
                    }
                }
                // Attack target
                else {
                    game.attackUnit(selectedUnit, target);
                    deselectUnit();
                    gameController.checkGameOver();
                    return 2000;
                }
            }
        }

        deselectUnit();
        endTurn();
        return 0;
    }

}
