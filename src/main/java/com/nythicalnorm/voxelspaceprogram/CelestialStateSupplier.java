package com.nythicalnorm.voxelspaceprogram;

import com.nythicalnorm.voxelspaceprogram.gui.ModScreenManager;
import com.nythicalnorm.voxelspaceprogram.network.PacketHandler;
import com.nythicalnorm.voxelspaceprogram.network.ServerboundTimeWarpChange;
import com.nythicalnorm.voxelspaceprogram.solarsystem.Orbit;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.solarsystem.planet.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.planetshine.networking.ClientTimeHandler;
import com.nythicalnorm.voxelspaceprogram.planetshine.textures.PlanetTexManager;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.planetshine.renderers.SpaceObjRenderer;
import com.nythicalnorm.voxelspaceprogram.spacecraft.ClientPlayerSpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import net.minecraft.client.Minecraft;

import java.util.Optional;
import java.util.Stack;

//@OnlyIn(Dist.CLIENT)
public class CelestialStateSupplier {
    private static final int[] timeWarpSettings = new int[]{1, 10, 100, 1000, 10000, 100000, 1000000};
    private short currentTimeWarpSetting = 0;

    private ClientPlayerSpacecraftBody playerOrbit;
    private PlanetaryBody currentPlanetOn;
    private ClientPlayerSpacecraftBody controllingBody;

    private final PlanetsProvider planetProvider;
    private final ModScreenManager screenManager;
    private final PlanetTexManager planetTexManager;

    public CelestialStateSupplier(EntitySpacecraftBody playerDataFromServer, PlanetsProvider planetProvider) {
        playerOrbit = new ClientPlayerSpacecraftBody(playerDataFromServer, Minecraft.getInstance().player);
        this.planetProvider = planetProvider;
        SpaceObjRenderer.PopulateRenderPlanets(planetProvider);
        this.screenManager = new ModScreenManager();
        this.planetTexManager = new PlanetTexManager();
    }

    public void tick() {
        if (controllingBody != null && screenManager.isSpacecraftScreenOpen() && currentTimeWarpSetting == 0) {
            screenManager.getSpacecraftScreen().sendInputs(controllingBody);
        }
    }

    public void UpdateOrbitalBodies(float partialTick) {
        //clientSideTickTime = currentTime;
        planetProvider.UpdatePlanets(ClientTimeHandler.calculateCurrentTime());

        if (!weInSpaceDim()) {
            playerOrbit.setParent(null);
        }

        PlanetaryBody planet = planetProvider.getDimensionPlanet(Minecraft.getInstance().level.dimension());
        if (planet != null) {
            currentPlanetOn = planet;
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
            PacketHandler.sendToServer(new ServerboundTimeWarpChange(timeWarpSettings[propesedSetIndex]));
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
                planetProvider.playerJoinedOrbital(Minecraft.getInstance().player.getStringUUID(), newAddress, playerOrbit);
            }
            else {
                planetProvider.playerChangeOrbitalSOIs(Minecraft.getInstance().player.getStringUUID(), planetProvider.getOrbit(oldAddress), newAddress, orbitalElements);
            }
        }
    }

    public boolean doRender() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return false;
        }
        return planetProvider.isDimensionPlanet(mc.level.dimension()) || planetProvider.isDimensionSpace(mc.level.dimension());
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
        if (playerOrbit.getParent() != null && playerOrbit.getParent() instanceof PlanetaryBody body) {
            return Optional.of(body);
        } else if (currentPlanetOn != null) {
            return Optional.of(currentPlanetOn);
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

    public PlanetsProvider getPlanetsProvider() {
        return planetProvider;
    }

    public boolean weInSpaceDim() {
        return planetProvider.isDimensionSpace(Minecraft.getInstance().level.dimension());
    }

    public ModScreenManager getScreenManager() {
        return screenManager;
    }

    public PlanetTexManager getPlanetTexManager() {
        return planetTexManager;
    }
}
