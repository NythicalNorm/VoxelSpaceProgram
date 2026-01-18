package com.nythicalnorm.voxelspaceprogram.network;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundLoginSolarSystemState {
    private final EntitySpacecraftBody playerData;

    public ClientboundLoginSolarSystemState(EntitySpacecraftBody playerData) {
        this.playerData = playerData;
    }

    public ClientboundLoginSolarSystemState(FriendlyByteBuf friendlyByteBuf) {
        EntitySpacecraftBody playerDataIncoming = new EntitySpacecraftBody();

        if (friendlyByteBuf.readBoolean()) {
            Orbit entityOrbit = NetworkEncoders.readOrbitalBody(friendlyByteBuf);
            if (entityOrbit instanceof EntitySpacecraftBody spacecraftBody) {
                playerDataIncoming = spacecraftBody;
            }
        }

        this.playerData = playerDataIncoming;
    }

    public ClientboundLoginSolarSystemState() {
        this.playerData = null;
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        if (playerData != null) {
            friendlyByteBuf.writeBoolean(true);
            NetworkEncoders.writeOrbitalBody(friendlyByteBuf, playerData);
        } else {
            friendlyByteBuf.writeBoolean(false);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> VoxelSpaceProgram.startClient(playerData));
            context.setPacketHandled(true);
        }
    }
}
