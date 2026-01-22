package com.nythicalnorm.voxelspaceprogram.network;

import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;
import com.nythicalnorm.voxelspaceprogram.planetshine.networking.ClientTimeHandler;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.star.StarBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.spacecraft.AbstractPlayerSpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.ClientPlayerSpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientPacketHandler {
    public static void StartClientPacket(long currentTime, long timeWarp, EntitySpacecraftBody playerData, OrbitId playerParentOrbit, List<CelestialBody> planetaryBodyList) {
        Map<OrbitId, CelestialBody> AllPlanetaryBodies = new Object2ObjectOpenHashMap<>();
        ConcurrentMap<OrbitId, EntitySpacecraftBody > AllSpacecraftBodies = new ConcurrentHashMap<>();
        Map<ResourceKey<Level>, CelestialBody> PlanetDimensions = new Object2ObjectOpenHashMap<>();
        StarBody rootStar = null;

        for (CelestialBody planetaryBody : planetaryBodyList) {
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
        ClientPlayerSpacecraftBody clientPlayerSpacecraftBody;

        if (playerData instanceof ClientPlayerSpacecraftBody plrSpacecraftBody) {
            if (playerParentOrbit != null) {
                planetsProvider.playerJoinedOrbital(playerParentOrbit, playerData);
                plrSpacecraftBody.setPlayer(Minecraft.getInstance().player);
            }
            clientPlayerSpacecraftBody = plrSpacecraftBody;
        } else {
            AbstractPlayerSpacecraftBody.PlayerSpacecraftBuilder playerSpacecraftBuilder = new AbstractPlayerSpacecraftBody.PlayerSpacecraftBuilder();
            playerSpacecraftBuilder.setPlayer(Minecraft.getInstance().player);
            clientPlayerSpacecraftBody = (ClientPlayerSpacecraftBody) playerSpacecraftBuilder.buildClientSide();
        }
        CelestialStateSupplier css =  new CelestialStateSupplier(clientPlayerSpacecraftBody, planetsProvider);
        css.setCurrentTime(currentTime);
        css.setTimePassPerTick(timeWarp);
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
                css.getPlanetTexManager().incomingPlanetTexture(css.getClientPlanet(planetID), planetTexture));
    }

    public static void UpdateTimeState(long currenttime) {
        CelestialStateSupplier.getInstance().ifPresent(css ->
                ClientTimeHandler.UpdateState(currenttime));
    }

    public static void timeWarpSetFromServer(boolean successfullyChanged, long setTimeWarpSpeed) {
        CelestialStateSupplier.getInstance().ifPresent(celestialStateSupplier ->
                celestialStateSupplier.timeWarpSetFromServer(successfullyChanged, setTimeWarpSpeed));
    }
}
