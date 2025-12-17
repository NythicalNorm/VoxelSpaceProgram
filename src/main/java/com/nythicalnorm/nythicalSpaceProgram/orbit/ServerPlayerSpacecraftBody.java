package com.nythicalnorm.nythicalSpaceProgram.orbit;

import net.minecraft.server.level.ServerPlayer;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.UUID;

public class ServerPlayerSpacecraftBody extends PlayerSpacecraftBody {
    private UUID playerUUID;
    private boolean isShipBound;

    public ServerPlayerSpacecraftBody(ServerPlayer playerEntity, boolean isStableOrbit, boolean isShipBound, Quaternionf playerRot, OrbitalElements elements) {
        this.player = playerEntity;
        this.playerUUID = playerEntity.getUUID();
        this.isStableOrbit = isStableOrbit;
        this.isShipBound = isShipBound;
        this.orbitalElements = elements;
        this.rotation = playerRot;
        this.angularVelocity = new Vector3f();
    }

    public String getUUid() {
        return player.getStringUUID();
    }

    public boolean isStableOrbit() {
        return isStableOrbit;
    }
}
