package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies;

public interface CelestialBodyAccessor {
    boolean isPlanet();
    CelestialBody getCelestialBody();
    void setCelestialBody(CelestialBody celestialBody);
}
