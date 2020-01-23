package com.example.michael.strategygame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;

import java.util.Random;

public class GameView extends InteractiveView {
    private RectF gameBounds;
    private int numRows, numCols;
    private int tileSize = 64;
    private Level level;
    private Rect[][] terrainRects;
    private TileSheet tileSheet;
    private TileCheck[][] tileChecks;

    private Paint gridPaint, movePaint, attackPaint, healthPaint, friendlyRectPaint, enemyRectPaint, selectRectPaint;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super (context, attrs);

        // Creates the sci-fi tilesheet object from the reference id
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scifi_tilesheet, options);
        tileSheet = new TileSheet(bitmap, 7, 18, 64, 64);

        // Sets scale values in InteractiveView class
        setScaling(2.0f, 1.5f, 2.5f);

        gridPaint = new Paint();
        movePaint = new Paint();
        attackPaint = new Paint();
        healthPaint = new Paint();
        friendlyRectPaint = new Paint();
        enemyRectPaint = new Paint();
        selectRectPaint = new Paint();

        movePaint.setStyle(Paint.Style.FILL);
        movePaint.setColor(Color.argb(50, 0, 0, 200));

        attackPaint.setStyle(Paint.Style.FILL);
        attackPaint.setColor(Color.argb(70, 255, 0, 0));

        friendlyRectPaint.setStyle(Paint.Style.STROKE);
        friendlyRectPaint.setColor(Color.BLUE);
        friendlyRectPaint.setStrokeWidth(2);

        enemyRectPaint.setStyle(Paint.Style.STROKE);
        enemyRectPaint.setColor(Color.RED);
        enemyRectPaint.setStrokeWidth(2);

        selectRectPaint.setStyle(Paint.Style.STROKE);
        selectRectPaint.setColor(Color.WHITE);
        selectRectPaint.setStrokeWidth(3);

        healthPaint.setColor(Color.YELLOW );
        healthPaint.setTextSize(26);
        healthPaint.setTextAlign(Paint.Align.RIGHT);
    }

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }

    public void setTileChecks(TileCheck[][] tileChecks) {
        this.tileChecks = tileChecks;
        invalidate();
    }

    public Tile getTileAtPos(float x, float y) {
        x -= getTranslateX();
        y -= getTranslateY();
        float scaledTileSize = tileSize * getScaleFactor();
        int col = (int)(x / scaledTileSize) + 1;
        int row = (int)(y / scaledTileSize) + 1;
        return level.getLevelTiles()[row - 1][col - 1];
    }

    public void setLevel(Level level) {
        this.level = level;
        numRows = level.getNumRows();
        numCols = level.getNumCols();

        // Sets the boundaries for the InteractiveView class panning and zooming
        float rightBound = numCols * tileSize;
        float bottomBound = numRows * tileSize;
        gameBounds = new RectF(0, 0, rightBound, bottomBound);
        setBounds(gameBounds);

        // Creates a rectangle array of all the tile positions on the tilesheet
        // to draw for the terrain
        terrainRects = getTerrainRectArray();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Scales and translates the canvas in the InteractiveView class
        super.onDraw(canvas);
        if (level != null)
            drawLevel(canvas);
        drawGrid(canvas);
    }

    private void drawGrid(Canvas canvas) {
        // Draw vertical grid lines
        for (int i = 0; i < numCols; i++) {
            int x = (i + 1) * tileSize;
            canvas.drawLine(x, 0, x, numRows * tileSize, gridPaint);
        }
        // Draw horizontal grid lines
        for (int i = 0; i < numRows; i++) {
            int y = (i + 1) * tileSize;
            canvas.drawLine(0, y, numCols * tileSize, y, gridPaint);
        }
    }

    private void drawLevel(Canvas canvas) {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                Bitmap bitmap = tileSheet.getBitmap();
                // Rectangle of the cell on the screen
                RectF tileRect = getTileRect(i + 1, j + 1);
                // Rectangle of the terrain tile on the tilesheet
                Rect spriteRect = terrainRects[i][j];
                // Draw terrain
                if (spriteRect != null)
                    canvas.drawBitmap(bitmap, spriteRect, tileRect, null);

                // Draw unit
                Unit unit = level.getLevelTiles()[i][j].getUnit();
                if (unit != null) {
                    // Rectangle of the unit tile on the tilesheet
                    spriteRect = getUnitRect(unit);
                    canvas.drawBitmap(bitmap, spriteRect, tileRect, null);
                    // Draw unit's health indicator
                    if (unit.getHealth() < 100) {
                        String health = Integer.toString(Math.max(Math.round(unit.getHealth() / 10.0f), 1));
                        canvas.drawText(health, tileRect.right - 10, tileRect.bottom - 10, healthPaint);
                    }
                    if (unit.getTeam() == 1) {
                        canvas.drawRect(tileRect.left + 1, tileRect.top + 1, tileRect.right - 1, tileRect.bottom - 1, friendlyRectPaint);
                    }
                    else if (unit.getTeam() == 2) {
                        canvas.drawRect(tileRect.left + 1, tileRect.top + 1, tileRect.right - 1, tileRect.bottom - 1, enemyRectPaint);
                    }
                }

                // Draw move rectangle over the tile if the tile is able to be moved to by the
                // selected unit, or draw attack indicators if the unit is in attack mode
                if (tileChecks != null) {
                    TileCheck tile = tileChecks[i][j];
                    if (tile == TileCheck.VALID_MOVE || tile == TileCheck.PASS_ONLY)
                        canvas.drawRect(tileRect, movePaint);
                    else if (tile == TileCheck.ATTACK_EMPTY || tile == TileCheck.ATTACK_TARGET)
                        canvas.drawRect(tileRect, attackPaint);
                    else if (tile == TileCheck.ORIGIN) {
                        canvas.drawRect(tileRect.left + 2, tileRect.top + 2, tileRect.right - 2, tileRect.bottom - 2, selectRectPaint);
                    }
                }
            }
        }
    }

    private RectF getTileRect(int row, int col) {

        return new RectF((col - 1) * tileSize, (row - 1) * tileSize, col * tileSize, row * tileSize);
    }

    private Rect getUnitRect(Unit unit) {
        if (unit == null)
            return null;

        Unit.UnitSprite sprite = unit.getSprite();
        int team = unit.getTeam();
        // Team's sprites start on row 4
        int row = team + 3;

        switch (sprite) {
            case INFANTRY:
                return tileSheet.getTile(row, 7);
            case TANK:
                return tileSheet.getTile(row, 16);
            case ARTILLERY:
                return tileSheet.getTile(row, 14);
            case RPG:
                return tileSheet.getTile(row, 10);
        }
        return null;
    }

    private Rect[][] getTerrainRectArray() {
        Random random = new Random();
        Tile[][] tiles = level.getLevelTiles();
        Rect[][] terrainTiles = new Rect[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                terrainTiles[i][j] = getTerrainRect(tiles[i][j].getTerrain(), random);
            }
        }

        return terrainTiles;
    }

    private Rect getTerrainRect(Terrain terrain, Random random) {
        int variant;

        // Whether the terrain connects to other terrain or edge of the map
        boolean north = terrain.isConnectNorth();
        boolean south = terrain.isConnectSouth();
        boolean east = terrain.isConnectEast();
        boolean west = terrain.isConnectWest();

        Terrain.TerrainType terrainType = terrain.getTerrainType();
        switch (terrainType) {
            case EMPTY:
                // Randomly select between 2 empty tiles
                variant = random.nextInt(2);
                if (variant == 0)
                    return tileSheet.getTile(1, 1);
                else
                    return tileSheet.getTile(1, 2);
            case WATER:
                return tileSheet.getTile(2, 1);
            case FOREST:
                // Randomly select between 6 forest tiles
                variant = random.nextInt(6);
                switch (variant) {
                    case 0:
                        return tileSheet.getTile(1, 4);
                    case 1:
                        return tileSheet.getTile(2, 3);
                    case 2:
                        return tileSheet.getTile(2, 4);
                    case 3:
                        return tileSheet.getTile(3, 2);
                    case 4:
                        return tileSheet.getTile(3, 3);
                    default:
                        return tileSheet.getTile(3, 4);
                }
            case ROAD:
                // Set road tile based on connections to other roads
                if (north && south && east && west)
                    return tileSheet.getTile(1, 7);
                if (north && south && west)
                    return tileSheet.getTile(2, 8);
                if (north && south && east)
                    return tileSheet.getTile(2, 9);
                if (south && east && west)
                    return tileSheet.getTile(1, 8);
                if (north && east && west)
                    return tileSheet.getTile(1, 9);
                if (north && south)
                    return tileSheet.getTile(1, 5);
                if (east && west)
                    return tileSheet.getTile(1, 6);
                if (south && east)
                    return tileSheet.getTile(2, 5);
                if (south && west)
                    return tileSheet.getTile(2, 6);
                if (north && east)
                    return tileSheet.getTile(3, 5);
                if (north && west)
                    return tileSheet.getTile(3, 6);
                if (west)
                    return tileSheet.getTile(2, 7);
                if (east)
                    return tileSheet.getTile(3, 7);
                if (north)
                    return tileSheet.getTile(3, 8);
                if (south)
                    return tileSheet.getTile(3, 9);
                return tileSheet.getTile(1, 7);
            case MOUNTAIN:
                return tileSheet.getTile(6,4);
        }
        return null;
    }

}
