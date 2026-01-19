package com.nythicalnorm.voxelspaceprogram.network;

import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundFocusedOrbitUpdate {
    private final OrbitId spacecraftID;
    private final OrbitId newParentID;
    private final OrbitalElements orbitalElements;

    public ClientboundFocusedOrbitUpdate(OrbitId spacecraftID, OrbitId newParentID, OrbitalElements elements) {
        this.spacecraftID = spacecraftID;
        this.newParentID = newParentID;
        this.orbitalElements = elements;
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        spacecraftID.encodeToBuffer(friendlyByteBuf);
        newParentID.encodeToBuffer(friendlyByteBuf);
        NetworkEncoders.writeOrbitalElements(friendlyByteBuf, orbitalElements);
    }

    public ClientboundFocusedOrbitUpdate(FriendlyByteBuf friendlyByteBuf) {
        this.spacecraftID = new OrbitId(friendlyByteBuf);
        this.newParentID = new OrbitId(friendlyByteBuf);
        this.orbitalElements = NetworkEncoders.readOrbitalElements(friendlyByteBuf);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT ) {
            NetworkEvent.Context context = contextSupplier.get();
            CelestialStateSupplier.getInstance().ifPresent(celestialStateSupplier -> {
                context.enqueueWork(() -> celestialStateSupplier.trackedOrbitUpdate(this.spacecraftID, this.newParentID, this.orbitalElements));
            });
            context.setPacketHandled(true);
        }
    }
}
