package com.nythicalnorm.nythicalSpaceProgram.solarsystem;

import com.nythicalnorm.nythicalSpaceProgram.common.EntityBody;
import net.minecraft.server.level.ServerPlayer;

public class ServerPlayerOrbitalData extends EntityBody {
    private ServerPlayer playerEntity;
    private boolean isShipBound;

    public ServerPlayerOrbitalData(ServerPlayer playerEntity, boolean isStableOrbit, boolean isShipBound, OrbitalElements elements) {
        this.playerEntity = playerEntity;
        this.isStableOrbit = isStableOrbit;
        this.isShipBound = isShipBound;
        this.orbitalElements = elements;
    }

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
