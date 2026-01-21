package com.nythicalnorm.voxelspaceprogram;

import com.nythicalnorm.voxelspaceprogram.dimensions.DimensionTeleporter;
import com.nythicalnorm.voxelspaceprogram.dimensions.SpaceDimension;
import com.nythicalnorm.voxelspaceprogram.network.*;
import com.nythicalnorm.voxelspaceprogram.network.orbitaldata.ClientboundFocusedOrbitUpdate;
import com.nythicalnorm.voxelspaceprogram.network.orbitaldata.ClientboundLoginSolarSystemState;
import com.nythicalnorm.voxelspaceprogram.network.time.ClientboundSolarSystemTimeUpdate;
import com.nythicalnorm.voxelspaceprogram.network.time.ClientboundTimeWarpUpdate;
import com.nythicalnorm.voxelspaceprogram.planettexgen.biometex.BiomeColorHolder;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.planettexgen.handlers.PlanetTexHandler;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.spacecraft.AbstractPlayerSpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.ServerPlayerSpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.SpacecraftControlState;
import com.nythicalnorm.voxelspaceprogram.storage.VSPCommonSaveData;
import com.nythicalnorm.voxelspaceprogram.storage.VSPDataPackManager;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import com.nythicalnorm.voxelspaceprogram.util.Stage;
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

import java.util.List;
import java.util.Optional;

public class SolarSystem extends Stage {
    private static SolarSystem instance;
    private final MinecraftServer server;
    private PlanetTexHandler planetTexHandler;

    public SolarSystem(MinecraftServer server, PlanetsProvider pPlanets) {
        super(pPlanets);
        instance = this;
        this.server = server;
        BiomeColorHolder.init();

        VoxelSpaceProgram.setPlanetsProvider(planetsProvider);
    }

    public static SolarSystem get() {
        return instance;
    }

    public static Optional<SolarSystem> getInstance() {
        if (instance != null) {
            return Optional.of(instance);
        }
        return Optional.empty();
    }

    public static void close() {
        VoxelSpaceProgram.exitWorld();
        instance = null;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public void OnTick() {
        setCurrentTime(currentTime + timePassPerTick);
        planetsProvider.UpdatePlanets(currentTime);
        PacketHandler.sendToAllClients(new ClientboundSolarSystemTimeUpdate(currentTime));
    }

    public void serverStarted() {
        VSPCommonSaveData vspCommonSaveData = VSPDataPackManager.createOrLoadSaveData(server);
        setCurrentTime(vspCommonSaveData.getCurrentTime());
        setTimePassPerTick(vspCommonSaveData.getTimeWarp());

        this.planetTexHandler = new PlanetTexHandler();
        server.execute(() -> planetTexHandler.loadOrCreatePlanetTex(server, this.planetsProvider));
        //server.execute(() -> planetTexHandler.getOrCreateBiomeTex(server.overworld()));
    }

    public void ChangeTimeWarp(long proposedSetTimeWarpSpeed, ServerPlayer player) {
        long timePassPerSec = (long) Mth.clamp(proposedSetTimeWarpSpeed, 0, 5000000);
        timePassPerSec = Calcs.TimePerTickToTimePerMilliTick(timePassPerSec);

        setTimePassPerTick(timePassPerSec);
        server.getPlayerList().broadcastSystemMessage(Component.translatable("voxelspaceprogram.state.settimewarp",
                proposedSetTimeWarpSpeed), true);
        PacketHandler.sendToAllClients(new ClientboundTimeWarpUpdate(true, timePassPerSec));
    }

    public void playerJoined(Player entity) {
        OrbitId playerEntityID = new OrbitId(entity);
        List<CelestialBody> allPlanetaryBodies = planetsProvider.getAllPlanetaryBodies().values().stream().toList();
        ServerPlayerSpacecraftBody playerSpacecraftBody = null;

        if (planetsProvider.getAllSpacecraftBodies().containsKey(playerEntityID)) {
            if (planetsProvider.getAllSpacecraftBodies().get(playerEntityID) instanceof ServerPlayerSpacecraftBody pPlrSpacecraftBody) {
                playerSpacecraftBody = pPlrSpacecraftBody;
            }
        } else if (entity.level().dimension() == SpaceDimension.SPACE_LEVEL_KEY) {
            ServerLevel overworldLevel = server.getLevel(Level.OVERWORLD);
            entity.changeDimension(overworldLevel, new DimensionTeleporter(overworldLevel.getSharedSpawnPos().getCenter()));
        }

        PacketHandler.sendToPlayer(new ClientboundLoginSolarSystemState(playerSpacecraftBody, allPlanetaryBodies, getCurrentTime(), getTimePassPerTick()), (ServerPlayer) entity);

        if (planetTexHandler != null) {
            planetTexHandler.sendAllTexToPlayer((ServerPlayer) entity);
        }
        //server.execute(() -> planetTexHandler.sendBiomeTexToPlayer((ServerPlayer) entity, planetsProvider.getDimensionPlanet(entity.level().dimension())));
    }

    // Called when the player changes SOIs or joins on orbit artificially like the teleport command
    public void playerJoinOrbit(CelestialBody body, ServerPlayer player, OrbitalElements elements) {
        OrbitId newPlanetID = body.getOrbitId();
        OrbitId PlayerID = new OrbitId(player.getUUID());
        if (player.level().dimension() != SpaceDimension.SPACE_LEVEL_KEY) {
            player.changeDimension(server.getLevel(SpaceDimension.SPACE_LEVEL_KEY), new DimensionTeleporter(new Vec3(0d, 128d, 0d)));
        }

        if (planetsProvider.getAllSpacecraftBodies().containsKey(PlayerID)) {
            OrbitalBody playerSpacecraftBody = planetsProvider.getAllSpacecraftBodies().get(PlayerID);
            if (playerSpacecraftBody == null) {
                return;
            }

            planetsProvider.playerChangeOrbitalSOIs(playerSpacecraftBody, newPlanetID, elements);
            PacketHandler.sendToPlayer(new ClientboundFocusedOrbitUpdate(PlayerID, newPlanetID, elements), player);
        }
        else  {
            AbstractPlayerSpacecraftBody.PlayerSpacecraftBuilder builder = new AbstractPlayerSpacecraftBody.PlayerSpacecraftBuilder();
            builder.setPlayer(player);
            builder.setRotation(new Quaternionf());
            builder.setStableOrbit(true);
            builder.setOrbitalElements(elements);

            ServerPlayerSpacecraftBody newOrbitalData = (ServerPlayerSpacecraftBody) builder.build();

            planetsProvider.playerJoinedOrbital(newPlanetID, newOrbitalData);
            planetsProvider.getAllSpacecraftBodies().put(PlayerID, newOrbitalData);
            PacketHandler.sendToPlayer(new ClientboundFocusedOrbitUpdate(PlayerID, newPlanetID, elements), player);
        }
    }

    public void playerCloned(ServerPlayer player) {
        EntitySpacecraftBody spacecraftBody = planetsProvider.getAllSpacecraftBodies().get(new OrbitId(player));
        if (spacecraftBody instanceof ServerPlayerSpacecraftBody serverPlayerSpacecraftBody) {
            serverPlayerSpacecraftBody.setPlayer(player);
        }

        playerDimChanged(player, player.level().dimension());
    }

    public void handleSpacecraftMove(ServerPlayer player, OrbitId spacecraftBodyAddress, SpacecraftControlState state) {
       OrbitalBody spacecraft = planetsProvider.getSpacecraftOrbit(spacecraftBodyAddress);
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
}
