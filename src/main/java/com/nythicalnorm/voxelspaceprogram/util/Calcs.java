package com.nythicalnorm.voxelspaceprogram.util;

import com.nythicalnorm.voxelspaceprogram.solarsystem.planet.PlanetaryBody;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;

public class Calcs {
    public static Vector3d planetDimPosToNormalizedVector(Vec3 pos, double planetRadius, Quaternionf planetRot, boolean isNormalized) {
        double cellSize = getSquareCellSize(planetRadius);
        double halfCellSize = cellSize*0.5d;

        int xCell = getCellIndex(cellSize, pos.x);
        int zCell = getCellIndex(cellSize, pos.z);

        xCell = Mth.clamp(xCell,-1, 2);

        if (xCell == 0) {
            zCell = Mth.clamp(zCell,-1, 1);
        }
        else {
            zCell = 0;
        }
        double xWithinCell = pos.x - xCell*cellSize;
        double zWithinCell = pos.z - zCell*cellSize;

        xWithinCell = Mth.clamp(xWithinCell, -halfCellSize, halfCellSize);
        zWithinCell = Mth.clamp(zWithinCell, -halfCellSize, halfCellSize);

        int QuadId = xCell + 1;
        if (xCell == 0) {
            if (zCell == 1){
                QuadId = 4;
            }
            else if (zCell == -1) {
                QuadId = 5;
            }
        }
        double radius = 1d;
        if (!isNormalized) {
            radius = planetRadius + pos.y; // + 10000000
        }
        Vector3d quadSpherePos =  getQuadPlanettoSquarePos(zWithinCell, xWithinCell, halfCellSize, QuadId, radius);

        quadSpherePos.rotate(new Quaterniond(planetRot.x, planetRot.y, planetRot.z, planetRot.w));
        return quadSpherePos;
    }

    public static double getSquareCellSize(double planetRadius) {
        return Math.PI*planetRadius*0.5d;
    }

    public static int getCellIndex(double cellSize, double posAxis) {
        return (int)Math.floor((posAxis + (cellSize/2)) / cellSize);
    }

    public static Vector3d getQuadPlanettoSquarePos(double sidesUpIter, double sidesRightIter, double MaxPerSide, int squareSide, double radius) {
        double sidesrightP = sidesRightIter/MaxPerSide;
        //negative correction because north is negative z in mc
        double sidesupP = -sidesUpIter/MaxPerSide;
        Vector3d squarePos = new Vector3d();
//        sidesupP = (sidesupP - 0.5f)*2f;
//        sidesrightP = (sidesrightP - 0.5f)*2f;

        squarePos = switch (squareSide) {
            case 0 -> new Vector3d(sidesrightP, sidesupP, 1f);
            case 1 -> new Vector3d(1f, sidesupP, -sidesrightP);
            case 2 -> new Vector3d(-sidesrightP, sidesupP, -1);
            case 3 -> new Vector3d(-1f, sidesupP, sidesrightP);
            case 5 -> new Vector3d(-sidesupP, 1f, -sidesrightP);
            case 4 -> new Vector3d(sidesupP, -1f, -sidesrightP);
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


    //client side only
    public static Vector3f getUpVectorForPlanetRot(Vector3f playerRelativePos, PlanetaryBody planet) {
        Vector3f upDir = new Vector3f(0f,-1f,0f);
        AxisAngle4f northPole = planet.getNorthPoleDir();
        upDir = new Vector3f(northPole.x, northPole.z, northPole.y);

        upDir.normalize();
        Quaternionf rot = new Quaternionf(new AxisAngle4f(Mth.HALF_PI, 1f, 0f, 0f));

        upDir.rotate(rot);
        return upDir;
    }

    public static Quaterniond quaternionFtoD(Quaternionf quaternionf) {
        return new Quaterniond(quaternionf.x, quaternionf.y, quaternionf.z, quaternionf.w);
    }

    public static long TimePerTickToTimePerMilliTick(long timePassPerSec) {
        return timePassPerSec * 1000;
    }

    public static long TimePerTickToTimePerMilliTick(double timePassPerSec) {
        return (long) (timePassPerSec * 1000);
    }

    public static double TimePerMilliTickToTick(double timePassPerTick) {
        return (double) timePassPerTick / 1000d;
    }

    public static double timeLongToDouble(long diff) {
       return (double) diff / 20000;
    }

    public static long timeDoubleToLong(double diff) {
        return (long) (diff * 20000);
    }

//    public static boolean IsNaN(Quaternionf q) {
//        return Double.isNaN(q.w()) ||
//                Double.isNaN(q.x()) ||
//                Double.isNaN(q.y()) ||
//                Double.isNaN(q.z());
//    }
}
