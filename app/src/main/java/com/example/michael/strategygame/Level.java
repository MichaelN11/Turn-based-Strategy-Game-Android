package com.example.michael.strategygame;

import java.util.ArrayList;
import java.util.List;

public class Level {
    private Tile[][] levelTiles;
    private List<Unit> unitList;
    private int numRows, numCols;

    public Level(Tile[][] levelTiles, List<Unit> unitList, int numRows, int numCols) {
        this.levelTiles = levelTiles;
        this.unitList = unitList;
        this.numRows = numRows;
        this.numCols = numCols;

        // Tells terrain if it is connected to another terrain of the same type in a certain
        // direction, used for displaying things like roads
        makeTerrainConnections();
    }

    public Tile[][] getLevelTiles() { return levelTiles; }

    public List<Unit> getUnitList() {
        return unitList;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    private void makeTerrainConnections(){
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                boolean north, south, east, west;
                Terrain t = levelTiles[i][j].getTerrain();

                // If cell in direction is out of bounds or of the same type, set value to true
                north = ((i - 1 < 0) || (t.getTerrainType() == levelTiles[i-1][j].getTerrain().getTerrainType()));
                south = ((i + 1 >= numRows) || (t.getTerrainType() == levelTiles[i+1][j].getTerrain().getTerrainType()));
                east = ((j + 1 >= numCols) || (t.getTerrainType() == levelTiles[i][j+1].getTerrain().getTerrainType()));
                west = ((j - 1 < 0) || (t.getTerrainType() == levelTiles[i][j-1].getTerrain().getTerrainType()));

                t.setConnections(north, south, east, west);
            }
        }
    }
}
