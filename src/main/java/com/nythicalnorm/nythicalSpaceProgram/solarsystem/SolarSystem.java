package com.nythicalnorm.nythicalSpaceProgram.solarsystem;

import com.nythicalnorm.nythicalSpaceProgram.common.EntityBody;
import com.nythicalnorm.nythicalSpaceProgram.common.Orbit;
import com.nythicalnorm.nythicalSpaceProgram.common.PlanetaryBody;
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
import java.util.Stack;

public class SolarSystem {
    public double currentTime; // time passed since start in seconds
    public double timePassPerSecond;
    //public static double tickTimeStamp;
    private MinecraftServer server;
    private HashMap<String, Stack<String>> allPlayerOrbitalAddresses;
    private Planets planets;

    public SolarSystem(MinecraftServer server, Planets pPlanets) {
        timePassPerSecond = 1;
        allPlayerOrbitalAddresses = new HashMap<>();
        this.server = server;
        this.planets = pPlanets;
    }

    public Optional<MinecraftServer> getServer() {
        return Optional.of(server);
    }

    public Planets getPlanets() {
        return planets;
    }

    public void OnTick() {
        currentTime = currentTime + (timePassPerSecond/20);
        HashMap<String, Vector3d> PlanetPositions = new HashMap<>();

        planets.UpdatePlanets(currentTime);

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
        if (allPlayerOrbitalAddresses.containsKey(entity.getStringUUID())) {
            Orbit obt = planets.getPlanet(allPlayerOrbitalAddresses.get(entity.getStringUUID()));
            PacketHandler.sendToPlayer(new ClientBoundLoginSolarSystemState((EntityBody) obt), (ServerPlayer) entity);
        }
        else {
            PacketHandler.sendToPlayer(new ClientBoundLoginSolarSystemState(new PlayerSpacecraftBody()), (ServerPlayer) entity);
        }
    }

    public void playerJoinOrbit(String body, ServerPlayer player, OrbitalElements elements) {
        Optional<Stack<String>> oldAddress = Optional.empty();

        if (allPlayerOrbitalAddresses.containsKey(player.getStringUUID())) {
            oldAddress = Optional.of(allPlayerOrbitalAddresses.get(player.getStringUUID()));
        }
        ServerPlayerOrbitalData orbitData = new ServerPlayerOrbitalData(player, true, false, elements);
        Stack<String> address = planets.getPlanetAddress(body);
        allPlayerOrbitalAddresses.put(player.getStringUUID(), address);
        Orbit newOrbitPlanet = planets.getPlanet(address);
        if (newOrbitPlanet instanceof PlanetaryBody plnt) {
            plnt.addChildSpacecraft(player.getStringUUID(), orbitData);
        }
    }
}
