package com.nythicalnorm.voxelspaceprogram.spacecraft.player;

import java.util.UUID;

public class ServerPlayerOrbitBody extends AbstractPlayerOrbitBody {

    public ServerPlayerOrbitBody(PlayerOrbitBuilder playerSpacecraftBuilder) {
        super(playerSpacecraftBuilder);
    }

    public UUID getUUid() {
        return player.getUUID();
    }
}
