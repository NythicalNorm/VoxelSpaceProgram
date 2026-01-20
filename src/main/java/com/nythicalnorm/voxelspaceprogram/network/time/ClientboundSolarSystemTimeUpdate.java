package com.nythicalnorm.voxelspaceprogram.network.time;

import com.nythicalnorm.voxelspaceprogram.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSolarSystemTimeUpdate {
    private final long currentTime;

    public ClientboundSolarSystemTimeUpdate(long currentTime) {
        this.currentTime = currentTime;
    }

    public ClientboundSolarSystemTimeUpdate(FriendlyByteBuf friendlyByteBuf) {
        this.currentTime = friendlyByteBuf.readLong();
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeLong(this.currentTime);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT ) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    ClientPacketHandler.UpdateTimeState(currentTime)));

            context.setPacketHandled(true);
        }
    }
}
