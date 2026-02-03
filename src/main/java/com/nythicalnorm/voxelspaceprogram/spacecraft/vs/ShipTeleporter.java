package com.nythicalnorm.voxelspaceprogram.spacecraft.vs;

import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.ServerCelestialBody;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.joml.primitives.AABBdc;
import org.valkyrienskies.core.api.ships.LoadedServerShip;

import java.util.List;

// got some code off genesis mod,
public class ShipTeleporter {
    public static final int TELEPORT_Y_HEIGHT = 1000;
    private final ServerLevel spaceLevel;

    public ShipTeleporter(ServerLevel spaceLevel) {
        this.spaceLevel = spaceLevel;
    }

    public void teleportShipsAndEntities(LoadedServerShip ship, CelestialBody celestialBody, List<LoadedServerShip> alreadyTeleported) {
        ServerLevel planetLevel = ((ServerCelestialBody) celestialBody).getLevel();
        AABBdc shipWorldAABB = ship.getWorldAABB();
        AABB mcAABB = new AABB(shipWorldAABB.minX(), shipWorldAABB.maxY(), shipWorldAABB.maxZ(), shipWorldAABB.maxX(), shipWorldAABB.maxY(), shipWorldAABB.maxZ());
        List<Entity> allShipEntities = planetLevel.getEntities(null, mcAABB);
    }
}
