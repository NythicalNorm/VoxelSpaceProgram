package com.nythicalnorm.voxelspaceprogram.network.time;

import com.nythicalnorm.voxelspaceprogram.SolarSystem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class ServerboundTimeWarpChange {
    private final int ProposedSetTimeWarpSpeed;

    public ServerboundTimeWarpChange(int proposedSetTimeWarpSpeed)
    {
        this.ProposedSetTimeWarpSpeed = proposedSetTimeWarpSpeed;
    }

    public ServerboundTimeWarpChange(FriendlyByteBuf friendlyByteBuf) {
        this.ProposedSetTimeWarpSpeed = friendlyByteBuf.readInt();
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(this.ProposedSetTimeWarpSpeed);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_SERVER ) {
            NetworkEvent.Context context = contextSupplier.get();
            SolarSystem.getInstance().ifPresent(solarSystem ->
                    context.enqueueWork(() -> solarSystem.ChangeTimeWarp(ProposedSetTimeWarpSpeed, contextSupplier.get().getSender())));
            context.setPacketHandled(true);
        }
    }
}
