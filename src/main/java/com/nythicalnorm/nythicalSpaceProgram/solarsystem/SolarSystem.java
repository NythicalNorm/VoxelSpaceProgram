package com.nythicalnorm.nythicalSpaceProgram.solarsystem;

import com.nythicalnorm.nythicalSpaceProgram.network.ClientBoundSpaceShipsPosUpdate;
import com.nythicalnorm.nythicalSpaceProgram.network.PacketHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.joml.Vector3d;
import java.util.HashMap;
import java.util.Map;

public class SolarSystem {
    public static double currentTime; // time passed since start in seconds
    public static  double timePassPerSecond;
    //public static double tickTimeStamp;

    public SolarSystem() {
        timePassPerSecond = 1;
    }

    public void OnTick() {
        currentTime = currentTime + (timePassPerSecond/20);
        HashMap<String, Vector3d> PlanetPositions = new HashMap<>();

        for (Map.Entry<String, PlanetaryBody> entry : Planets.PLANETARY_BODIES.entrySet()) {
            PlanetaryBody planet = entry.getValue();
            PlanetPositions.put(entry.getKey(),planet.CalculateCartesianPosition(currentTime));
        }

        PacketHandler.sendToAllClients(new ClientBoundSpaceShipsPosUpdate(currentTime,timePassPerSecond));
    }

    public static void ChangeTimeWarp(double proposedSetTimeWarpSpeed, ServerPlayer player) {
        if (player == null) {
            return;
        }
        timePassPerSecond = proposedSetTimeWarpSpeed;
        player.displayClientMessage(Component.translatable("nythicalspaceprogram.settimewarp").append(proposedSetTimeWarpSpeed + "x"), true);
    }
}
