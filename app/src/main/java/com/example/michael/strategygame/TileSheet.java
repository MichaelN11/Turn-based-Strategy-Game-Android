package com.example.michael.strategygame;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class TileSheet {
    private Bitmap sheet;
    private int rowNum, colNum;
    private int tileWidth, tileHeight;

    public TileSheet(Bitmap sheet, int rowNum, int colNum, int tileWidth, int tileHeight) {
        this.sheet = sheet;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public Bitmap getBitmap() {
        return sheet;
    }

    // Gets a rectangle that contains a tile from the tilesheet
    public Rect getTile(int row, int col) {
        if (row <= rowNum && col <= colNum)
            return new Rect((col - 1) * tileWidth, (row - 1) * tileHeight, col * tileWidth, row * tileHeight);
        else
            return null;
    }

}
