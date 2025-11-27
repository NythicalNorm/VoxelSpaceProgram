package com.nythicalnorm.nythicalSpaceProgram.planetshine;

import com.nythicalnorm.nythicalSpaceProgram.network.PacketHandler;
import com.nythicalnorm.nythicalSpaceProgram.network.ServerBoundTimeWarpChange;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetDimensions;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetaryBody;
import com.nythicalnorm.nythicalSpaceProgram.planet.Planets;
import com.nythicalnorm.nythicalSpaceProgram.util.Calcs;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3d;

@OnlyIn(Dist.CLIENT)
public class CelestialStateSupplier {
    private double serverSideSolarSystemTime = 0;
    private double clientSideSolarSystemTime = 0;
    private long clientSideTickTime = 0L;

    public double lastUpdatedTimeWarpPerSec = 0;

    private Vector3d playerAbsolutePositon;
    private Vector3d playerRelativePositon;
    private PlanetaryBody currentPlanet;

    public CelestialStateSupplier() {
        playerAbsolutePositon = new Vector3d();
        playerRelativePositon = new Vector3d();
    }

    public void UpdateState(double currentTime, double TimePassedPerSec){
        serverSideSolarSystemTime = currentTime;
        lastUpdatedTimeWarpPerSec = TimePassedPerSec;

        if (Math.abs(clientSideSolarSystemTime - serverSideSolarSystemTime) >  lastUpdatedTimeWarpPerSec*0.01d)
        {
            clientSideSolarSystemTime = serverSideSolarSystemTime;
        }
    }

    public double UpdatePlanetaryBodies() {
        long currentTime = Util.getMillis();

        if (!Minecraft.getInstance().isPaused()) {
            float timeDiff = (float) (currentTime - clientSideTickTime) / 1000;
            clientSideSolarSystemTime = clientSideSolarSystemTime + timeDiff * lastUpdatedTimeWarpPerSec;
        }

        clientSideTickTime = currentTime;
        Planets.UpdatePlanets(clientSideSolarSystemTime);
        updatePlayerPos();
        return clientSideSolarSystemTime;
    }

    public void updatePlayerPos() {
        LocalPlayer plr = Minecraft.getInstance().player;
        if (PlanetDimensions.isDimensionPlanet(plr.level().dimension())) {
            currentPlanet = PlanetDimensions.getDimPlanet(plr.level().dimension());
            playerRelativePositon = Calcs.planetDimPosToNormalizedVector(plr.position(), currentPlanet, false);
            Vector3d newAbs = new Vector3d(currentPlanet.getPlanetAbsolutePos());
            playerAbsolutePositon = newAbs.add(playerRelativePositon);
        }
    }

    public Vector3d getPlayerAbsolutePositon() {
        return playerAbsolutePositon;
    }

    public Vector3d getPlayerRelativePositon() {
        return playerRelativePositon;
    }

    public double getLastUpdatedTimeWarpPerSec() {
        return lastUpdatedTimeWarpPerSec;
    }

    public void TryChangeTimeWarp(boolean DoInc) {
        double sign = 2;
        if (!DoInc) {
            sign = 0.5;
        }
        PacketHandler.sendToServer(new ServerBoundTimeWarpChange(sign * lastUpdatedTimeWarpPerSec));
    }

    public boolean isOnPlanet()
    {
        return currentPlanet != null;
    }

    public PlanetaryBody getDimPlanet() {
        return currentPlanet;
    }
}
