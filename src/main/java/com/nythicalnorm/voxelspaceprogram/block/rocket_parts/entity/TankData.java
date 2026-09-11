package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity;

import org.joml.Vector3d;
import org.joml.Vector3i;

public class TankData {
    private final Vector3i tankSize;
    private final Vector3i  tankStartPos;
    private final Vector3i  tankEndPos;

    public TankData(Vector3i tankSize, Vector3i tankStartPos, Vector3i tankEndPos) {
        this.tankSize = tankSize;
        this.tankStartPos = tankStartPos;
        this.tankEndPos = tankEndPos;
    }

    public double getTankVolume() {
        return tankSize.x() * tankSize.y() * tankSize.z();
    }

    public Vector3d getTankCenter() {
        double xCenter = (double) (tankStartPos.x() + tankEndPos.x()) / 2.0d;
        double yCenter = (double) (tankStartPos.y() + tankEndPos.y()) / 2.0d;
        double zCenter = (double) (tankStartPos.z() + tankEndPos.z()) / 2.0d;
        return new Vector3d(xCenter, yCenter, zCenter);
    }
}
