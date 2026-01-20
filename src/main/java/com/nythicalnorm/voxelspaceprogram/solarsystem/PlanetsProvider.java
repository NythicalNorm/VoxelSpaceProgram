package com.nythicalnorm.voxelspaceprogram.solarsystem;

import com.nythicalnorm.voxelspaceprogram.dimensions.SpaceDimension;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.*;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.*;

public class PlanetsProvider {
    private final Map<ResourceKey<Level>, PlanetaryBody> planetDimensions;
    private final Map<OrbitId, PlanetaryBody> allPlanetaryBodies;
    private final Map<OrbitId, EntitySpacecraftBody> allSpacecraftBodies;
    private final StarBody rootStar;

    public PlanetsProvider(Map<OrbitId, PlanetaryBody> pAllPlanetaryBodies, Map<OrbitId, EntitySpacecraftBody> pAllSpacecraftBodies, Map<ResourceKey<Level>, PlanetaryBody> pPlanetDimensions, StarBody rootStar) {
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

    public Map<ResourceKey<Level>, PlanetaryBody> getPlanetDimensions() {
        return planetDimensions;
    }

    public @Nullable PlanetaryBody getPlanet(String key) {
        for (PlanetaryBody planetaryBody : allPlanetaryBodies.values()) {
            if (planetaryBody.getName().equals(key)) {
                return planetaryBody;
            }
        }
        return null;
    }

    public PlanetaryBody getPlanet(OrbitId planetID) {
        return allPlanetaryBodies.get(planetID);
    }

    public Orbit getSpacecraftOrbit(OrbitId spacecraftBodyAddress) {
        return allSpacecraftBodies.get(spacecraftBodyAddress);
    }

    public void playerChangeOrbitalSOIs(Orbit spacecraftBody, OrbitId newParentID, OrbitalElements orbitalElementsNew) {
        PlanetaryBody newOrbitPlanet = getPlanet(newParentID);

        orbitalElementsNew.setOrbitalPeriod(newOrbitPlanet.getMass());
        spacecraftBody.setOrbitalElements(orbitalElementsNew);

        //removing the old reference to the object
        spacecraftBody.removeParent();
        // adding reference to new object
        newOrbitPlanet.addChildBody(spacecraftBody);
    }

    // Need to split off this into its own data packet
    public void playerJoinedOrbital(OrbitId newParentID, EntitySpacecraftBody OrbitalDataNew) {
        Orbit newOrbitPlanet = getPlanet(newParentID);

        if (newOrbitPlanet instanceof PlanetaryBody plnt) {
            OrbitalDataNew.getOrbitalElements().setOrbitalPeriod(plnt.getMass());
            //temp default Rotation
            OrbitalDataNew.setRotation(new Quaternionf());
            plnt.addChildBody(OrbitalDataNew);
        }
    }

    public List<String> getAllPlanetNames() {
        List<String> planetNames = new ArrayList<>();
        for (PlanetaryBody planetaryBody : allPlanetaryBodies.values()) {
            planetNames.add(planetaryBody.getName());
        }
        return planetNames;
    }

    public Map<OrbitId, PlanetaryBody> getAllPlanetaryBodies() {
        return allPlanetaryBodies;
    }

    public List<PlanetaryBody> getAllPlanetOrbitsList() {
        return allPlanetaryBodies.values().stream().toList();
    }

    public PlanetaryBody getOverworldPlanet() {
        return planetDimensions.get(Level.OVERWORLD);
    }

    public StarBody getRootStar() {
        return rootStar;
    }

    public PlanetaryBody getDimensionPlanet(ResourceKey<Level> dim) {
        return planetDimensions.get(dim);
    }

    public boolean isDimensionSpace(ResourceKey<Level> dim) {
        return dim == SpaceDimension.SPACE_LEVEL_KEY;
    }
}
