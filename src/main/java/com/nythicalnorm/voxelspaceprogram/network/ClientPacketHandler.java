package com.nythicalnorm.voxelspaceprogram.network;

import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;
import com.nythicalnorm.voxelspaceprogram.planetshine.networking.ClientTimeHandler;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.star.StarBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.spacecraft.player.AbstractPlayerOrbitBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.player.ClientPlayerOrbitBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntityOrbitBody;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientPacketHandler {
    public static void StartClientPacket(long currentTime, long timeWarp, EntityOrbitBody playerData, OrbitId playerParentOrbit, List<CelestialBody> planetaryBodyList) {
        Map<OrbitId, CelestialBody> AllPlanetaryBodies = new Object2ObjectOpenHashMap<>();
        ConcurrentMap<OrbitId, EntityOrbitBody> AllSpacecraftBodies = new ConcurrentHashMap<>();
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
        ClientPlayerOrbitBody clientPlayerSpacecraftBody;

        if (playerData instanceof ClientPlayerOrbitBody plrSpacecraftBody) {
            if (playerParentOrbit != null) {
                planetsProvider.playerJoinedOrbital(playerParentOrbit, playerData);
                plrSpacecraftBody.setPlayer(Minecraft.getInstance().player);
            }
            clientPlayerSpacecraftBody = plrSpacecraftBody;
        } else {
            AbstractPlayerOrbitBody.PlayerOrbitBuilder playerSpacecraftBuilder = new AbstractPlayerOrbitBody.PlayerOrbitBuilder();
            playerSpacecraftBuilder.setPlayer(Minecraft.getInstance().player);
            clientPlayerSpacecraftBody = (ClientPlayerOrbitBody) playerSpacecraftBuilder.buildClientSide();
        }
        CelestialStateSupplier css =  new CelestialStateSupplier(clientPlayerSpacecraftBody, planetsProvider);
        css.setCurrentTime(currentTime);
        css.setTimePassPerTick(timeWarp);
    }

    public static void OrbitSOIChange(OrbitId spacecraftID, OrbitId newParentID, OrbitalElements orbitalElements) {
        CelestialStateSupplier.getInstance().ifPresent(celestialStateSupplier ->
                celestialStateSupplier.orbitSOIChange(spacecraftID, newParentID, orbitalElements));
    }

    public static void orbitRemove(OrbitId spacecraftID) {
        CelestialStateSupplier.getInstance().ifPresent(celestialStateSupplier ->
                celestialStateSupplier.orbitRemove(spacecraftID));
    }

    public static void incomingLodTexture(ResourceKey<Level> dimensionID, int textureID, int textureSize, byte[] biomeTexture) {
        CelestialStateSupplier.getInstance().ifPresent(celestialStateSupplier ->
                celestialStateSupplier.getPlanetTexManager().incomingLodTexture(dimensionID, textureID, textureSize, biomeTexture));
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
