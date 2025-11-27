package com.nythicalnorm.nythicalSpaceProgram.util;

import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetaryBody;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class Calcs {
    public static Vector3d planetDimPosToNormalizedVector(Vec3 pos, PlanetaryBody planet, boolean isNormalized) {
        double cellSize = 100; //Math.PI*planet.getRadius()*0.5d;
        double halfCellSize = cellSize*0.5d;

        int xCell = (int)Math.floor((pos.x + halfCellSize) / cellSize);
        int zCell = (int)Math.floor((pos.z + halfCellSize) / cellSize);

        double xWithinCell = (pos.x + halfCellSize) % cellSize;
        double zWithinCell = (pos.z + halfCellSize) % cellSize;

        if (xCell > 2) {
            xCell = 2;
            xWithinCell = cellSize;
        }
        else if (xCell < -1) {
            xCell = -1;
            xWithinCell = -cellSize;
        }

        if (zCell > 1) {
            zCell = 1;
            zWithinCell = cellSize;
        }
        else if (zCell < -1) {
            zCell = -1;
            zWithinCell = -cellSize;
        }

        xCell = clamp(-1,2, xCell);
        zCell = clamp(-1,1, zCell);

        int QuadId = xCell + 1;
        if (xCell == 0) {
            if (zCell == 1){
                QuadId = 4;
            }
            else if (zCell == -1) {
                QuadId = 5;
            }
        }
        double radius = 1;
        if (!isNormalized) {
            radius = planet.getRadius() + 10000000 + pos.y;
        }

        return getQuadPlanettoSquarePos(zWithinCell, xWithinCell, cellSize, QuadId, radius);
    }

    public static Vector3d getQuadPlanettoSquarePos(double sidesUpIter, double sidesRightIter, double MaxPerSide, int squareSide, double radius) {
        double sidesrightP = (sidesRightIter)/MaxPerSide;
        double sidesupP = (sidesUpIter)/MaxPerSide;
        Vector3d squarePos = new Vector3d();
        sidesupP = (sidesupP - 0.5f)*2f;
        sidesrightP = (sidesrightP - 0.5f)*2f;

        squarePos = switch (squareSide) {
            case 0 -> new Vector3d(sidesrightP, sidesupP, 1f);
            case 1 -> new Vector3d(1f, sidesupP, -sidesrightP);
            case 2 -> new Vector3d(-sidesrightP, sidesupP, -1);
            case 3 -> new Vector3d(-1f, sidesupP, sidesrightP);
            case 4 -> new Vector3d(1f+sidesupP, 1f, -sidesrightP);
            case 5 -> new Vector3d(1f+sidesupP, -1f, -sidesrightP);
            default -> squarePos;
        };

        squarePos.normalize();
        squarePos.mul(radius);
        return squarePos;
    }

    public static Vector3f getQuadSquarePos(float sidesUpIter, float sidesRightIter, float MaxPerSide, int squareSide, float radius) {
        float sidesrightP = sidesRightIter/MaxPerSide;
        float sidesupP = sidesUpIter/MaxPerSide;
        Vector3f squarePos = new Vector3f();

        squarePos = switch (squareSide) {
            case 0 -> new Vector3f(sidesrightP, sidesupP, 1f);
            case 1 -> new Vector3f(1f, sidesupP, -sidesrightP);
            case 2 -> new Vector3f(-sidesrightP, sidesupP, -1);
            case 3 -> new Vector3f(-1f, sidesupP, sidesrightP);
            case 4 -> new Vector3f(-sidesrightP, 1f, sidesupP);
            case 5 -> new Vector3f(sidesrightP, -1f, sidesupP);
            default -> squarePos;
        };
        squarePos.normalize();
        squarePos.mul(radius);
        return squarePos;
    }

    private static int clamp (int min, int max, int val) {
        if (val > max) {
            return max;
        }
        else if (val < min) {
            return max;
        }
        else {
            return val;
        }
    }
}
