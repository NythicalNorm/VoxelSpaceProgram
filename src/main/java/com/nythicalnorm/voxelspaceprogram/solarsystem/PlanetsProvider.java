package com.nythicalnorm.voxelspaceprogram.solarsystem;

import com.nythicalnorm.voxelspaceprogram.dimensions.SpaceDimension;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.*;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.*;

public class PlanetsProvider {
    private final Map<ResourceKey<Level>, CelestialBody> planetDimensions;
    private final Map<OrbitId, CelestialBody> allPlanetaryBodies;
    private final Map<OrbitId, EntitySpacecraftBody> allSpacecraftBodies;
    private final StarBody rootStar;

    public PlanetsProvider(Map<OrbitId, CelestialBody> pAllPlanetaryBodies, Map<OrbitId, EntitySpacecraftBody> pAllSpacecraftBodies, Map<ResourceKey<Level>, CelestialBody> pPlanetDimensions, StarBody rootStar) {
        this.allPlanetaryBodies = pAllPlanetaryBodies;
        this.allSpacecraftBodies = pAllSpacecraftBodies;
        this.planetDimensions = pPlanetDimensions;
        this.rootStar = rootStar;
        rootStar.initCalcs();
    }

    public void UpdatePlanets(long currentTime) {
        rootStar.simulatePlanets(currentTime);
    }

    public Map<OrbitId, EntitySpacecraftBody> getAllSpacecraftBodies() {
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
        CelestialBody newOrbitPlanet = getPlanet(newParentID);

        orbitalElementsNew.setOrbitalPeriod(newOrbitPlanet.getMass());
        spacecraftBody.setOrbitalElements(orbitalElementsNew);

        //removing the old reference to the object
        spacecraftBody.removeParent();
        // adding reference to new object
        newOrbitPlanet.addChildBody(spacecraftBody);
    }

    // Need to split off this into its own data packet
    public void playerJoinedOrbital(OrbitId newParentID, EntitySpacecraftBody OrbitalDataNew) {
        OrbitalBody newOrbitPlanet = getPlanet(newParentID);

        if (newOrbitPlanet instanceof PlanetaryBody plnt) {
            OrbitalDataNew.getOrbitalElements().setOrbitalPeriod(plnt.getMass());
            //temp default Rotation
            OrbitalDataNew.setRotation(new Quaternionf());
            plnt.addChildBody(OrbitalDataNew);
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
}
