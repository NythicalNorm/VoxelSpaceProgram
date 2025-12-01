package com.nythicalnorm.nythicalSpaceProgram.solarsystem;

import com.nythicalnorm.nythicalSpaceProgram.common.EntityBody;
import net.minecraft.server.level.ServerPlayer;

public class ServerPlayerOrbitalData extends EntityBody {
    private ServerPlayer playerEntity;
    private boolean isStableOrbit;
    private boolean isShipBound;

    public ServerPlayer getPlayerEntity() {
        return playerEntity;
    }

    public String getUUid() {
        return playerEntity.getStringUUID();
    }

    public boolean isStableOrbit() {
        return isStableOrbit;
    }
}
