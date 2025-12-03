package com.nythicalnorm.nythicalSpaceProgram.planetshine;

import com.nythicalnorm.nythicalSpaceProgram.orbit.EntityOrbitalBody;
import com.nythicalnorm.nythicalSpaceProgram.orbit.PlanetaryBody;
import com.nythicalnorm.nythicalSpaceProgram.orbit.ClientPlayerSpacecraftBody;
import com.nythicalnorm.nythicalSpaceProgram.network.PacketHandler;
import com.nythicalnorm.nythicalSpaceProgram.network.ServerBoundTimeWarpChange;
import com.nythicalnorm.nythicalSpaceProgram.solarsystem.Planets;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers.SpaceObjRenderer;
import com.nythicalnorm.nythicalSpaceProgram.orbit.OrbitalElements;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;

import java.lang.Math;
import java.util.Optional;
import java.util.Stack;

//@OnlyIn(Dist.CLIENT)
public class CelestialStateSupplier {
    private double serverSideSolarSystemTime = 0;
    private double clientSideSolarSystemTime = 0;
    private long clientSideTickTime = 0L;
    public double lastUpdatedTimeWarpPerSec = 0;

    private ClientPlayerSpacecraftBody playerData;
    private PlanetaryBody currentPlanetOn;

    private Planets planets;

    public CelestialStateSupplier(EntityOrbitalBody playerDataFromServer, Planets planets) {
        playerData = new ClientPlayerSpacecraftBody(playerDataFromServer);
        this.planets = planets;
        SpaceObjRenderer.PopulateRenderPlanets(planets);
    }

    public void UpdateState(double currentTime, double TimePassedPerSec){
        serverSideSolarSystemTime = currentTime;
        lastUpdatedTimeWarpPerSec = TimePassedPerSec;

        if (Math.abs(clientSideSolarSystemTime - serverSideSolarSystemTime) >  lastUpdatedTimeWarpPerSec*0.01d)
        {
            clientSideSolarSystemTime = serverSideSolarSystemTime;
        }
    }

    public void UpdateOrbitalBodies() {
        long currentTime = Util.getMillis();

        if (!Minecraft.getInstance().isPaused()) {
            float timeDiff = (float) (currentTime - clientSideTickTime) / 1000;
            clientSideSolarSystemTime = clientSideSolarSystemTime + timeDiff * lastUpdatedTimeWarpPerSec;
        }

        clientSideTickTime = currentTime;
        planets.UpdatePlanets(clientSideSolarSystemTime);

        String planetName = planets.getDimensionPlanet(Minecraft.getInstance().level.dimension());
        if (planets.getAllPlanetNames().contains(planetName)) {
            currentPlanetOn = planets.getPlanet(planetName);
            playerData.updatePlayerPosRot(Minecraft.getInstance().player, currentPlanetOn);
        } else {
            currentPlanetOn = null;
        }
    }

    public double getLastUpdatedTimeWarpPerSec() {
        return lastUpdatedTimeWarpPerSec;
    }

    public ClientPlayerSpacecraftBody getPlayerData() {
        return playerData;
    }

    public void TryChangeTimeWarp(boolean DoInc) {
        double sign = 2;
        if (!DoInc) {
            sign = 0.5;
        }
        PacketHandler.sendToServer(new ServerBoundTimeWarpChange(sign * lastUpdatedTimeWarpPerSec));
    }


    public void trackedOrbitUpdate(int shipID, Stack<String> oldAddress, Stack<String> newAddress, OrbitalElements orbitalElements) {
        if (Minecraft.getInstance().player.getId() == shipID) {
            if (oldAddress == null) {
                playerData.setOrbitalElements(orbitalElements);
                planets.playerJoinedOrbital(Minecraft.getInstance().player.getStringUUID(), newAddress, playerData);
            }
            else {
                planets.playerChangeOrbitalSOIs(Minecraft.getInstance().player.getStringUUID(), oldAddress, newAddress, orbitalElements);
            }
        }
    }

    public boolean doRender() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return false;
        }
        return planets.isDimensionPlanet(mc.level.dimension()) || planets.isDimensionSpace(mc.level.dimension());
    }


    public Optional<PlanetaryBody> getCurrentPlanet() {
        if (currentPlanetOn != null) {
            return Optional.of(currentPlanetOn);
        }
        else  {
            return Optional.empty();
        }
    }

    public boolean isOnPlanet()
    {
        return currentPlanetOn != null;
    }

    public Planets getPlanets() {
        return planets;
    }

    public boolean weInSpace() {
        return planets.isDimensionSpace(Minecraft.getInstance().level.dimension());
    }
}
