package com.nythicalnorm.nythicalSpaceProgram.network;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class ServerBoundTimeWarpChange {
    private final double ProposedSetTimeWarpSpeed;

    public ServerBoundTimeWarpChange(double proposedSetTimeWarpSpeed)
    {
        this.ProposedSetTimeWarpSpeed = proposedSetTimeWarpSpeed;
    }

    public ServerBoundTimeWarpChange(FriendlyByteBuf friendlyByteBuf) {
        this.ProposedSetTimeWarpSpeed = friendlyByteBuf.readDouble();
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeDouble(this.ProposedSetTimeWarpSpeed);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER ) {
            NetworkEvent.Context context = contextSupplier.get();
            NythicalSpaceProgram.getSolarSystem().ifPresent(solarSystem -> {
                context.enqueueWork(() -> solarSystem.ChangeTimeWarp(ProposedSetTimeWarpSpeed, contextSupplier.get().getSender()));
            });
            context.setPacketHandled(true);
        }
    }
}
