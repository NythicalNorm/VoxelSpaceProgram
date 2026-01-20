package com.nythicalnorm.voxelspaceprogram.network;

import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;
import com.nythicalnorm.voxelspaceprogram.planetshine.networking.ClientTimeHandler;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.StarBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.spacecraft.ClientPlayerSpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public class ClientPacketHandler {
    public static void StartClientPacket(EntitySpacecraftBody playerData, OrbitId playerParentOrbit, List<PlanetaryBody> planetaryBodyList) {
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
        if (playerData instanceof ClientPlayerSpacecraftBody clientPlayerSpacecraftBody) {
            if (playerParentOrbit != null) {
                planetsProvider.playerJoinedOrbital(playerParentOrbit, playerData);
            }
            new CelestialStateSupplier(clientPlayerSpacecraftBody, planetsProvider);
        } else {
            new CelestialStateSupplier(new ClientPlayerSpacecraftBody(), planetsProvider);
        }
    }

    public static void FocusedOrbitUpdate(OrbitId spacecraftID, OrbitId newParentID, OrbitalElements orbitalElements) {
        CelestialStateSupplier.getInstance().ifPresent(celestialStateSupplier ->
                celestialStateSupplier.trackedOrbitUpdate(spacecraftID, newParentID, orbitalElements));
    }

    public static void incomingBiomeTexture(ResourceKey<Level> dimensionID, int textureID, short textureSize, byte[] biomeTexture) {
        CelestialStateSupplier.getInstance().ifPresent(celestialStateSupplier ->
                celestialStateSupplier.getPlanetTexManager().incomingBiomeTexture(dimensionID, textureID, textureSize, biomeTexture));
    }

    public static void incomingPlanetTexture(OrbitId planetID, byte[] planetTexture) {
        CelestialStateSupplier.getInstance().ifPresent(css ->
                css.getPlanetTexManager().incomingPlanetTexture(css.getPlanetsProvider().getPlanet(planetID), planetTexture));
    }

    public static void UpdateTimeState(long currenttime, long timePassPerSecond) {
        CelestialStateSupplier.getInstance().ifPresent(css -> {
            ClientTimeHandler.UpdateState(currenttime, timePassPerSecond);
            css.setTimePassPerTick(timePassPerSecond);
        });
    }

    public static void timeWarpSetFromServer(boolean successfullyChanged, int setTimeWarpSpeed) {
        CelestialStateSupplier.getInstance().ifPresent(celestialStateSupplier ->
                celestialStateSupplier.timeWarpSetFromServer(successfullyChanged, setTimeWarpSpeed));
    }
}
