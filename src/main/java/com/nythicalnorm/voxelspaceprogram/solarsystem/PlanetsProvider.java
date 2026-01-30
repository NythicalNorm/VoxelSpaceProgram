package com.nythicalnorm.voxelspaceprogram.solarsystem;

import com.nythicalnorm.voxelspaceprogram.dimensions.SpaceDimension;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.*;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.star.StarBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntityOrbitBody;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class PlanetsProvider {
    private final Map<ResourceKey<Level>, CelestialBody> planetDimensions;
    private final Map<OrbitId, CelestialBody> allPlanetaryBodies;
    private final ConcurrentMap<OrbitId, EntityOrbitBody> allSpacecraftBodies;
    private final StarBody rootStar;

    public PlanetsProvider(Map<OrbitId, CelestialBody> pAllPlanetaryBodies, ConcurrentMap<OrbitId, EntityOrbitBody> pAllSpacecraftBodies, Map<ResourceKey<Level>, CelestialBody> pPlanetDimensions, StarBody rootStar) {
        this.allPlanetaryBodies = pAllPlanetaryBodies;
        this.allSpacecraftBodies = pAllSpacecraftBodies;
        this.planetDimensions = pPlanetDimensions;
        this.rootStar = rootStar;
        rootStar.initCalcs();
    }

    public void UpdatePlanets(long currentTime) {
        rootStar.simulatePlanets(currentTime);
    }

    public Map<OrbitId, EntityOrbitBody> getAllSpacecraftBodies() {
        return allSpacecraftBodies;
    }

    public Map<ResourceKey<Level>, CelestialBody> getPlanetDimensions() {
        return planetDimensions;
    }

    public @Nullable CelestialBody getPlanet(String key) {
        for (CelestialBody planetaryBody : allPlanetaryBodies.values()) {
            if (planetaryBody.getName().equals(key)) {
                return planetaryBody;
            }
        }
        return null;
    }

    public CelestialBody getPlanet(OrbitId planetID) {
        return allPlanetaryBodies.get(planetID);
    }

    public OrbitalBody getSpacecraftOrbit(OrbitId spacecraftBodyAddress) {
        return allSpacecraftBodies.get(spacecraftBodyAddress);
    }

    public void playerChangeOrbitalSOIs(OrbitalBody spacecraftBody, OrbitId newParentID, OrbitalElements orbitalElementsNew) {
        playerChangeOrbitalSOIs(spacecraftBody, getPlanet(newParentID), orbitalElementsNew);
    }

    public void playerChangeOrbitalSOIs(OrbitalBody spacecraftBody, CelestialBody newOrbitPlanet, OrbitalElements orbitalElementsNew) {

        orbitalElementsNew.setOrbitalPeriod(newOrbitPlanet.getMass());
        spacecraftBody.setOrbitalElements(orbitalElementsNew);

        //removing the old reference to the object
        spacecraftBody.removeParent();
        // adding reference to new object
        newOrbitPlanet.addChildBody(spacecraftBody);
    }

    public void playerJoinedOrbital(OrbitId newParentID, OrbitalBody OrbitalDataNew) {
        playerJoinedOrbital(getPlanet(newParentID), OrbitalDataNew);
    }

    public void playerJoinedOrbital(CelestialBody newOrbitPlanet, OrbitalBody OrbitalDataNew) {
        if (newOrbitPlanet != null) {
            OrbitalDataNew.getOrbitalElements().setOrbitalPeriod(newOrbitPlanet.getMass());
            //temp default Rotation
            OrbitalDataNew.setRotation(new Quaternionf());
            newOrbitPlanet.addChildBody(OrbitalDataNew);

            if (OrbitalDataNew instanceof EntityOrbitBody entitySpacecraftBody) {
                getAllSpacecraftBodies().putIfAbsent(entitySpacecraftBody.getOrbitId(), entitySpacecraftBody);
            }
        }
    }

    public List<String> getAllPlanetNames() {
        List<String> planetNames = new ArrayList<>();
        for (CelestialBody planetaryBody : allPlanetaryBodies.values()) {
            planetNames.add(planetaryBody.getName());
        }
        return planetNames;
    }

    public Map<OrbitId, CelestialBody> getAllPlanetaryBodies() {
        return allPlanetaryBodies;
    }

    public List<CelestialBody> getAllPlanetOrbitsList() {
        return allPlanetaryBodies.values().stream().toList();
    }

    public CelestialBody getOverworldPlanet() {
        return planetDimensions.get(Level.OVERWORLD);
    }

    public StarBody getRootStar() {
        return rootStar;
    }

    public CelestialBody getDimensionPlanet(ResourceKey<Level> dim) {
        return planetDimensions.get(dim);
    }


    public boolean isDimensionSpace(ResourceKey<Level> dim) {
        return dim == SpaceDimension.SPACE_LEVEL_KEY;
    }

    public boolean isDimensionPlanet(ResourceKey<Level> level) {
        return planetDimensions.containsKey(level);
    }
}
