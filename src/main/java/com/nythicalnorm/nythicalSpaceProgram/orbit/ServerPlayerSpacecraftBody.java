package com.nythicalnorm.nythicalSpaceProgram.orbit;

import net.minecraft.server.level.ServerPlayer;
import org.joml.Quaternionf;

import java.util.UUID;

public class ServerPlayerSpacecraftBody extends EntityOrbitalBody {
    private ServerPlayer playerEntity;
    private UUID playerUUID;
    private boolean isShipBound;

    public ServerPlayerSpacecraftBody(ServerPlayer playerEntity, boolean isStableOrbit, boolean isShipBound, Quaternionf playerRot, OrbitalElements elements) {
        this.playerEntity = playerEntity;
        this.playerUUID = playerEntity.getUUID();
        this.isStableOrbit = isStableOrbit;
        this.isShipBound = isShipBound;
        this.orbitalElements = elements;
        this.rotation = playerRot;
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
