package com.example.michael.strategygame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class GameController {
    private static int PLAYER_TEAM = 1;

    private GameView gameView;
    private Game game;
    private Level level;
    private Unit selectedUnit;
    private TileCheck[][] selectedUnitMoves;
    private boolean attackMode = false;
    private Button fireButton;
    private Button endTurnButton;
    private Button levelSelectButton;
    private TextView endText, turnText;
    private AI ai;
    private Handler aiHandler;
    private Intent menuIntent;

    public GameController(final GameView gameView, Button[] buttons, TextView endText, TextView turnText) {
        this.gameView = gameView;
        game = new Game();

        fireButton = buttons[0];
        fireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireOrder();
            }
        });
        fireButton.setVisibility(View.INVISIBLE);
        endTurnButton = buttons[1];
        endTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTurn();
            }
        });
        levelSelectButton = buttons[2];

        this.endText = endText;
        this.turnText = turnText;

        aiHandler = new Handler();
    }

    // Loads a level from a string of characters representing terrain types, and a list of units
    public void loadLevel(String levelTerrainString, int numRows, int numCols, List<Unit> unitList) {
        Tile[][] tiles = new Tile[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                // Find position of char equivalent to position in the array
                int pos = (i * numCols) + j;
                tiles[i][j] = new Tile(i + 1, j + 1, getTerrainFromChar(levelTerrainString.charAt(pos)), null);
            }
        }

        for (Unit unit : unitList) {
            tiles[unit.getRow() - 1][unit.getCol() - 1].setUnit(unit);
        }

        level = new Level(tiles, unitList, numRows, numCols);
        gameView.setLevel(level);
        game.setLevel(level);

        init();
    }

    private void init() {

        ai = new AI(game, gameView, this, level, 2);

        // IDE mad at me because this is not blind people friendly
        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && !gameView.isScrolling()) {
                    float x = event.getX();
                    float y = event.getY();
                    Tile tile = gameView.getTileAtPos(x, y);
                    tileClicked(tile.getRow(), tile.getCol());
                }
                return false;
            }
        });

        startTurn();
    }

    public static int getPlayerTeam() {
        return PLAYER_TEAM;
    }

    public void selectUnit(Unit unit) {
        if (unit.isActive()) {
            attackMode = false;
            selectedUnit = unit;
            selectedUnitMoves = game.findValidMoveTiles(unit);
            gameView.setTileChecks(selectedUnitMoves);
            if (game.unitCanAttack(unit))
                fireButton.setVisibility(View.VISIBLE);
            else
                fireButton.setVisibility(View.INVISIBLE);
        }
    }

    public void deselectUnit() {
        fireButton.setVisibility(View.INVISIBLE);
        selectedUnit = null;
        selectedUnitMoves = null;
        gameView.setTileChecks(null);
    }

    public void fireOrder() {
        if (selectedUnit != null && game.unitCanAttack(selectedUnit)) {
            attackMode = true;
            selectedUnitMoves = game.findAttackRange(selectedUnit);
            gameView.setTileChecks(selectedUnitMoves);
        }
    }

    public void endTurn() {
        deselectUnit();
        endTurnButton.setVisibility(View.INVISIBLE);
        turnText.setText("Enemy Turn");
        turnText.setTextColor(Color.RED);
        game.endTurn();

        // AI's turn
        aiHandler.post(ai);

    }

    public void winLevel() {
        endTurnButton.setVisibility(View.INVISIBLE);
        endText.setVisibility(View.VISIBLE);
        levelSelectButton.setVisibility(View.VISIBLE);
        turnText.setVisibility(View.INVISIBLE);
    }

    public void loseLevel() {
        endTurnButton.setVisibility(View.INVISIBLE);
        endText.setText("Defeat!");
        endText.setTextColor(Color.RED);
        endText.setVisibility(View.VISIBLE);
        levelSelectButton.setVisibility(View.VISIBLE);
        turnText.setVisibility(View.INVISIBLE);
    }

    public void startTurn() {

        endTurnButton.setVisibility(View.VISIBLE);
        turnText.setText("Player Turn");
        turnText.setTextColor(Color.BLUE);
    }

    public void checkGameOver() {
        if (game.isGameOver()) {
            if (game.getWinner() == PLAYER_TEAM)
                winLevel();
            else
                loseLevel();
        }
    }

    private void tileClicked(int row, int col) {
        // If you tap a unit that belongs to you, select it, otherwise if you have a unit selected
        // try to execute a move or attack order
        if (game.getTeamTurn() == PLAYER_TEAM) {
            Unit unit = level.getLevelTiles()[row - 1][col - 1].getUnit();
            if (unit != null && unit.getTeam() == PLAYER_TEAM && selectedUnit != unit)
                selectUnit(unit);
            else if (selectedUnit != null) {
                TileCheck tileCheck = selectedUnitMoves[row - 1][col - 1];
                if (!attackMode) {
                    if (tileCheck == TileCheck.VALID_MOVE) {
                        game.moveUnit(selectedUnit, level.getLevelTiles()[row - 1][col - 1]);
                        selectUnit(selectedUnit);
                    } else if (tileCheck == TileCheck.INVALID || tileCheck == TileCheck.ORIGIN)
                        deselectUnit();
                } else {
                    // Selected unit attacks the unit at the clicked position
                    if (tileCheck == TileCheck.ATTACK_TARGET) {
                        Unit target = level.getLevelTiles()[row - 1][col - 1].getUnit();
                        if (game.attackUnit(selectedUnit, target)) {
                            deselectUnit();
                            checkGameOver();
                        }
                    } else
                        deselectUnit();
                }
            }
        }
    }

    private Terrain getTerrainFromChar(char c) {
        Terrain.TerrainType terrain;
        switch (c) {
            case 'F':
                terrain = Terrain.TerrainType.FOREST;
                break;
            case 'M':
                terrain = Terrain.TerrainType.MOUNTAIN;
                break;
            case 'W':
                terrain = Terrain.TerrainType.WATER;
                break;
            case 'R':
                terrain = Terrain.TerrainType.ROAD;
                break;
            default:
                terrain = Terrain.TerrainType.EMPTY;
        }
        return new Terrain(terrain);
    }
}
