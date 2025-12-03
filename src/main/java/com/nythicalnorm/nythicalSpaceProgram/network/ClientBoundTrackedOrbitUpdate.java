package com.nythicalnorm.nythicalSpaceProgram.network;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.orbit.OrbitalElements;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Stack;
import java.util.function.Supplier;

public class ClientBoundTrackedOrbitUpdate {
    private final int entityId;
    private final Stack<String> oldAddress;
    private final Stack<String> newAddress;
    private final OrbitalElements orbitalElements;

    public ClientBoundTrackedOrbitUpdate(Entity entity, Stack<String> oldAddress, Stack<String> newAddress, OrbitalElements elements) {
        this.entityId = entity.getId();
        this.oldAddress = oldAddress;
        this.newAddress = newAddress;
        this.orbitalElements = elements;
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(entityId);
        if (oldAddress != null) {
            friendlyByteBuf.writeBoolean(true);
            NetworkEncoders.writeStack(friendlyByteBuf, oldAddress);
        } else {
            friendlyByteBuf.writeBoolean(false);
        }
        NetworkEncoders.writeStack(friendlyByteBuf, newAddress);
        NetworkEncoders.writeOrbitalElements(friendlyByteBuf, orbitalElements);
    }

    public ClientBoundTrackedOrbitUpdate(FriendlyByteBuf friendlyByteBuf) {
        this.entityId = friendlyByteBuf.readInt();
        if (friendlyByteBuf.readBoolean()) {
            this.oldAddress = NetworkEncoders.readStack(friendlyByteBuf);
        } else {
            this.oldAddress = null;
        }
        this.newAddress = NetworkEncoders.readStack(friendlyByteBuf);
        this.orbitalElements = NetworkEncoders.readOrbitalElements(friendlyByteBuf);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT ) {
            NetworkEvent.Context context = contextSupplier.get();
            NythicalSpaceProgram.getCelestialStateSupplier().ifPresent(celestialStateSupplier -> {
                context.enqueueWork(() -> celestialStateSupplier.trackedOrbitUpdate(this.entityId, this.oldAddress, this.newAddress, this.orbitalElements));
            });
            context.setPacketHandled(true);
        }
    }
}
