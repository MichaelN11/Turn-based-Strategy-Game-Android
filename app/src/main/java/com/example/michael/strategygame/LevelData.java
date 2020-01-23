package com.example.michael.strategygame;

import com.example.michael.strategygame.Units.Artillery;
import com.example.michael.strategygame.Units.Infantry;
import com.example.michael.strategygame.Units.RPG;
import com.example.michael.strategygame.Units.Tank;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LevelData {
    private List<Unit> unitList;
    private String terrainString;
    private int numRows, numCols;

    public LevelData(List<Unit> units, String terrain, int rows, int cols) {
        unitList = units;
        this.terrainString = terrain;
        numCols = cols;
        numRows = rows;
    }

    public List<Unit> getUnitList() {
        return unitList;
    }

    public String getTerrainString() {
        return terrainString;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public static LevelData getLevelData(int level) {
        LinkedList<Unit> units = new LinkedList<Unit>();
        String terrain = "";
        int rows = 0, cols = 0;

        switch(level) {
            case 1:
                rows = 11;
                cols = 12;
                terrain =
                                "WWWWWWWWEEMM" +
                                "WWWWWWWEEEEF" +
                                "WWEEEEFEEEEE" +
                                "EEEEMFFFMEEE" +
                                "RRRRRRRRRRRR" +
                                "EERMMMMFFFEE" +
                                "EERMMMFFFEEE" +
                                "MERRRRRRREEE" +
                                "EEEEFFEERRRR" +
                                "FFEEEEEEEEFM" +
                                "WWWFEEEEEEFM";
                units.add(new Infantry(1,4,2));
                units.add(new Infantry(1,9,2));
                units.add(new Infantry(1,5,4));
                units.add(new Infantry(1,10,4));
                units.add(new Tank(1, 5, 1));
                units.add(new RPG(1,5,3));
                units.add(new RPG(1,7,3));
                units.add(new RPG(1,6,4));
                units.add(new Tank(2,9,12));
                units.add(new RPG(2, 5, 12));
                units.add(new RPG(2, 8, 10));
                units.add(new RPG(2, 6, 10));
                units.add(new Infantry(2,5,9));
                units.add(new Infantry(2,3,9));
                units.add(new Infantry(2,4,11));
                break;
            case 2:
                rows = 12;
                cols = 10;
                terrain =
                        "WWEEFFFREM" +
                        "EEEEEFFREM" +
                        "FFEEMMMREE" +
                        "FFEEEFFREF" +
                        "EEFRRRRREE" +
                        "WWWRWWWEWW" +
                        "WWWRWWWMWW" +
                        "WWWRWWWMMW" +
                        "EFFRMMFEEE" +
                        "EERREEEEEE" +
                        "ERREEEFFEM" +
                        "EREEEFFFEE";
                units.add(new Infantry(1,9,4));
                units.add(new Infantry(1,10,2));
                units.add(new Infantry(1,9,7));
                units.add(new Tank(1, 10, 3));
                units.add(new Tank(1, 10, 4));
                units.add(new Artillery(1, 12, 2));
                units.add(new RPG(1,9,5));
                units.add(new RPG(1,10,5));
                units.add(new Artillery(2, 4, 6));
                units.add(new Tank(2,3,8));
                units.add(new Tank(2,2,8));
                units.add(new RPG(2, 3, 7));
                units.add(new RPG(2, 4, 7));
                units.add(new Infantry(2,5,8));
                units.add(new Infantry(2,5,6));
                units.add(new Infantry(2,4,3));
                break;
            case 3:
                rows = 12;
                cols = 12;
                terrain =
                                "MMMFMMEEEFFW" +
                                "MRRRRRRRRRRW" +
                                "ERMEEMEEEERW" +
                                "ERFWWWWWWERE" +
                                "MREWWWWWWFRE" +
                                "RREWWWWWWERE" +
                                "FREWWWWWWERR" +
                                "FREWWWWWWERF" +
                                "ERMWWWWWWFRF" +
                                "ERMEEEEFFFRF" +
                                "FRRRRRRRRRRW" +
                                "FFEEEEFFFFFF";
                units.add(new Infantry(1,5,3));
                units.add(new Infantry(1,10,2));
                units.add(new Infantry(1,7,3));
                units.add(new Tank(1, 5, 2));
                units.add(new Tank(1, 7, 2));
                units.add(new Tank(1, 6, 1));
                units.add(new Artillery(1, 3, 1));
                units.add(new Artillery(1, 8, 2));
                units.add(new Artillery(1, 4, 3));
                units.add(new RPG(1,7,1));
                units.add(new RPG(1,4,1));
                units.add(new Artillery(2, 1, 11));
                units.add(new Artillery(2, 10, 10));
                units.add(new Artillery(2, 5, 10));
                units.add(new Tank(2,4,11));
                units.add(new Tank(2,10,11));
                units.add(new Tank(2,6,11));
                units.add(new Tank(2,8,10));
                units.add(new RPG(2, 1, 10));
                units.add(new RPG(2, 10, 8));
                units.add(new Infantry(2,11,10));
                units.add(new Infantry(2,2,11));
                units.add(new Infantry(2,4,12));
                units.add(new Infantry(2,9,10));
                break;
            case 4:
                rows = 14;
                cols = 14;
                terrain =
                                "MWWMMMRMMMMFFM" +
                                "MFFMMMRMMMMMMM" +
                                "MFFFMEREEEEFMM" +
                                "MMEEEERREEEMWW" +
                                "MMMEEEEREEEEMF" +
                                "RRREEEMRFEEEMM" +
                                "MMRRRRRRRREEMM" +
                                "MMEEEEERERRRRR" +
                                "FMMEEEEREMEMMM" +
                                "FMMEFEEREEEMMM" +
                                "FMMFFEEREEEMMM" +
                                "MMFFFEEREEFFMM" +
                                "MMMMMMMRMMMMFF" +
                                "FFWMMMMRMMMMFM";
                units.add(new Infantry(1,2,8));
                units.add(new Infantry(1,7,2));
                units.add(new Infantry(1,9,13));
                units.add(new Tank(1, 2, 7));
                units.add(new Tank(1, 6, 2));
                units.add(new Tank(1, 8, 13));
                units.add(new Artillery(1, 1, 7));
                units.add(new Artillery(1, 6, 1));
                units.add(new Artillery(1, 8, 14));
                units.add(new RPG(1,5,2));
                units.add(new RPG(1,7,13));
                units.add(new Artillery(2, 14, 8));
                units.add(new Artillery(2, 13, 8));
                units.add(new Tank(2,12,8));
                units.add(new Tank(2,12,7));
                units.add(new Tank(2,12,9));
                units.add(new Tank(2,7,8));
                units.add(new Tank(2,13,7));
                units.add(new RPG(2, 13, 9));
                units.add(new RPG(2, 13, 6));
                units.add(new Infantry(2,12,6));
                units.add(new Infantry(2,13,12));
                units.add(new Infantry(2,12,3));
                units.add(new Infantry(2,8,8));
                units.add(new Infantry(2,7,7));

                break;
            case 5:
                rows = 12;
                cols = 11;
                terrain =
                        "FFWWWREEEEE" +
                                "FFFEEREFFFF" +
                                "MFMEERRRRRR" +
                                "WMMMEWWWWEE" +
                                "WMFEEWWWWEE" +
                                "RRRRRRRRRFF" +
                                "MEREEWWWRFF" +
                                "FMREEEFMRRR" +
                                "FMRREEFFMMM" +
                                "RFERRRREMMF" +
                                "RFERFFREEEE" +
                                "RFERFFREEEE";
                units.add(new Infantry(1,1,8));
                units.add(new Infantry(1,2,2));
                units.add(new Infantry(1,1,7));
                units.add(new Tank(1, 4, 5));
                units.add(new Artillery(1, 3, 5));
                units.add(new RPG(1,5,3));
                units.add(new Infantry(2,10,3));
                units.add(new Infantry(2,10,6));
                units.add(new RPG(2, 11, 4));
                units.add(new Artillery(2, 9, 4));
                units.add(new Tank(2,9,8));
                break;
            case 6:
                rows = 12;
                cols = 11;
                terrain =
                        "FFWWWREEEEE" +
                                "FFFEEREFFFF" +
                                "MFMEERRRRRR" +
                                "WMMMEWWWWEE" +
                                "WMFEEWWWWEE" +
                                "RRRRRRRRRFF" +
                                "MEREEWWWRFF" +
                                "FMREEEFMRRR" +
                                "FMRREEFFMMM" +
                                "RFERRRREMMF" +
                                "RFERFFREEEE" +
                                "RFERFFREEEE";
                units.add(new Infantry(1,1,8));
                units.add(new Infantry(1,2,2));
                units.add(new Infantry(1,1,7));
                units.add(new Tank(1, 4, 5));
                units.add(new Artillery(1, 3, 5));
                units.add(new RPG(1,5,3));
                units.add(new Infantry(2,10,3));
                units.add(new Infantry(2,10,6));
                units.add(new RPG(2, 11, 4));
                units.add(new Artillery(2, 9, 4));
                units.add(new Tank(2,9,8));
                break;
            case 7:
                rows = 12;
                cols = 11;
                terrain =
                        "FFWWWREEEEE" +
                                "FFFEEREFFFF" +
                                "MFMEERRRRRR" +
                                "WMMMEWWWWEE" +
                                "WMFEEWWWWEE" +
                                "RRRRRRRRRFF" +
                                "MEREEWWWRFF" +
                                "FMREEEFMRRR" +
                                "FMRREEFFMMM" +
                                "RFERRRREMMF" +
                                "RFERFFREEEE" +
                                "RFERFFREEEE";
                units.add(new Infantry(1,1,8));
                units.add(new Infantry(1,2,2));
                units.add(new Infantry(1,1,7));
                units.add(new Tank(1, 4, 5));
                units.add(new Artillery(1, 3, 5));
                units.add(new RPG(1,5,3));
                units.add(new Infantry(2,10,3));
                units.add(new Infantry(2,10,6));
                units.add(new RPG(2, 11, 4));
                units.add(new Artillery(2, 9, 4));
                units.add(new Tank(2,9,8));
                break;
        }

        return new LevelData(units, terrain, rows, cols);
    }
}
