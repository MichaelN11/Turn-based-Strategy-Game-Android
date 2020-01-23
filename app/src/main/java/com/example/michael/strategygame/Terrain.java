package com.example.michael.strategygame;

public class Terrain {
    public enum TerrainType {
    EMPTY,
    WATER,
    FOREST,
    ROAD,
    MOUNTAIN }

    private TerrainType type;
    private boolean connectNorth = false;
    private boolean connectEast = false;
    private boolean connectWest = false;
    private boolean connectSouth = false;

    public Terrain(TerrainType type) {
        this.type = type;
    }

    public void setConnections(boolean north, boolean south, boolean east, boolean west) {
        connectEast = east;
        connectNorth = north;
        connectSouth = south;
        connectWest = west;
    }

    public TerrainType getTerrainType() {
        return type;
    }

    public boolean isConnectNorth() {
        return connectNorth;
    }

    public boolean isConnectEast() {
        return connectEast;
    }

    public boolean isConnectWest() {
        return connectWest;
    }

    public boolean isConnectSouth() {
        return connectSouth;
    }

}
