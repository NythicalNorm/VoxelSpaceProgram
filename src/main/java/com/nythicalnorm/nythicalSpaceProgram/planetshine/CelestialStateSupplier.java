package com.nythicalnorm.nythicalSpaceProgram.planetshine;

import com.nythicalnorm.nythicalSpaceProgram.gui.ModScreenManager;
import com.nythicalnorm.nythicalSpaceProgram.orbit.*;
import com.nythicalnorm.nythicalSpaceProgram.network.PacketHandler;
import com.nythicalnorm.nythicalSpaceProgram.network.ServerBoundTimeWarpChange;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.networking.ClientTimeHandler;
import com.nythicalnorm.nythicalSpaceProgram.solarsystem.Planets;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers.SpaceObjRenderer;
import net.minecraft.client.Minecraft;

import java.util.Optional;
import java.util.Stack;

//@OnlyIn(Dist.CLIENT)
public class CelestialStateSupplier {
    private static final int[] timeWarpSettings = new int[]{1, 10, 100, 1000, 10000, 100000, 1000000};
    private short currentTimeWarpSetting;

    private ClientPlayerSpacecraftBody playerOrbit;
    private PlanetaryBody currentPlanetOn;
    private PlanetaryBody currentPlanetSOIin;
    private ClientPlayerSpacecraftBody controllingBody;

    private final Planets planets;
    private ModScreenManager screenManager;

    public CelestialStateSupplier(EntitySpacecraftBody playerDataFromServer, Planets planets) {
        playerOrbit = new ClientPlayerSpacecraftBody(playerDataFromServer);
        this.planets = planets;
        SpaceObjRenderer.PopulateRenderPlanets(planets);
        this.screenManager = new ModScreenManager();
    }

    public void tick() {
        if (controllingBody != null && screenManager.isSpacecraftScreenOpen()) {
            screenManager.getSpacecraftScreen().sendInputs(controllingBody);
        }
    }

    public void UpdateOrbitalBodies(float partialTick) {
        //clientSideTickTime = currentTime;
        planets.UpdatePlanets(ClientTimeHandler.calculateCurrentTime(partialTick));

        if (!weInSpace()) {
            currentPlanetSOIin = null;
        }

        String planetName = planets.getDimensionPlanet(Minecraft.getInstance().level.dimension());
        if (planets.getAllPlanetNames().contains(planetName)) {
            currentPlanetOn = planets.getPlanet(planetName);
            playerOrbit.updatePlayerPosRot(Minecraft.getInstance().player, currentPlanetOn);
        } else {
            currentPlanetOn = null;
        }
    }

    public ClientPlayerSpacecraftBody getPlayerOrbit() {
        return playerOrbit;
    }

    public void TryChangeTimeWarp(boolean doInc) {
        short propesedSetIndex = currentTimeWarpSetting;
        propesedSetIndex = doInc ? ++propesedSetIndex : --propesedSetIndex;

        if (propesedSetIndex >= 0 && propesedSetIndex < timeWarpSettings.length) {
            PacketHandler.sendToServer(new ServerBoundTimeWarpChange(timeWarpSettings[propesedSetIndex]));
        }
    }


    public void timeWarpSetFromServer(boolean successfullyChanged, int setTimeWarpSpeed) {
        if (!successfullyChanged) {
            return;
        }

        for (short i = 0; i<timeWarpSettings.length; i++) {
            if (timeWarpSettings[i] == setTimeWarpSpeed) {
                currentTimeWarpSetting = i;
            }
        }
    }

    public short getTimeWarpSetting() {
        return this.currentTimeWarpSetting;
    }

    public void trackedOrbitUpdate(int shipID, Stack<String> oldAddress, Stack<String> newAddress, OrbitalElements orbitalElements) {
        if (Minecraft.getInstance().player.getId() == shipID) {
            if (oldAddress == null) {
                playerOrbit.setOrbitalElements(orbitalElements);
                currentPlanetSOIin = planets.playerJoinedOrbital(Minecraft.getInstance().player.getStringUUID(), newAddress, playerOrbit);
            }
            else {
                currentPlanetSOIin = planets.playerChangeOrbitalSOIs(Minecraft.getInstance().player.getStringUUID(), oldAddress, newAddress, orbitalElements);
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

    public Optional<PlanetaryBody> getCurrentPlanetSOIin() {
        if (currentPlanetSOIin != null) {
            return Optional.of(currentPlanetSOIin);
        }
        else  {
            return Optional.empty();
        }
    }

    public Optional<Orbit> getControllingBody() {
        if (controllingBody != null) {
            return  Optional.of(controllingBody);
        }
        return Optional.empty();
    }

    public void setControllingBody(ClientPlayerSpacecraftBody controllingBody) {
        this.controllingBody = controllingBody;
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

    public ModScreenManager getScreenManager() {
        return screenManager;
    }
}
