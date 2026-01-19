package com.nythicalnorm.voxelspaceprogram.spacecraft;

import com.nythicalnorm.voxelspaceprogram.SolarSystem;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import net.minecraft.server.level.ServerPlayer;
import org.joml.Quaternionf;

import java.util.UUID;

public class ServerPlayerSpacecraftBody extends AbstractPlayerSpacecraftBody {
    private boolean isShipBound;

    public ServerPlayerSpacecraftBody(ServerPlayer playerEntity, boolean isStableOrbit, boolean isShipBound, Quaternionf playerRot, OrbitalElements elements) {
        super(playerEntity);
        this.isStableOrbit = isStableOrbit;
        this.isShipBound = isShipBound;
        this.rotation = playerRot;
        this.orbitalElements = elements;
    }

    public UUID getUUid() {
        return player.getUUID();
    }

    @Override
    public void removeYourself() {
        SolarSystem.getInstance().ifPresent(solarSystem -> {
            solarSystem.getPlanetsProvider().getAllSpacecraftBodies().remove(this.id);
        });
        super.removeYourself();
    }
}
