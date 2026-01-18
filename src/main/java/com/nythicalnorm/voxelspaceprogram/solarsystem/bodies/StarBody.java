package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies;

import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.*;

public class StarBody extends PlanetaryBody {
    public StarBody() {

    }

    public StarBody(String name, PlanetAtmosphere effects, @Nullable HashMap<OrbitId, Orbit> childBody, double radius, double mass) {
        super(name, null, null, effects, childBody, radius, mass, 0f, 0, 0);
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
