package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies;

public interface PlanetAccessor {
    boolean isPlanet();
    CelestialBody getCelestialBody();
    void setCelestialBody(CelestialBody celestialBody);
}
