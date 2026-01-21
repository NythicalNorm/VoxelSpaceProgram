package com.nythicalnorm.voxelspaceprogram;

import com.nythicalnorm.voxelspaceprogram.gui.ModScreenManager;
import com.nythicalnorm.voxelspaceprogram.network.PacketHandler;
import com.nythicalnorm.voxelspaceprogram.network.time.ServerboundTimeWarpChange;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetAccessor;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.planetshine.networking.ClientTimeHandler;
import com.nythicalnorm.voxelspaceprogram.planetshine.textures.PlanetTexManager;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.planetshine.renderers.SpaceObjRenderer;
import com.nythicalnorm.voxelspaceprogram.spacecraft.ClientPlayerSpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.util.Stage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class CelestialStateSupplier extends Stage {
    private static CelestialStateSupplier instance;
    private final Minecraft minecraft;

    private final ClientPlayerSpacecraftBody playerOrbit;
    private CelestialBody currentPlanetOn;
    private ClientPlayerSpacecraftBody controllingBody;

    private final ModScreenManager screenManager;
    private final PlanetTexManager planetTexManager;

    public CelestialStateSupplier(ClientPlayerSpacecraftBody playerDataFromServer, PlanetsProvider planetProvider) {
        super(planetProvider);
        instance = this;
        minecraft = Minecraft.getInstance();
        playerOrbit = playerDataFromServer;
        SpaceObjRenderer.PopulateRenderPlanets(planetProvider);
        this.screenManager = new ModScreenManager();
        this.planetTexManager = new PlanetTexManager();
        if (minecraft.level != null) {
            onClientLevelLoad(minecraft.level);
        }

        if (VoxelSpaceProgram.getAnyPlanetsProvider() == null) {
            VoxelSpaceProgram.setPlanetsProvider(planetProvider);
        }
    }

    public static Optional<CelestialStateSupplier> getInstance() {
        if (instance != null) {
            return Optional.of(instance);
        }
        return Optional.empty();
    }

    public float getSunAngle() {
        return playerOrbit.getSunAngle();
    }

    public static void close() {
        VoxelSpaceProgram.exitWorld();
        instance = null;
    }

    public void tick() {
        if (controllingBody != null && screenManager.isSpacecraftScreenOpen() && getCurrentTimeWarpSetting() == 0) {
            screenManager.getSpacecraftScreen().sendInputs(controllingBody);
        }
    }

    public void onClientLevelLoad(ClientLevel clientLevel) {
        CelestialBody celestialBody = planetsProvider.getDimensionPlanet(clientLevel.dimension());
        if (celestialBody != null) {
            ((PlanetAccessor) clientLevel).setCelestialBody(celestialBody);
            currentPlanetOn = celestialBody;
        } else {
            currentPlanetOn = null;
        }
    }

    public void UpdateOrbitalBodies(float partialTick) {
        this.currentTime = ClientTimeHandler.calculateCurrentTime(currentTime);
        planetsProvider.UpdatePlanets(currentTime);

        if (!weInSpaceDim()) {
            playerOrbit.setParent(null);
        }

        if (currentPlanetOn != null) {
            playerOrbit.updatePlayerPosRot(minecraft.player, currentPlanetOn);
        }
    }

    public ClientPlayerSpacecraftBody getPlayerOrbit() {
        return playerOrbit;
    }

    public void TryChangeTimeWarp(boolean doInc) {
        int propesedSetIndex = getCurrentTimeWarpSetting();
        propesedSetIndex = doInc ? ++propesedSetIndex : --propesedSetIndex;

        if (propesedSetIndex >= 0 && propesedSetIndex < timeWarpSettings.size()) {
            PacketHandler.sendToServer(new ServerboundTimeWarpChange(timeWarpSettings.get(propesedSetIndex)));
        }
    }

    public void timeWarpSetFromServer(boolean successfullyChanged, long setTimeWarpSpeed) {
        if (successfullyChanged) {
            setTimePassPerTick(setTimeWarpSpeed);
        }
    }

    public void trackedOrbitUpdate(OrbitId spacecraftID, OrbitId newParentID, OrbitalElements orbitalElements) {
        if (this.playerOrbit.getOrbitId().equals(spacecraftID)) {
            //temporary setting the rotation to default
            this.playerOrbit.setRotation(new Quaternionf());
            planetsProvider.playerChangeOrbitalSOIs(this.playerOrbit, newParentID, orbitalElements);
        }
    }

    public boolean doRender() {
        if (minecraft.level == null) {
            return false;
        }
        PlanetAccessor planetAccessor = (PlanetAccessor) minecraft.level;
        return planetAccessor.isPlanet() || planetsProvider.isDimensionSpace(minecraft.level.dimension());
    }

    public Optional<CelestialBody> getCurrentPlanet() {
        if (currentPlanetOn != null) {
            return Optional.of(currentPlanetOn);
        }
        else  {
            return Optional.empty();
        }
    }

    public Optional<CelestialBody> getCurrentPlanetSOIin() {
        if (playerOrbit.getParent() != null && playerOrbit.getParent() instanceof PlanetaryBody body) {
            return Optional.of(body);
        } else if (currentPlanetOn != null) {
            return Optional.of(currentPlanetOn);
        }
        else  {
            return Optional.empty();
        }
    }

    public Optional<OrbitalBody> getControllingBody() {
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

    public boolean weInSpaceDim() {
        if (minecraft.level != null) {
            return planetsProvider.isDimensionSpace(minecraft.level.dimension());
        } else {
            return false;
        }
    }

    public ModScreenManager getScreenManager() {
        return screenManager;
    }

    public PlanetTexManager getPlanetTexManager() {
        return planetTexManager;
    }
}
