package com.nythicalnorm.voxelspaceprogram.solarsystem;

import com.nythicalnorm.voxelspaceprogram.SolarSystem;
import com.nythicalnorm.voxelspaceprogram.network.PacketHandler;
import com.nythicalnorm.voxelspaceprogram.network.orbitaldata.ClientboundOrbitRemove;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.ServerCelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntityOrbitBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.player.AbstractPlayerOrbitBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.spaceship.AbstractSpaceshipBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.spaceship.ServerSpaceshipBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.vs.ShipTeleporter;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import com.nythicalnorm.voxelspaceprogram.util.CelestialBodyUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import org.joml.*;
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.api.ValkyrienSkies;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class EntityShipManager {

    private final SolarSystem solarSystem;
    private final ShipTeleporter shipTeleporter;
    private final Map<OrbitId, OrbitHostSpace> allHostSpaces;

    private static final int HOST_SPACE_GAP_SIZE = 16000;
    private static final int HOST_SPACE_DIAMETER = 8000;
    private static final double teleportToGroundHeight = 1000d;

    public EntityShipManager(SolarSystem solarSystem, Map<OrbitId, OrbitHostSpace> allHostSpaces) {
        this.solarSystem = solarSystem;
        shipTeleporter = new ShipTeleporter(solarSystem.getSpaceLevel(), this);
        this.allHostSpaces = allHostSpaces;
    }

    public OrbitHostSpace getOrCreateHostSpace(EntityOrbitBody entityOrbitBody){
        OrbitHostSpace hostSpace = allHostSpaces.get(entityOrbitBody.getOrbitId());
        if (hostSpace == null) {
            Vector3d posNew = genNewHostSpaceLoc(allHostSpaces.size());
            hostSpace = new OrbitHostSpace(entityOrbitBody.getOrbitId(), posNew, entityOrbitBody);
            allHostSpaces.put(entityOrbitBody.getOrbitId(), hostSpace);
        }
        return hostSpace;
    }

    private Vector3d genNewHostSpaceLoc(int alreadyGenerated) {
        int totalXDist = 56_000_000;
        int startPosX = -28_000_000;
        int startPosZ = 0;
        int posPerXSlice = totalXDist / HOST_SPACE_GAP_SIZE;
        alreadyGenerated = alreadyGenerated + (posPerXSlice / 2); // beginning at origin in the first slice, cause its probably less bugs at low pos.

        double x = startPosX + ((alreadyGenerated % totalXDist) * HOST_SPACE_GAP_SIZE);
        double z = startPosZ + (((double) alreadyGenerated / posPerXSlice) * HOST_SPACE_GAP_SIZE);
        return new Vector3d(x, 128d, z);
    }

    public void onGameTick() {
        checkShipTeleportToSpace();
        checkEntityTeleportToPlanet();
    }

    public void checkShipTeleportToSpace() {
        List<LoadedServerShip> alreadyTeleported = new ArrayList<>();
        ValkyrienSkies.api().getServerShipWorld(solarSystem.getServer()).getLoadedShips().forEach(loadedServerShip -> {
            ResourceKey<Level> shipDimension = VSGameUtilsKt.getResourceKey(loadedServerShip.getChunkClaimDimension());
            CelestialBody celestialBody = solarSystem.getPlanetsProvider().getDimensionPlanet(shipDimension);

            if (celestialBody != null) {
                Vector3dc currentPos = loadedServerShip.getTransform().getPositionInWorld();
                if (currentPos.y() >= ShipTeleporter.TELEPORT_Y_HEIGHT && !alreadyTeleported.contains(loadedServerShip)) {
                    shipTeleporter.teleportShipsAndEntities(loadedServerShip, celestialBody, alreadyTeleported, solarSystem.getServer());
                }
            }
        });
    }

    private void checkEntityTeleportToPlanet() {
        solarSystem.getPlanetsProvider().getAllSpacecraftBodies().values().forEach(entityOrbitBody -> {
            if (entityOrbitBody.isHostOfItsSpace() && entityOrbitBody.getAltitude() < teleportToGroundHeight && entityOrbitBody.getParent() instanceof CelestialBody celestialBody) {
                ServerLevel planetLevel = ((ServerCelestialBody)celestialBody).getLevel();
                if (planetLevel != null) {
                    Vector2d pos = Calcs.vectorToPlanetDimPos(entityOrbitBody.getRelativePos(), celestialBody.getRadius(), celestialBody.getRotation());
                    if (entityOrbitBody instanceof AbstractPlayerOrbitBody playerOrbitBody) {
                        teleportEntity(playerOrbitBody.getPlayerEntity(), planetLevel, pos);
                        solarSystem.getPlanetsProvider().entityRemoveOrbital(entityOrbitBody);
                        PacketHandler.sendToAllClients(new ClientboundOrbitRemove(playerOrbitBody.getOrbitId()));
                    }
                }
            }
        });
    }

    public void teleportEntity(Entity entity, ServerLevel level, Vector3d position) {
        teleportEntity(entity, level, position.x, position.y, position.z);
    }

    public void teleportEntity(Entity entity, ServerLevel level, Vector2d position) {
        teleportEntity(entity, level, position.x, 1000d, position.y);
    }

    public void teleportEntity(Entity entity, ServerLevel level, double x, double y, double z) {
        entity.teleportTo(level, x, y, z, EnumSet.noneOf(RelativeMovement.class), -85f, 0f);
    }

    public static ServerSpaceshipBody planetShipToSpaceShipBodyBuilder(ServerShip ship, CelestialBody celestialBody) {
        BodyKinematics bodyKinematics = ship.getKinematics();
        AbstractSpaceshipBody.ShipOrbitBuilder builder = new AbstractSpaceshipBody.ShipOrbitBuilder();
        builder.setShip(ship);

        Vector3d relativesShipPosition = CelestialBodyUtils.getRelativePositon(bodyKinematics.getTransform().getPosition(), celestialBody);
        builder.setRelativeOrbitalPos(relativesShipPosition);

        Quaterniond rotationDifference = CelestialBodyUtils.getSpaceRotationFromPlanetPos(relativesShipPosition, celestialBody);
        Quaterniond shipNewRot = new Quaterniond();
        bodyKinematics.getRotation().mul(rotationDifference, shipNewRot);
        builder.setRotation(shipNewRot);
        // need to take into account the planets rotational velocity that is also transferred to the ship, earth moving at 1000 m/s at the equator etc...
        Vector3d velocity = new Vector3d(ship.getVelocity()).rotate(rotationDifference);
        builder.setRelativeVelocity(velocity);
        Vector3d absoluteShipPosition = celestialBody.getAbsolutePos().add(relativesShipPosition);
        builder.setAbsoluteOrbitalPos(absoluteShipPosition);

        builder.setParent(celestialBody);
        builder.setStableOrbit(false);
        builder.setOrbitalElements(new OrbitalElements(relativesShipPosition, velocity, SolarSystem.get().getCurrentTime(), celestialBody.getMass()));

        return (ServerSpaceshipBody) builder.build();
    }
}
