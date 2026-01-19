package com.nythicalnorm.voxelspaceprogram.network.orbitaldata;

import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.StarBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientSyncer {
    public static void StartClientPacket(EntitySpacecraftBody playerData, List<PlanetaryBody> planetaryBodyList) {
        Map<OrbitId, PlanetaryBody> AllPlanetaryBodies = new Object2ObjectOpenHashMap<>();
        Map<OrbitId, EntitySpacecraftBody > AllSpacecraftBodies = new Object2ObjectOpenHashMap<>();
        Map<ResourceKey<Level>, PlanetaryBody> PlanetDimensions = new Object2ObjectOpenHashMap<>();
        StarBody rootStar = null;

        for (PlanetaryBody planetaryBody : planetaryBodyList) {
            if (planetaryBody instanceof StarBody starBody) {
                rootStar = starBody;
            }
            if (planetaryBody.getDimension() != null) {
                PlanetDimensions.put(planetaryBody.getDimension(), planetaryBody);
            }
            AllPlanetaryBodies.put(planetaryBody.getOrbitId(), planetaryBody);
        }
        if (rootStar == null) {
            throw new IllegalStateException ("can't start client Solar system without a host star");
        }
        PlanetsProvider planetsProvider = new PlanetsProvider(AllPlanetaryBodies, AllSpacecraftBodies, PlanetDimensions, rootStar);
        new CelestialStateSupplier(playerData, planetsProvider);
    }
}
