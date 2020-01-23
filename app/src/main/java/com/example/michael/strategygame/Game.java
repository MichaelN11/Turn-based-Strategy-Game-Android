package com.example.michael.strategygame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Game {
    private Level level;
    private int numTeams = 2;
    private int teamTurn = 1;
    private int numRows, numCols;
    private boolean gameOver = false;
    private int winner = 0;

    private TileCheck[][] validTiles;
    private int[][] moveRemaining;
    private boolean[][] beenToTile;
    private float[] terrainCosts;
    private Unit movingUnit;
    private Tile destination;
    private LinkedList<Tile> tempMovePath;
    private LinkedList<Tile> finalMovePath;
    private int movePathMoveRemaining;
    private boolean findingAttacks = false;
    private int moveDepth = 0;
    private LinkedList<MoveDestination> continueMoves;
    private List<MoveDestination> attackList;

    public Game() {
    }

    public void setLevel(Level level) {
        this.level = level;
        numRows = level.getNumRows();
        numCols = level.getNumCols();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getWinner() {
        return winner;
    }

    public void endTurn() {
        List<Unit> unitList = level.getUnitList();

        // Refresh units' movement
        for (Unit unit : unitList) {
            if (unit.getTeam() == teamTurn && !unit.isDead()) {
                unit.setRemainingMovement(unit.getMaxMovement());
                unit.setActive(true);
            }
        }

        // Cycle which team's turn it is
        teamTurn = (teamTurn < numTeams) ? teamTurn + 1 : 1;
    }

    // Finds the tiles a unit is able to move to this turn
    public TileCheck[][] findValidMoveTiles(Unit unit) {
        prepareMoveTest(unit);
        int movement = unit.getRemainingMovement();
        int row = unit.getRow();
        int col = unit.getCol();
        destination = null;
        moveTest(row, col, movement, true);
        return validTiles;
    }

    public LinkedList<Tile> moveUnit(Unit unit, Tile destination) {

        // Make sure it's the unit's turn to move
        if (unit.getTeam() != teamTurn || !unit.isActive() || unit.isDead())
            return null;
        // Don't move on top of units
        if (destination.getUnit() != null)
            return null;

        prepareMoveTest(unit);
        int movement = unit.getRemainingMovement();
        int row = unit.getRow();
        int col = unit.getCol();
        this.destination = destination;
        tempMovePath = new LinkedList<Tile>();
        finalMovePath = new LinkedList<Tile>();
        moveTest(row, col, movement, true);
        if (!finalMovePath.isEmpty()) {
            Tile[][] levelTiles = level.getLevelTiles();
            int destRow = destination.getRow();
            int destCol = destination.getCol();
            levelTiles[row - 1][col - 1].setUnit(null);
            levelTiles[destRow - 1][destCol - 1].setUnit(unit);
            unit.setRowAndCol(destRow, destCol);
            unit.setRemainingMovement(movePathMoveRemaining);
            return finalMovePath;
        }
        return null;
    }

    public TileCheck[][] findAttackRange(Unit unit) {
        int row = unit.getRow();
        int col = unit.getCol();
        return findAttackRange(unit, row, col);
    }

    public TileCheck[][] findAttackRange(Unit unit, int row, int col) {
        Tile[][] tiles = level.getLevelTiles();
        int minRange = unit.getMinRange();
        int maxRange = unit.getMaxRange();

        TileCheck[][] attackRange = new TileCheck[numRows][numCols];
        for(int i = 0; i < numRows; i++)
            for (int j = 0; j < numCols; j++)
                attackRange[i][j] = TileCheck.INVALID;

        // For each radius, go around and mark the tiles in range until it's back at the start
        int x, y, xIncr, yIncr;
        for (int i = minRange; i <= maxRange; i++) {
            x = col - 1;
            y = row - 1 + i;
            xIncr = 1;
            yIncr = 1;
            do {
                if (y >= 0 && x >= 0 && y < numRows && x < numCols) {
                    Unit tileUnit = tiles[y][x].getUnit();
                    if (tileUnit != null && tileUnit.getTeam() != unit.getTeam())
                        attackRange[y][x] = TileCheck.ATTACK_TARGET;
                    else
                        attackRange[y][x] = TileCheck.ATTACK_EMPTY;
                }
                if (Math.abs(x - (col - 1)) == i)
                    xIncr *= -1;
                if (Math.abs(y - (row - 1)) == i)
                    yIncr *= -1;
                x += xIncr;
                y += yIncr;
            } while (y != row - 1 + i);
        }

        return attackRange;
    }

    public boolean attackUnit(Unit attacker, Unit defender) {

        // Must be attacker's turn, must be active and alive, if indirect unit it can't have moved
        if (!unitCanAttack(attacker))
            return false;
        // If defender is in attacker's range, the attacker fires first on the defender
        TileCheck[][] attackRange = findAttackRange(attacker);
        if (attackRange[defender.getRow() - 1][defender.getCol() - 1] == TileCheck.ATTACK_TARGET) {
            unitFireOnUnit(attacker, defender);
            // If the defender is still alive, and it was a direct attack, return fire
            if (!defender.isDead() && !attacker.isIndirect() && !defender.isIndirect())
                unitFireOnUnit(defender, attacker);
            attacker.setActive(false);
            return true;
        }
        return false;
    }

    public void killUnit(Unit unit) {
        unit.setDead(true);
        level.getLevelTiles()[unit.getRow() - 1][unit.getCol() - 1].setUnit(null);
        checkGameState();
    }

    public int getTeamTurn() {
        return teamTurn;
    }

    public boolean unitCanAttack(Unit unit) {
        if (unit.getTeam() != teamTurn)
            return false;
        if (!unit.isActive() || unit.isDead())
            return false;
        if (unit.isIndirect() && unit.getRemainingMovement() < unit.getMaxMovement())
            return false;
        return true;
    }

    public List<MoveDestination> findAttackMoves(Unit unit) {
        prepareMoveTest(unit);
        tempMovePath = new LinkedList<Tile>();
        continueMoves = new LinkedList<MoveDestination>();
        attackList = new LinkedList<MoveDestination>();
        moveDepth = 0;
        findingAttacks = true;
        moveTest(unit.getRow(), unit.getCol(), unit.getRemainingMovement(), true);
        while (attackList.isEmpty() && !continueMoves.isEmpty()) {
            moveDepth -= unit.getMaxMovement();
            LinkedList<MoveDestination> prevContinueMoves = new LinkedList<MoveDestination>(continueMoves);
            for (MoveDestination moveDestination : prevContinueMoves) {
                tempMovePath = moveDestination.getPath();
                moveTest(moveDestination.getTile().getRow(), moveDestination.getTile().getCol(), moveDestination.getMovement(), false);
            }
        }
        findingAttacks = false;
        return attackList;
    }

    public int getDamage(Unit firingUnit, Unit receivingUnit) {
        int attackPower = firingUnit.getAttackPower();
        int attackerHealth = firingUnit.getHealth();

        Tile defenseTile = level.getLevelTiles()[receivingUnit.getRow() - 1][receivingUnit.getCol() - 1];
        int terrainDefense = getDefenseBonus(defenseTile.getTerrain().getTerrainType());
        int defenderHealth = receivingUnit.getHealth();
        int defense = receivingUnit.getDefense();

        float matchup = getMatchupModifer(firingUnit.getDamageType(), receivingUnit.getMoveType());

        float terrainFactor = (100 - (terrainDefense * (defenderHealth / 10))) / 100.0f;
        float healthFactor = attackerHealth / 100.0f;
        int baseDamage = 55 + (attackPower * 10) - (defense * 10);

        return (int) (baseDamage * healthFactor * terrainFactor * matchup);
    }

    private void unitFireOnUnit(Unit firingUnit, Unit receivingUnit) {
        int damage = getDamage(firingUnit, receivingUnit);
        receivingUnit.setHealth(receivingUnit.getHealth() - damage);
        if (receivingUnit.getHealth() <= 0)
            killUnit(receivingUnit);
    }

    private void checkGameState() {
        int[] unitCount = new int[numTeams];
        for (Unit unit : level.getUnitList()) {
            if (!unit.isDead())
                unitCount[unit.getTeam() - 1] += 1;
        }
        int teamsAlive = 0;
        int winTeam = 0;
        for (int i = 0; i < numTeams; i++) {
            if (unitCount[i] > 0) {
                teamsAlive += 1;
                winTeam = i + 1;
            }
        }
        if (teamsAlive == 0) {
            //draw
            gameOver = true;
            teamTurn = 0;
            winner = 0;
        }
        else if (teamsAlive == 1) {
            //winner
            gameOver = true;
            teamTurn = 0;
            winner = winTeam;
        }
    }

    private float getMatchupModifer(Unit.DamageType attackerType, Unit.MoveType defenderType) {
        switch (attackerType) {
            case GUN:
                switch (defenderType) {
                    case TIRE:
                    case TREAD:
                        return 0.5f;
                }
            case EXPLOSIVE:
                switch (defenderType) {
                    case TIRE:
                    case TREAD:
                        return 1.5f;
                }
            case ANTI_VEHICLE:
                switch (defenderType) {
                    case TIRE:
                    case TREAD:
                        return 2;
                }
        }
        return 1;
    }

    private void prepareMoveTest(Unit unit) {
        // Initialize the arrays
        validTiles = new TileCheck[numRows][numCols];
        moveRemaining = new int[numRows][numCols];
        beenToTile = new boolean[numRows][numCols];
        for(int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                validTiles[i][j] = TileCheck.INVALID;
                moveRemaining[i][j] = 0;
                beenToTile[i][j] = false;
            }
        }
        Unit.MoveType moveType = unit.getMoveType();
        // Find the movement cost for each terrain type based off the unit's move type,
        // stored in an array
        terrainCosts = getMoveCostArray(moveType);
        movingUnit = unit;
    }

    private void moveTest(int row, int col, int movement, boolean firstStep) {
        // Return if off the level
        if (row > numRows || row < 1 ||
                col > numCols || col < 1)
            return;
        // Return if already been to this tile with this same amount of movement points
        if (!firstStep && beenToTile[row - 1][col - 1] && movement <= moveRemaining[row - 1][col - 1])
            return;
        moveRemaining[row - 1][col - 1] = movement;
        beenToTile[row - 1][col - 1] = true;

        Tile currentTile = level.getLevelTiles()[row - 1][col - 1];

        if (!firstStep) {
            // Find the cost of the current terrain
            Terrain.TerrainType terrain = currentTile.getTerrain().getTerrainType();
            float cost = terrainCosts[terrain.ordinal()];
            // If looking for future moves, save current tile to return to after moving to next depth
            if (findingAttacks) {
                if (movement - cost < moveDepth) {
                    // Come back later
                    continueMoves.add(new MoveDestination(new LinkedList<Tile>(tempMovePath), movement, currentTile));
                    beenToTile[row - 1][col - 1] = false;
                    return;
                }
            }
            // Return if terrain costs too many movement points and we're not checking past 1 turn
            else if (movement < cost)
                return;
            movement -= cost;
        }

        // Can't move through other team's units, can't move on top of own team's units
        Unit unit = currentTile.getUnit();
        if (unit != null) {
            if (unit.getTeam() != movingUnit.getTeam()) {
                validTiles[row - 1][col - 1] = TileCheck.ENEMY;
                return;
            }
            else if (firstStep)
                // Save the unit's origin point
                validTiles[row - 1][col - 1] = TileCheck.ORIGIN;
            else
                validTiles[row - 1][col - 1] = TileCheck.PASS_ONLY;
        }
        else
            validTiles[row - 1][col - 1] = TileCheck.VALID_MOVE;

        if (!firstStep && (destination != null || findingAttacks))
            tempMovePath.add(currentTile);

        // If at destination, stop and save path and amount of movement points remaining
        if (destination != null && destination == currentTile) {
            finalMovePath = new LinkedList<Tile>(tempMovePath);
            movePathMoveRemaining = movement;
            return;
        }

        if (findingAttacks) {
            TileCheck[][] attacks = findAttackRange(movingUnit, row, col);
            for (int i = 0; i < numRows; i++)
                for (int j = 0; j < numCols; j++) {
                    if (attacks[i][j] == TileCheck.ATTACK_TARGET) {
                        Tile attackTile = level.getLevelTiles()[i][j];
                        attackList.add(new MoveDestination(new LinkedList<Tile>(tempMovePath), movement,
                                currentTile, attackTile, getDamage(movingUnit, attackTile.getUnit())));
                    }
                }
        }

        moveTest(row - 1, col, movement, false);
        moveTest(row + 1, col, movement, false);
        moveTest(row, col - 1, movement, false);
        moveTest(row, col + 1, movement, false);
        if (!firstStep && (destination != null || findingAttacks))
            tempMovePath.removeLast();
    }

    // Some terrain gives a defensive bonus to units on the cell
    public int getDefenseBonus(Terrain.TerrainType terrain) {
        switch (terrain) {
            case EMPTY:
                return 1;
            case FOREST:
                return 2;
            case MOUNTAIN:
                return 3;
            default:
                return 0;
        }
    }

    public float[] getMoveCostArray(Unit.MoveType type) {
        Terrain.TerrainType terrainTypes[] = Terrain.TerrainType.values();
        float[] terrainCosts = new float[terrainTypes.length];
        for (Terrain.TerrainType terrain : terrainTypes) {
            terrainCosts[terrain.ordinal()] = getMoveCost(type, terrain);
        }
        return terrainCosts;
    }

    private float getMoveCost(Unit.MoveType moveType, Terrain.TerrainType terrainType) {
        float immovable = 100;
        if (terrainType == Terrain.TerrainType.WATER)
            return immovable;
        switch (moveType) {
            case FOOT:
                switch(terrainType) {
                    case MOUNTAIN:
                        return 2;
                }
                break;
            case TIRE:
                switch (terrainType) {
                    case EMPTY:
                        return 2;
                    case FOREST:
                        return 3;
                    case MOUNTAIN:
                        return immovable;
                }
                break;
            case TREAD:
                switch (terrainType) {
                    case FOREST:
                        return 2;
                    case MOUNTAIN:
                        return immovable;
                }
                break;
        }
        return 1;
    }
}
