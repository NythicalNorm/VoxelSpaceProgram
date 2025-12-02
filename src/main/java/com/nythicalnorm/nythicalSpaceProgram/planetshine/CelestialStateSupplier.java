package com.nythicalnorm.nythicalSpaceProgram.planetshine;

import com.nythicalnorm.nythicalSpaceProgram.common.EntityBody;
import com.nythicalnorm.nythicalSpaceProgram.common.PlanetaryBody;
import com.nythicalnorm.nythicalSpaceProgram.common.PlayerSpacecraftBody;
import com.nythicalnorm.nythicalSpaceProgram.network.PacketHandler;
import com.nythicalnorm.nythicalSpaceProgram.network.ServerBoundTimeWarpChange;
import com.nythicalnorm.nythicalSpaceProgram.planet.Planets;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers.SpaceObjRenderer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;

import java.lang.Math;
import java.util.Optional;

//@OnlyIn(Dist.CLIENT)
public class CelestialStateSupplier {
    private double serverSideSolarSystemTime = 0;
    private double clientSideSolarSystemTime = 0;
    private long clientSideTickTime = 0L;
    public double lastUpdatedTimeWarpPerSec = 0;

    private PlayerSpacecraftBody playerData;
    private PlanetaryBody currentPlanetOn;

    private Planets planets;

    public CelestialStateSupplier(EntityBody playerDataFromServer, Planets planets) {
        playerData = new PlayerSpacecraftBody(playerDataFromServer);
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
        }
    }

    public double getLastUpdatedTimeWarpPerSec() {
        return lastUpdatedTimeWarpPerSec;
    }

    public PlayerSpacecraftBody getPlayerData() {
        return playerData;
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
