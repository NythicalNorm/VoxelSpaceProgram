package com.nythicalnorm.voxelspaceprogram;

import com.nythicalnorm.voxelspaceprogram.dimensions.DimensionTeleporter;
import com.nythicalnorm.voxelspaceprogram.dimensions.SpaceDimension;
import com.nythicalnorm.voxelspaceprogram.network.*;
import com.nythicalnorm.voxelspaceprogram.planettexgen.biometex.BiomeColorHolder;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetAccessor;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.planettexgen.handlers.PlanetTexHandler;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.ServerPlayerSpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.SpacecraftControlState;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class SolarSystem {
    public long currentTime; // time passed since start in 1000 times currentTick, in milliTicks if you will.
    public long timePassPerTick;
    //public static double tickTimeStamp;
    private final MinecraftServer server;
    private final PlanetsProvider planetsProvider;
    private PlanetTexHandler planetTexHandler;

    public SolarSystem(MinecraftServer server, PlanetsProvider pPlanets) {
        timePassPerTick = 1000;
        this.server = server;
        this.planetsProvider = pPlanets;
        BiomeColorHolder.init();
    }

    public MinecraftServer getServer() {
        return server;
    }

    public PlanetsProvider getPlanetsProvider() {
        return planetsProvider;
    }

    public void OnTick() {
        currentTime = currentTime + timePassPerTick;
        planetsProvider.UpdatePlanets(currentTime);
        PacketHandler.sendToAllClients(new ClientboundSolarSystemTimeUpdate(currentTime, timePassPerTick));
    }

    public void serverStarted() {
        this.planetTexHandler = new PlanetTexHandler();
        server.execute(() -> planetTexHandler.loadOrCreatePlanetTex(server, this.planetsProvider));
        //server.execute(() -> planetTexHandler.getOrCreateBiomeTex(server.overworld()));

        planetsProvider.getPlanetDimensions().forEach((levelResourceKey, planetaryBody) -> {
            Level planetLevel = server.getLevel(levelResourceKey);
            ((PlanetAccessor) planetLevel).setPlanetaryBody(planetaryBody);
        });
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void ChangeTimeWarp(int proposedSetTimeWarpSpeed, ServerPlayer player) {
        long timePassPerSec = Mth.clamp(proposedSetTimeWarpSpeed, 0, 5000000);
        timePassPerTick = Calcs.TimePerTickToTimePerMilliTick(timePassPerSec);
        server.getPlayerList().broadcastSystemMessage(Component.translatable("voxelspaceprogram.state.settimewarp",
                proposedSetTimeWarpSpeed), true);
        PacketHandler.sendToAllClients(new ClientboundTimeWarpUpdate(true, proposedSetTimeWarpSpeed));
    }

    public void playerJoined(Player entity) {
        OrbitId playerEntityID = new OrbitId(entity);
        // this is not working check before making a saving system
        if (planetsProvider.getAllSpacecraftBodies().containsKey(playerEntityID)) {
            EntitySpacecraftBody playerSpacecraftBody = planetsProvider.getAllSpacecraftBodies().get(playerEntityID);
            PacketHandler.sendToPlayer(new ClientboundLoginSolarSystemState(playerSpacecraftBody), (ServerPlayer) entity);
        }
        else {
            if (entity.level().dimension() == SpaceDimension.SPACE_LEVEL_KEY) {
                ServerLevel overworldLevel = server.getLevel(Level.OVERWORLD);
                entity.changeDimension(overworldLevel, new DimensionTeleporter(overworldLevel.getSharedSpawnPos().getCenter()));
            }
            PacketHandler.sendToPlayer(new ClientboundLoginSolarSystemState(), (ServerPlayer) entity);
        }
        if (planetTexHandler != null) {
            planetTexHandler.sendAllTexToPlayer((ServerPlayer) entity);
        }

        //server.execute(() -> planetTexHandler.sendBiomeTexToPlayer((ServerPlayer) entity, planetsProvider.getDimensionPlanet(entity.level().dimension())));
    }

    // Called when the player changes SOIs or joins on orbit artificially like the teleport command
    public void playerJoinOrbit(PlanetaryBody body, ServerPlayer player, OrbitalElements elements) {
        OrbitId newPlanetID = body.getOrbitId();
        OrbitId PlayerID = new OrbitId(player.getUUID());
        if (player.level().dimension() != SpaceDimension.SPACE_LEVEL_KEY) {
            player.changeDimension(server.getLevel(SpaceDimension.SPACE_LEVEL_KEY), new DimensionTeleporter(new Vec3(0d, 128d, 0d)));
        }

        if (planetsProvider.getAllSpacecraftBodies().containsKey(PlayerID)) {
            Orbit playerSpacecraftBody = planetsProvider.getAllSpacecraftBodies().get(PlayerID);
            if (playerSpacecraftBody == null) {
                return;
            }

            planetsProvider.playerChangeOrbitalSOIs(playerSpacecraftBody, newPlanetID, elements);
            PacketHandler.sendToPlayer(new ClientboundFocusedOrbitUpdate(PlayerID, newPlanetID, elements), player);
        }
        else  {
            Quaternionf playerRot = new Quaternionf();
            ServerPlayerSpacecraftBody newOrbitalData = new ServerPlayerSpacecraftBody(player, true, true, playerRot, elements);
            planetsProvider.playerJoinedOrbital(newPlanetID, newOrbitalData);
            planetsProvider.getAllSpacecraftBodies().put(PlayerID, newOrbitalData);
            PacketHandler.sendToPlayer(new ClientboundFocusedOrbitUpdate(PlayerID, newPlanetID, elements), player);
        }
    }

    public void playerCloned(ServerPlayer player) {
        EntitySpacecraftBody spacecraftBody = planetsProvider.getAllSpacecraftBodies().get(new OrbitId(player));
        if (spacecraftBody instanceof ServerPlayerSpacecraftBody serverPlayerSpacecraftBody) {
            serverPlayerSpacecraftBody.setPlayerEntity(player);
        }

        playerDimChanged(player, player.level().dimension());
    }

    public void handleSpacecraftMove(ServerPlayer player, OrbitId spacecraftBodyAddress, SpacecraftControlState state) {
       Orbit spacecraft = planetsProvider.getSpacecraftOrbit(spacecraftBodyAddress);
       if (spacecraft == null) {
           return;
       }

       if (spacecraft instanceof EntitySpacecraftBody entitySpacecraftBody) {
           entitySpacecraftBody.processMovement(state);
       }
    }

    public void playerDimChanged(Player entity, ResourceKey<Level> toDimension) {
        if (toDimension != SpaceDimension.SPACE_LEVEL_KEY) {
            EntitySpacecraftBody entitySpacecraftBody = planetsProvider.getAllSpacecraftBodies().get(new OrbitId(entity));

            if (entitySpacecraftBody instanceof ServerPlayerSpacecraftBody serverPlayerSpacecraftBody) {
                serverPlayerSpacecraftBody.removeYourself();
            }
        }
    }

    public void removePlayerFromOrbit(OrbitId id) {
        planetsProvider.getAllSpacecraftBodies().remove(id);
    }
}
