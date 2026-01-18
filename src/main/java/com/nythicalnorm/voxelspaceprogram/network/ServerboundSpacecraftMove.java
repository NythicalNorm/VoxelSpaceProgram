package com.nythicalnorm.voxelspaceprogram.network;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.spacecraft.SpacecraftControlState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundSpacecraftMove {
    // Need to add a currentTimeStamp so when the orbital elements are calculated on the server side it matches regardless of ping
    private final SpacecraftControlState spacecraftControlState;
    private final OrbitId spacecraftBodyID;

    public ServerboundSpacecraftMove(OrbitId controlledBody, SpacecraftControlState spacecraftControlState) {
        this.spacecraftControlState = spacecraftControlState;
        this.spacecraftBodyID = controlledBody;
    }

    public ServerboundSpacecraftMove(FriendlyByteBuf friendlyByteBuf) {
        this.spacecraftBodyID = new OrbitId(friendlyByteBuf);
        this.spacecraftControlState = new SpacecraftControlState(friendlyByteBuf);
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        spacecraftBodyID.encodeToBuffer(friendlyByteBuf);
        spacecraftControlState.encode(friendlyByteBuf);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER ) {
            NetworkEvent.Context context = contextSupplier.get();
            VoxelSpaceProgram.getSolarSystem().ifPresent(solarSystem -> {
                context.enqueueWork(() -> solarSystem.handleSpacecraftMove(context.getSender(), spacecraftBodyID, spacecraftControlState));
            });
            context.setPacketHandled(true);
        }
    }
}
