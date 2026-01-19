package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies;

import com.nythicalnorm.voxelspaceprogram.solarsystem.CelestialBodyTypes;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBodyType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.*;

public class StarBody extends PlanetaryBody {
    public StarBody() {
        super();
    }

    @Override
    public OrbitalBodyType<? extends Orbit> getType() {
        return CelestialBodyTypes.STAR_BODY;
    }

    public void simulatePlanets(long currentTime) {
        this.simulatePropagate(currentTime, new Vector3d(0d, 0d, 0d), this.getMass());
    }

    public void initCalcs() {
        this.setSphereOfInfluence(Double.POSITIVE_INFINITY);
        this.calculateOrbitalPeriod();
        super.UpdateSOIs();
        this.parent = null;
        this.setChildrenParents();
    }
}
