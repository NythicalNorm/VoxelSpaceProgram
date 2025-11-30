package com.nythicalnorm.nythicalSpaceProgram.planetshine;

import com.nythicalnorm.nythicalSpaceProgram.common.PlayerOrbitalData;
import com.nythicalnorm.nythicalSpaceProgram.network.PacketHandler;
import com.nythicalnorm.nythicalSpaceProgram.network.ServerBoundTimeWarpChange;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetDimensions;
import com.nythicalnorm.nythicalSpaceProgram.planet.Planets;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;

import java.lang.Math;

//@OnlyIn(Dist.CLIENT)
public class CelestialStateSupplier {
    private double serverSideSolarSystemTime = 0;
    private double clientSideSolarSystemTime = 0;
    private long clientSideTickTime = 0L;
    public double lastUpdatedTimeWarpPerSec = 0;
    PlayerOrbitalData playerData;


    public CelestialStateSupplier(PlayerOrbitalData playerDataFromServer) {
        playerData = playerDataFromServer;
    }

    public void UpdateState(double currentTime, double TimePassedPerSec){
        serverSideSolarSystemTime = currentTime;
        lastUpdatedTimeWarpPerSec = TimePassedPerSec;

        if (Math.abs(clientSideSolarSystemTime - serverSideSolarSystemTime) >  lastUpdatedTimeWarpPerSec*0.01d)
        {
            clientSideSolarSystemTime = serverSideSolarSystemTime;
        }
    }

    public void UpdatePlanetaryBodies() {
        long currentTime = Util.getMillis();

        if (!Minecraft.getInstance().isPaused()) {
            float timeDiff = (float) (currentTime - clientSideTickTime) / 1000;
            clientSideSolarSystemTime = clientSideSolarSystemTime + timeDiff * lastUpdatedTimeWarpPerSec;
        }

        clientSideTickTime = currentTime;
        Planets.UpdatePlanets(clientSideSolarSystemTime);
        playerData.updatePlayerPosRot(Minecraft.getInstance().player);
    }

    public double getLastUpdatedTimeWarpPerSec() {
        return lastUpdatedTimeWarpPerSec;
    }

    public PlayerOrbitalData getPlayerData() {
        return playerData;
    }

    public void setPlayerData(PlayerOrbitalData playerData) {
        this.playerData = playerData;
    }

    public void TryChangeTimeWarp(boolean DoInc) {
        double sign = 2;
        if (!DoInc) {
            sign = 0.5;
        }
        PacketHandler.sendToServer(new ServerBoundTimeWarpChange(sign * lastUpdatedTimeWarpPerSec));
    }

    public boolean doRender() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return false;
        }
        return PlanetDimensions.isDimensionPlanet(mc.level.dimension()) || PlanetDimensions.isDimensionSpace(mc.level.dimension());
    }

    public boolean weInSpace() {
        return PlanetDimensions.isDimensionSpace(Minecraft.getInstance().level.dimension());
    }
}
