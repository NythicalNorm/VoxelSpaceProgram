package com.nythicalnorm.voxelspaceprogram.network.orbitaldata;

import com.nythicalnorm.voxelspaceprogram.network.ClientPacketHandler;
import com.nythicalnorm.voxelspaceprogram.network.NetworkEncoders;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
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
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    ClientPacketHandler.FocusedOrbitUpdate(this.spacecraftID, this.newParentID, this.orbitalElements)));
            context.setPacketHandled(true);
        }
    }
}
