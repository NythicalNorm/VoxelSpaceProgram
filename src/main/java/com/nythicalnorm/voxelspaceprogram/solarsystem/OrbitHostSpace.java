package com.nythicalnorm.voxelspaceprogram.solarsystem;

import com.nythicalnorm.voxelspaceprogram.spacecraft.EntityOrbitBody;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class OrbitHostSpace {
    private final OrbitId orbitIdOfHost;
    private final Vector3d originPos;
    private @Nullable EntityOrbitBody hostBody;
    private List<Entity> nonHostEntities;
    private List<Long> nonHostShipsIDS;

    public OrbitHostSpace(OrbitId orbitIdOfHost, Vector3d originPos, EntityOrbitBody entityOrbitBody) {
        this(orbitIdOfHost, originPos);
        this.hostBody = entityOrbitBody;
    }

    public OrbitHostSpace(OrbitId orbitIdOfHost, Vector3d originPos) {
        this.orbitIdOfHost = orbitIdOfHost;
        this.originPos = originPos;
        nonHostEntities = new ArrayList<>();
        nonHostShipsIDS = new ArrayList<>();
    }

    public void initSpace(EntityOrbitBody orbitBody) {
        this.hostBody = orbitBody;
    }

    public Vector3d getOriginPos() {
        return originPos;
    }

    public OrbitId getOrbitIdOfHost() {
        return orbitIdOfHost;
    }
}
