package com.nythicalnorm.voxelspaceprogram.spacecraft;

import com.nythicalnorm.voxelspaceprogram.SolarSystem;

import java.util.UUID;

public class ServerPlayerSpacecraftBody extends AbstractPlayerSpacecraftBody {

    public ServerPlayerSpacecraftBody(PlayerSpacecraftBuilder playerSpacecraftBuilder) {
        super(playerSpacecraftBuilder);
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
