package com.nythicalnorm.voxelspaceprogram.storage;

import com.nythicalnorm.voxelspaceprogram.SolarSystem;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetDataResolver;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.StarBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
        Map<OrbitId, PlanetaryBody> AllPlanetaryBodies = new Object2ObjectOpenHashMap<>();
        Map<OrbitId, EntitySpacecraftBody > AllSpacecraftBodies = new Object2ObjectOpenHashMap<>();
        Map<ResourceKey<Level>, PlanetaryBody> PlanetDimensions = new Object2ObjectOpenHashMap<>();
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

    private static void loadPlanetData(Map<OrbitId, PlanetaryBody> pAllPlanetaryBodies, Map<ResourceKey<Level>, PlanetaryBody> pPlanetDimensions) {
        for (Map.Entry<String, PlanetaryBody> entry : planetLoadedData.tempPlanetaryBodyMap().entrySet()) {
            String[] childPlanets = planetLoadedData.tempChildPlanetsMap().get(entry.getKey());
            for (String planet : childPlanets) {
                entry.getValue().addChildBody(planetLoadedData.tempPlanetaryBodyMap().get(planet));
            }

            if (planetLoadedData.tempDimensionsMap().containsKey(entry.getKey())) {
                ResourceLocation dimensionResourceLoc =  ResourceLocation.parse(planetLoadedData.tempDimensionsMap().get(entry.getKey()));
                ResourceKey<Level> dimensionLevelKey = ResourceKey.create(Registries.DIMENSION, dimensionResourceLoc);
                pPlanetDimensions.put(dimensionLevelKey, entry.getValue());
                entry.getValue().setDimension(dimensionLevelKey);
            }
            pAllPlanetaryBodies.put(entry.getValue().getOrbitId(), entry.getValue());
        }

        planetLoadedData = null;
    }
}
