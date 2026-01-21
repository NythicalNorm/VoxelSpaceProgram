package com.nythicalnorm.voxelspaceprogram.storage;

import com.nythicalnorm.voxelspaceprogram.SolarSystem;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.StarBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

import java.util.Map;

public class VSPDataPackManager {
    private static PlanetDataResolver.PlanetLoadedData planetLoadedData;
    private static final String VSPCommonData = "vsp_common_data";

    public static void planetDatapackLoaded(PlanetDataResolver.PlanetLoadedData pPlanetLoadedData) {
        if (SolarSystem.getInstance().isEmpty()) {
            planetLoadedData = pPlanetLoadedData;
        } else {
            VoxelSpaceProgram.log("Datapack reloaded, but planets can't be changed during runtime with datapacks.");
        }
    }

    public static void loadServerDataAndStartSolarSystem(MinecraftServer pServer) {
        Map<OrbitId, CelestialBody> AllPlanetaryBodies = new Object2ObjectOpenHashMap<>();
        Map<OrbitId, EntitySpacecraftBody > AllSpacecraftBodies = new Object2ObjectOpenHashMap<>();
        Map<ResourceKey<Level>, CelestialBody> PlanetDimensions = new Object2ObjectOpenHashMap<>();
        StarBody rootStar;

        if (planetLoadedData != null) {
            rootStar = planetLoadedData.rootStar();
            loadPlanetData(AllPlanetaryBodies, PlanetDimensions);
        } else {
            throw new IllegalStateException("Can't start Solar System server because no planet data is loaded");
        }
        // load spacecraft data here

        PlanetsProvider planetsProvider = new PlanetsProvider(AllPlanetaryBodies, AllSpacecraftBodies, PlanetDimensions, rootStar);
        new SolarSystem(pServer, planetsProvider);
    }

    public static VSPCommonSaveData createOrLoadSaveData(MinecraftServer server) {
       return server.overworld().getDataStorage().computeIfAbsent(VSPCommonSaveData::load, VSPCommonSaveData::new, VSPCommonData);
    }

    private static void loadPlanetData(Map<OrbitId, CelestialBody> pAllPlanetaryBodies, Map<ResourceKey<Level>, CelestialBody> pPlanetDimensions) {
        for (Map.Entry<String, CelestialBody> entry : planetLoadedData.tempPlanetaryBodyMap().entrySet()) {
            String[] childPlanets = planetLoadedData.tempChildPlanetsMap().get(entry.getKey());
            for (String planet : childPlanets) {
                entry.getValue().addChildBody(planetLoadedData.tempPlanetaryBodyMap().get(planet));
            }

            if (entry.getValue().getDimension() != null) {
                pPlanetDimensions.put(entry.getValue().getDimension(), entry.getValue());
            }

            pAllPlanetaryBodies.put(entry.getValue().getOrbitId(), entry.getValue());
        }

        planetLoadedData = null;
    }
}
