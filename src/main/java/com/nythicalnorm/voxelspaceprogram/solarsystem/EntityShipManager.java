package com.nythicalnorm.voxelspaceprogram.solarsystem;

import com.nythicalnorm.voxelspaceprogram.SolarSystem;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.spacecraft.spaceship.AbstractSpaceshipBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.spaceship.ServerSpaceshipBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.vs.ShipTeleporter;
import com.nythicalnorm.voxelspaceprogram.util.CelestialBodyUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.joml.*;
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.api.ValkyrienSkies;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class EntityShipManager {
    private final ShipTeleporter shipTeleporter;

    public EntityShipManager(SolarSystem solarSystem) {
        shipTeleporter = new ShipTeleporter(solarSystem.getSpaceLevel());
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

    public void onGameTick(SolarSystem solarSystem) {
        checkTeleportPlanetToSpace(solarSystem);
    }

    public void checkTeleportPlanetToSpace(SolarSystem solarSys) {
        ValkyrienSkies.api().getServerShipWorld(solarSys.getServer()).getLoadedShips().forEach(loadedServerShip -> {
            ResourceKey<Level> shipDimension = VSGameUtilsKt.getResourceKey(loadedServerShip.getChunkClaimDimension());
            CelestialBody celestialBody = solarSys.getPlanetsProvider().getDimensionPlanet(shipDimension);
            if (celestialBody != null) {
                Vector3dc currentPos = loadedServerShip.getTransform().getPositionInWorld();
                if (currentPos.y() >= ShipTeleporter.TELEPORT_Y_HEIGHT) {
                    shipTeleporter.teleportShipsAndEntities(loadedServerShip, celestialBody);
                }
            }
        });
    }
}
