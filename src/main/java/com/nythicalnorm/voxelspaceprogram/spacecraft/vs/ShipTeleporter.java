package com.nythicalnorm.voxelspaceprogram.spacecraft.vs;

import com.nythicalnorm.voxelspaceprogram.solarsystem.EntityShipManager;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.ServerCelestialBody;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.world.ServerShipWorld;
import org.valkyrienskies.mod.api.ValkyrienSkies;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;

// got some code off genesis mod,
public class ShipTeleporter {
    public static final int TELEPORT_Y_HEIGHT = 1000;
    public static final double shipExtraRange = 20;
    private final ServerLevel spaceLevel;

    public ShipTeleporter(ServerLevel spaceLevel, EntityShipManager entityShipManager) {
        this.spaceLevel = spaceLevel;
    }

    public void teleportShipsAndEntities(LoadedServerShip ship, CelestialBody celestialBody, List<LoadedServerShip> alreadyTeleported, MinecraftServer server) {
        ServerLevel planetLevel = ((ServerCelestialBody) celestialBody).getLevel();
        ServerShipWorld serverShipWorld = ValkyrienSkies.api().getServerShipWorld(server);

        AABBdc shipWorldAABB = ship.getWorldAABB();
        AABBdc shipWorldInflated = new AABBd(shipWorldAABB.minX() - shipExtraRange, shipWorldAABB.minY() - shipExtraRange, shipWorldAABB.minZ() - shipExtraRange,
                shipWorldAABB.maxX() + shipExtraRange, shipWorldAABB.maxY() + shipExtraRange, shipWorldAABB.maxZ() + shipExtraRange);

        Iterable<LoadedServerShip> serverShipIterable = serverShipWorld.getLoadedShips().getIntersecting(shipWorldInflated, VSGameUtilsKt.getDimensionId(spaceLevel));

        AABB entityAABB = new AABB(shipWorldAABB.minX(), shipWorldAABB.maxY(), shipWorldAABB.maxZ(), shipWorldAABB.maxX(), shipWorldAABB.maxY(), shipWorldAABB.maxZ()).inflate(10d);
        List<Entity> allNonPassengerEntities = planetLevel.getEntities((Entity) null, entityAABB, (entity) -> !entity.isPassenger());

    }

    private void teleportEntityAndPassengers(Entity entity) {

    }
}
