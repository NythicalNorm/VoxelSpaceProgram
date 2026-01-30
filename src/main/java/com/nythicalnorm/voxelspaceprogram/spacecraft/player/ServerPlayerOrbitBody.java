package com.nythicalnorm.voxelspaceprogram.spacecraft.player;

import com.nythicalnorm.voxelspaceprogram.SolarSystem;

import java.util.UUID;

public class ServerPlayerOrbitBody extends AbstractPlayerOrbitBody {

    public ServerPlayerOrbitBody(PlayerOrbitBuilder playerSpacecraftBuilder) {
        super(playerSpacecraftBuilder);
    }

    public UUID getUUid() {
        return player.getUUID();
    }

    @Override
    public void removeYourself() {
        SolarSystem.getInstance().ifPresent(solarSystem ->
                solarSystem.getPlanetsProvider().getAllSpacecraftBodies().remove(this.id));
        super.removeYourself();
    }
}
