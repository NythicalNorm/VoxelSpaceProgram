package com.nythicalnorm.nythicalSpaceProgram.solarsystem;

import com.nythicalnorm.nythicalSpaceProgram.dimensions.DimensionTeleporter;
import com.nythicalnorm.nythicalSpaceProgram.dimensions.SpaceDimension;
import com.nythicalnorm.nythicalSpaceProgram.network.*;
import com.nythicalnorm.nythicalSpaceProgram.orbit.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import java.util.HashMap;
import java.util.Stack;

public class SolarSystem {
    public double currentTime; // time passed since start in seconds
    public double timePassPerSecond;
    //public static double tickTimeStamp;
    private final MinecraftServer server;
    private HashMap<String, Stack<String>> allPlayerOrbitalAddresses;
    private final Planets planets;

    public SolarSystem(MinecraftServer server, Planets pPlanets) {
        timePassPerSecond = 1;
        allPlayerOrbitalAddresses = new HashMap<>();
        this.server = server;
        this.planets = pPlanets;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public Planets getPlanets() {
        return planets;
    }

    public void OnTick() {
        currentTime = currentTime + (timePassPerSecond/20);
        HashMap<String, Vector3d> PlanetPositions = new HashMap<>();

        planets.UpdatePlanets(currentTime);

        PacketHandler.sendToAllClients(new ClientBoundSolarSystemTimeUpdate(currentTime,timePassPerSecond));
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void ChangeTimeWarp(int proposedSetTimeWarpSpeed, ServerPlayer player) {
        if (player == null) {
            return;
        }
        timePassPerSecond = (double) Mth.clamp(proposedSetTimeWarpSpeed, 0, 5000000);
        player.displayClientMessage(Component.translatable("nythicalspaceprogram.settimewarp").append(proposedSetTimeWarpSpeed + "x"), true);
        PacketHandler.sendToAllClients(new ClientBoundTimeWarpUpdate(true, proposedSetTimeWarpSpeed));
    }

    public void playerJoined(Player entity) {
        // this is not working check before making a saving system
        if (allPlayerOrbitalAddresses.containsKey(entity.getStringUUID())) {
            PlanetaryBody obt = planets.getPlanet(allPlayerOrbitalAddresses.get(entity.getStringUUID()));
            EntityOrbitalBody playerEntity = (EntityOrbitalBody)obt.getChild(entity.getStringUUID());
            PacketHandler.sendToPlayer(new ClientBoundLoginSolarSystemState(playerEntity), (ServerPlayer) entity);
        }
        else {
            if (entity.level().dimension() == SpaceDimension.SPACE_LEVEL_KEY) {
                ServerLevel overworldLevel = server.getLevel(Level.OVERWORLD);
                entity.changeDimension(overworldLevel, new DimensionTeleporter(overworldLevel.getSharedSpawnPos().getCenter()));
            }
            PacketHandler.sendToPlayer(new ClientBoundLoginSolarSystemState(new ClientPlayerSpacecraftBody()), (ServerPlayer) entity);
        }
    }

    // Called when the player changes SOIs or joins on orbit artificially like the teleport command
    public void playerJoinOrbit(String body, ServerPlayer player, OrbitalElements elements) {
        Stack<String> newAddress = planets.getPlanetAddress(body);
        String PlayerUUid = player.getStringUUID();
        if (player.level().dimension() != SpaceDimension.SPACE_LEVEL_KEY) {
            player.changeDimension(server.getLevel(SpaceDimension.SPACE_LEVEL_KEY), new DimensionTeleporter(new Vec3(0d, 128d, 0d)));
        }

        if (allPlayerOrbitalAddresses.containsKey(PlayerUUid)) {
            Stack<String> oldAddress = allPlayerOrbitalAddresses.get(PlayerUUid);
            planets.playerChangeOrbitalSOIs(PlayerUUid, oldAddress, newAddress, elements);
            allPlayerOrbitalAddresses.remove(PlayerUUid);
            PacketHandler.sendToPlayer(new ClientBoundTrackedOrbitUpdate(player, oldAddress, newAddress, elements), player);
            allPlayerOrbitalAddresses.put(PlayerUUid, newAddress);
        }
        else  {
            Quaternionf playerRot = new Quaternionf();
            ServerPlayerSpacecraftBody newOrbitalData = new ServerPlayerSpacecraftBody(player, true, true, playerRot, elements);
            planets.playerJoinedOrbital(PlayerUUid, newAddress, newOrbitalData);
            allPlayerOrbitalAddresses.put(PlayerUUid, newAddress);
            PacketHandler.sendToPlayer(new ClientBoundTrackedOrbitUpdate(player, null, newAddress, elements), player);
        }
    }
}
