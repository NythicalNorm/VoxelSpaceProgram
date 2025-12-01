package com.nythicalnorm.nythicalSpaceProgram.solarsystem;

import com.nythicalnorm.nythicalSpaceProgram.common.PlayerSpacecraftBody;
import com.nythicalnorm.nythicalSpaceProgram.network.ClientBoundLoginSolarSystemState;
import com.nythicalnorm.nythicalSpaceProgram.network.ClientBoundSpaceShipsPosUpdate;
import com.nythicalnorm.nythicalSpaceProgram.network.PacketHandler;
import com.nythicalnorm.nythicalSpaceProgram.planet.Planets;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3d;
import java.util.HashMap;
import java.util.Optional;

public class SolarSystem {
    public double currentTime; // time passed since start in seconds
    public double timePassPerSecond;
    //public static double tickTimeStamp;
    private MinecraftServer server;
    private HashMap<String, ServerPlayerOrbitalData> allPlayerOrbitalData;


    public SolarSystem(MinecraftServer server) {
        timePassPerSecond = 1;
        allPlayerOrbitalData = new HashMap<>();
        this.server = server;
    }

    public Optional<MinecraftServer> getServer() {
        return Optional.of(server);
    }

    public void OnTick() {
        currentTime = currentTime + (timePassPerSecond/20);
        HashMap<String, Vector3d> PlanetPositions = new HashMap<>();

        Planets.UpdatePlanets(currentTime);

        PacketHandler.sendToAllClients(new ClientBoundSpaceShipsPosUpdate(currentTime,timePassPerSecond));
    }

    public void ChangeTimeWarp(double proposedSetTimeWarpSpeed, ServerPlayer player) {
        if (player == null) {
            return;
        }
        timePassPerSecond = proposedSetTimeWarpSpeed;
        player.displayClientMessage(Component.translatable("nythicalspaceprogram.settimewarp").append(proposedSetTimeWarpSpeed + "x"), true);
    }

    public void playerJoined(Player entity) {
        if (allPlayerOrbitalData.containsKey(entity.getStringUUID())) {
            PacketHandler.sendToPlayer(new ClientBoundLoginSolarSystemState(allPlayerOrbitalData.get(entity.getStringUUID())), (ServerPlayer) entity);
        }
        else {
            PacketHandler.sendToPlayer(new ClientBoundLoginSolarSystemState(new PlayerSpacecraftBody()), (ServerPlayer) entity);
        }
    }
}
