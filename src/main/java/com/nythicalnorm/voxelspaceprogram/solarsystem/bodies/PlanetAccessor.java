package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies;

public interface PlanetAccessor {
    boolean isPlanet();
    PlanetaryBody getPlanetaryBody();
    void setPlanetaryBody(PlanetaryBody planetaryBody);
}
